#!/usr/bin/env bash
# ==============================================================================
# setup-eks-epharmacy.sh
# Creates an EKS cluster, wires up IAM (OIDC / Pod Identity), installs the
# AWS Load Balancer Controller + EBS CSI driver, and deploys the microservice
# Helm chart.
#
# PREREQUISITES (edit these before running):
#   - cluster.yaml            -> eksctl cluster config (node group, VPC, etc.)
#   - aws/eks/alb-controller/iam_policy.json -> IAM policy JSON for ALB controller
#   - A Helm chart directory for the "epharmacy" app (cd into it where noted)
#   - awscli, eksctl, kubectl, helm already installed and configured
# ==============================================================================
set -euo pipefail

# ---- Variables (EDIT THESE) -------------------------------------------------
CLUSTER_NAME="epharmacy-eks"
REGION="ap-south-1"
ACCOUNT_ID="960233595057"          # your AWS account ID
NAMESPACE="epharmacy"
NODEGROUP_NAME="epharmacy-ng"
CHART_DIR="/mnt/c/microservice/helm/epharmacy"      # helm chart root
EKS_DIR="/mnt/c/microservice/helm/epharmacy/aws/eks" # trust-policy.json location

# ==============================================================================
# 1. CLUSTER CREATION
# ==============================================================================

# Create the EKS cluster (nodes, VPC, networking) from your eksctl config file
eksctl create cluster -f cluster.yaml

# Give the control plane a few seconds to settle before querying it
sleep 10s

# Confirm worker nodes joined the cluster
kubectl get nodes

# Create the Kubernetes namespace that the microservices will run in
kubectl create namespace "$NAMESPACE"

# Confirm the namespace was created
kubectl get namespaces

# ==============================================================================
# 2. ENABLE IAM OIDC (needed so K8s service accounts can assume IAM roles)
# ==============================================================================

# Link an IAM OIDC identity provider to the cluster (required for IRSA)
eksctl utils associate-iam-oidc-provider \
  --cluster "$CLUSTER_NAME" \
  --region "$REGION" \
  --approve

# Fetch the cluster's OIDC issuer URL (you'll need this ID for trust policies)
aws eks describe-cluster \
  --name "$CLUSTER_NAME" \
  --region "$REGION" \
  --query "cluster.identity.oidc.issuer" \
  --output text

# ==============================================================================
# 3. AWS LOAD BALANCER CONTROLLER - IAM POLICY + ROLE (IRSA method)
# ==============================================================================

cd "$EKS_DIR"

# Create the IAM policy the controller needs to manage ALBs/NLBs
aws iam create-policy \
  --policy-name AWSLoadBalancerControllerIAMPolicy \
  --policy-document file://aws/eks/alb-controller/iam_policy.json

# Let eksctl create the IAM role + Kubernetes service account + trust policy
# in one step (preferred over manually writing trust-policy.json)
eksctl create iamserviceaccount \
  --cluster "$CLUSTER_NAME" \
  --region "$REGION" \
  --namespace kube-system \
  --name aws-loadbalancer-controller \
  --attach-policy-arn "arn:aws:iam::${ACCOUNT_ID}:policy/AWSLoadBalancerControllerIAMPolicy" \
  --override-existing-serviceaccounts \
  --approve

# --- Dynamic trust-policy.json generation (manual alternative to eksctl) ----
# Only needed if you created AWSLoadBalancerControllerRole by hand instead of
# letting eksctl manage it above. Pulls the OIDC provider ID live from the
# cluster so it never goes stale after a cluster rebuild.

# Fetch the cluster's OIDC issuer URL
OIDC_ISSUER=$(aws eks describe-cluster \
  --name "$CLUSTER_NAME" \
  --region "$REGION" \
  --query "cluster.identity.oidc.issuer" \
  --output text)

# Extract just the unique OIDC provider ID from the issuer URL
OIDC_ID=$(echo "$OIDC_ISSUER" | awk -F'/id/' '{print $2}')

# Build the full OIDC provider path (region + ID) used in the trust policy
OIDC_PROVIDER="oidc.eks.${REGION}.amazonaws.com/id/${OIDC_ID}"

# Generate trust-policy.json with the live OIDC provider values substituted in
cat > trust-policy.json <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::${ACCOUNT_ID}:oidc-provider/${OIDC_PROVIDER}"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "${OIDC_PROVIDER}:sub": "system:serviceaccount:kube-system:aws-load-balancer-controller",
          "${OIDC_PROVIDER}:aud": "sts.amazonaws.com"
        }
      }
    }
  ]
}
EOF

# Apply the generated trust policy to the role
aws iam update-assume-role-policy \
  --role-name AWSLoadBalancerControllerRole \
  --policy-document file://trust-policy.json

# Verify the trust policy attached to the role
aws iam get-role \
  --role-name AWSLoadBalancerControllerRole \
  --query "Role.AssumeRolePolicyDocument"

# Confirm the service account exists in kube-system
kubectl get serviceaccount aws-loadbalancer-controller -n kube-system -o yaml

# ==============================================================================
# 4. INSTALL AWS LOAD BALANCER CONTROLLER (via Helm)
# ==============================================================================

