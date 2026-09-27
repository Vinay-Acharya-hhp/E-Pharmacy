eksctl create cluster -f cluster.yaml
sleep 10s
kubectl get nodes
kubectl create namespace epharmacy
kubectl get namespaces

eksctl utils associate-iam-oidc-provider \
  --cluster epharmacy-eks \
  --region ap-south-1 \
  --approve


aws eks describe-cluster \
  --name epharmacy-eks \
  --region ap-south-1 \
  --query "cluster.identity.oidc.issuer" \
  --output text

cd /mnt/c/microservice/helm/epharmacy/aws/eks

nano trust-policy.json


{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::960233595057:oidc-provider/oidc.eks.ap-south-1.amazonaws.com/id/67B1377BE1F0702ACD0DEB9CA5BB4109"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "oidc.eks.ap-south-1.amazonaws.com/id/67B1377BE1F0702ACD0DEB9CA5BB4109:sub": "system:serviceaccount:kube-system:aws-load-balancer-controller",
          "oidc.eks.ap-south-1.amazonaws.com/id/67B1377BE1F0702ACD0DEB9CA5BB4109:aud": "sts.amazonaws.com"
        }
      }
    }
  ]
}




aws iam update-assume-role-policy \
  --role-name AWSLoadBalancerControllerRole \
  --policy-document file://trust-policy.json


aws iam get-role \
  --role-name AWSLoadBalancerControllerRole \
  --query "Role.AssumeRolePolicyDocument"


kubectl create serviceaccount aws-loadbalancer-controller -n kube-system

kubectl get serviceaccount aws-loadbalancer-controller -n kube-system


kubectl annotate serviceaccount aws-loadbalancer-controller \
  -n kube-system \
  eks.amazonaws.com/role-arn=arn:aws:iam::960233595057:role/AWSLoadBalancerControllerRole

kubectl get serviceaccount aws-loadbalancer-controller \
  -n kube-system \
  -o yaml

annotations:
  eks.amazonaws.com/role-arn: arn:aws:iam::960233595057:role/AWSLoadBalancerControllerRole

  helm version

  helm repo add eks https://aws.github.io/eks-charts

  helm repo update

  helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=epharmacy-eks \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-loadbalancer-controller \
  --set region=ap-south-1 \
  --set vpcId=$(aws eks describe-cluster \
    --name epharmacy-eks \
    --region ap-south-1 \
    --query "cluster.resourcesVpcConfig.vpcId" \
    --output text)


aws eks create-addon \
  --cluster-name epharmacy-eks \
  --region ap-south-1 \
  --addon-name aws-ebs-csi-driver

aws eks describe-addon \
  --cluster-name epharmacy-eks \
  --region ap-south-1 \
  --addon-name aws-ebs-csi-driver \
  --query "addon.status"

kubectl get ingress -n epharmacy

  kubectl get pods -n kube-system | grep aws-load-balancer


  kubectl get deployment aws-load-balancer-controller \
  -n kube-system \
  -o jsonpath='{.spec.template.spec.serviceAccountName}'

  helm show values .

  helm lint .

  kubectl config current-context

  helm install epharmacy . -n epharmacy

  helm status epharmacy -n epharmacy

  kubectl get pvc -n epharmacy

  helm upgrade epharmacy . -n epharmacy

  aws eks list-addons \
  --cluster-name epharmacy-eks \
  --region ap-south-1


  aws iam create-role \
  --role-name AmazonEKS_EBS_CSI_DriverRole \
  --assume-role-policy-document '{
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Principal": {
          "Service": "pods.eks.amazonaws.com"
        },
        "Action": [
          "sts:AssumeRole",
          "sts:TagSession"
        ]
      }
    ]
  }'


  aws iam attach-role-policy \
  --role-name AmazonEKS_EBS_CSI_DriverRole \
  --policy-arn arn:aws:iam::aws:policy/AmazonEBSCSIDriverPolicyV2


  aws iam list-attached-role-policies \
  --role-name AmazonEKS_EBS_CSI_DriverRole



  aws eks create-pod-identity-association \
  --cluster-name epharmacy-eks \
  --region ap-south-1 \
  --role-arn arn:aws:iam::960233595057:role/AmazonEKS_EBS_CSI_DriverRole \
  --namespace kube-system \
  --service-account ebs-csi-controller-sa


  kubectl get pods -n kube-system | grep ebs

  aws eks update-addon \
  --cluster-name epharmacy-eks \
  --region ap-south-1 \
  --addon-name aws-ebs-csi-driver \
  --pod-identity-associations '[{"serviceAccount":"ebs-csi-controller-sa","roleArn":"arn:aws:iam::960233595057:role/AmazonEKS_EBS_CSI_DriverRole"}]'


  aws eks describe-addon \
  --cluster-name epharmacy-eks \
  --region ap-south-1 \
  --addon-name aws-ebs-csi-driver \
  --query 'addon.{status:status,health:health,version:addonVersion}' \
  --output json

  aws eks delete-pod-identity-association \
  --cluster-name epharmacy-eks \
  --region ap-south-1 \
  --association-id a-cpydiklb2sntwdw6c

  aws eks create-addon \
  --cluster-name epharmacy-eks \
  --region ap-south-1 \
  --addon-name aws-ebs-csi-driver \
  --pod-identity-associations '[{"serviceAccount":"ebs-csi-controller-sa","roleArn":"arn:aws:iam::960233595057:role/AmazonEKS_EBS_CSI_DriverRole"}]'

  kubectl get nodes -o custom-columns=NAME:.metadata.name,PODS:.status.allocatable.pods


  eksctl scale nodegroup \
  --cluster epharmacy-eks \
  --region ap-south-1 \
  --name epharmacy-ng \
  --nodes 3

  aws iam update-assume-role-policy \
  --role-name AWSLoadBalancerControllerRole \
  --policy-document '{
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Principal": {
          "Federated": "arn:aws:iam::960233595057:oidc-provider/oidc.eks.ap-south-1.amazonaws.com/id/67B1377BE1F0702ACD0DEB9CA5BB4109"
        },
        "Action": "sts:AssumeRoleWithWebIdentity",
        "Condition": {
          "StringEquals": {
            "oidc.eks.ap-south-1.amazonaws.com/id/67B1377BE1F0702ACD0DEB9CA5BB4109:aud": "sts.amazonaws.com",
            "oidc.eks.ap-south-1.amazonaws.com/id/67B1377BE1F0702ACD0DEB9CA5BB4109:sub": "system:serviceaccount:kube-system:aws-loadbalancer-controller"
          }
        }
      }
    ]
  }'

  aws iam create-policy \
  --policy-name AWSLoadBalancerControllerIAMPolicy \
  --policy-document file://aws/eks/alb-controller/iam_policy.json


  eksctl create iamserviceaccount \
  --cluster epharmacy-eks \
  --region ap-south-1 \
  --namespace kube-system \
  --name aws-loadbalancer-controller \
  --attach-policy-arn arn:aws:iam::YOUR_ACCOUNT_ID:policy/AWSLoadBalancerControllerIAMPolicy \
  --override-existing-serviceaccounts \
  --approve

  helm upgrade --install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=epharmacy-eks \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-loadbalancer-controller \
  --set region=ap-south-1 \
  --set vpcId=YOUR_VPC_ID

  eksctl delete cluster \
  --name epharmacy-eks \
  --region ap-south-1

  aws eks list-clusters --region ap-south-1
