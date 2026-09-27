```bash
#!/bin/bash

# ============================================
# E-Pharmacy EKS Cluster Creation
# ============================================

CLUSTER_NAME="epharmacy-eks"
AWS_REGION="ap-south-1"
CLUSTER_CONFIG="cluster.yaml"

echo "============================================"
echo " E-Pharmacy EKS Cluster Creation"
echo "============================================"

echo ""
echo "Cluster : $CLUSTER_NAME"
echo "Region  : $AWS_REGION"
echo "Config  : $CLUSTER_CONFIG"
echo ""

# --------------------------------------------
# 1. Check AWS authentication
# --------------------------------------------

echo "Checking AWS account..."

aws sts get-caller-identity

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: AWS authentication failed."
    echo "Check your AWS CLI configuration."
    exit 1
fi

echo ""
echo "AWS authentication successful."

# --------------------------------------------
# 2. Check cluster.yaml
# --------------------------------------------

if [ ! -f "$CLUSTER_CONFIG" ]; then
    echo ""
    echo "ERROR: $CLUSTER_CONFIG not found."
    echo ""
    echo "Run this script from:"
    echo "/mnt/c/microservice/helm/epharmacy/aws/eks"
    exit 1
fi

echo ""
echo "Found cluster configuration."

# --------------------------------------------
# 3. Check whether cluster already exists
# --------------------------------------------

echo ""
echo "Checking whether EKS cluster already exists..."

aws eks describe-cluster \
    --name "$CLUSTER_NAME" \
    --region "$AWS_REGION" \
    >/dev/null 2>&1

if [ $? -eq 0 ]; then
    echo ""
    echo "EKS cluster '$CLUSTER_NAME' already exists."
    echo "Nothing to create."
    exit 0
fi

# --------------------------------------------
# 4. Create EKS cluster
# --------------------------------------------

echo ""
echo "Creating EKS cluster..."
echo "This may take several minutes."
echo ""

eksctl create cluster -f "$CLUSTER_CONFIG"

if [ $? -ne 0 ]; then
    echo ""
    echo "============================================"
    echo " ERROR: EKS cluster creation failed."
    echo "============================================"
    exit 1
fi

# --------------------------------------------
# 5. Verify cluster
# --------------------------------------------

echo ""
echo "============================================"
echo " EKS cluster created successfully."
echo "============================================"

echo ""
echo "Checking nodes..."

kubectl get nodes

echo ""
echo "Checking cluster..."

kubectl cluster-info

echo ""
echo "============================================"
echo " EKS setup completed."
echo "============================================"
```