# Sanity-check Helm is installed
helm version

# Add the official EKS charts repo
helm repo add eks https://aws.github.io/eks-charts

# Refresh local chart index
helm repo update

# Install/upgrade the controller, binding it to the IRSA service account
helm upgrade --install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName="$CLUSTER_NAME" \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-loadbalancer-controller \
  --set region="$REGION" \
  --set vpcId=$(aws eks describe-cluster \
    --name "$CLUSTER_NAME" \
    --region "$REGION" \
    --query "cluster.resourcesVpcConfig.vpcId" \
    --output text)

# Confirm the controller pod is running
kubectl get pods -n kube-system | grep aws-load-balancer

# Confirm the controller deployment is using the right service account
kubectl get deployment aws-load-balancer-controller \
  -n kube-system \
  -o jsonpath='{.spec.template.spec.serviceAccountName}'

# ==============================================================================
# 5. EBS CSI DRIVER - IAM ROLE (Pod Identity method) + ADDON
# ==============================================================================

# Create the IAM role that the EBS CSI driver pods will assume via Pod Identity
aws iam create-role \
  --role-name AmazonEKS_EBS_CSI_DriverRole \
  --assume-role-policy-document '{
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Principal": { "Service": "pods.eks.amazonaws.com" },
        "Action": ["sts:AssumeRole", "sts:TagSession"]
      }
    ]
  }'

# Attach AWS's managed policy that grants EBS volume permissions
aws iam attach-role-policy \
  --role-name AmazonEKS_EBS_CSI_DriverRole \
  --policy-arn arn:aws:iam::aws:policy/AmazonEBSCSIDriverPolicyV2

# Verify the policy attached correctly
aws iam list-attached-role-policies \
  --role-name AmazonEKS_EBS_CSI_DriverRole

# Link the IAM role to the ebs-csi-controller-sa service account via Pod Identity
aws eks create-pod-identity-association \
  --cluster-name "$CLUSTER_NAME" \
  --region "$REGION" \
  --role-arn "arn:aws:iam::${ACCOUNT_ID}:role/AmazonEKS_EBS_CSI_DriverRole" \
  --namespace kube-system \
  --service-account ebs-csi-controller-sa

# Install the EBS CSI driver addon, wired to the Pod Identity association
aws eks create-addon \
  --cluster-name "$CLUSTER_NAME" \
  --region "$REGION" \
  --addon-name aws-ebs-csi-driver \
  --pod-identity-associations "[{\"serviceAccount\":\"ebs-csi-controller-sa\",\"roleArn\":\"arn:aws:iam::${ACCOUNT_ID}:role/AmazonEKS_EBS_CSI_DriverRole\"}]"

# Confirm the addon is ACTIVE and healthy
aws eks describe-addon \
  --cluster-name "$CLUSTER_NAME" \
  --region "$REGION" \
  --addon-name aws-ebs-csi-driver \
  --query 'addon.{status:status,health:health,version:addonVersion}' \
  --output json

# Confirm the EBS CSI driver pods are running
kubectl get pods -n kube-system | grep ebs

# List all installed addons on the cluster
aws eks list-addons \
  --cluster-name "$CLUSTER_NAME" \
  --region "$REGION"

# ==============================================================================
# 6. DEPLOY THE MICROSERVICE (Helm chart)
# ==============================================================================

cd "$CHART_DIR"

# Preview the chart's default configurable values
helm show values .

# Lint the chart for syntax/schema errors before installing
helm lint .

# Confirm kubectl is pointed at the right cluster context
kubectl config current-context

# Install the microservice chart into the epharmacy namespace
helm install epharmacy . -n "$NAMESPACE"

# Check the release status
helm status epharmacy -n "$NAMESPACE"

# Confirm persistent volume claims were bound (EBS CSI working)
kubectl get pvc -n "$NAMESPACE"

# Confirm an Ingress/ALB was created for the service (ALB controller working)
kubectl get ingress -n "$NAMESPACE"

# ==============================================================================
# 7. SCALING / DAY-2 OPERATIONS (run as needed, not part of first-time setup)
# ==============================================================================

# Check how many pods each node can schedule
kubectl get nodes -o custom-columns=NAME:.metadata.name,PODS:.status.allocatable.pods

# Scale the worker node group up/down
eksctl scale nodegroup \
  --cluster "$CLUSTER_NAME" \
  --region "$REGION" \
  --name "$NODEGROUP_NAME" \
  --nodes 3

# Push chart/config changes to the running release
helm upgrade epharmacy . -n "$NAMESPACE"

# ==============================================================================
# 8. TEARDOWN (only when decommissioning the whole environment)
# ==============================================================================

# Remove a stale Pod Identity association (needs its association ID)
# aws eks delete-pod-identity-association \
#   --cluster-name "$CLUSTER_NAME" \
#   --region "$REGION" \
#   --association-id <ASSOCIATION_ID>

# Delete the entire EKS cluster and its resources
# eksctl delete cluster \
#   --name "$CLUSTER_NAME" \
#   --region "$REGION"

# Confirm the cluster is gone
# aws eks list-clusters --region "$REGION"
