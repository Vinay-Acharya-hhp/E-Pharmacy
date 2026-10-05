helm repo add eks https://aws.github.io/eks-charts
helm repo update

helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=epharmacy \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller \
  --set region=ap-south-1 \
  --set vpcId=vpc-08173d88bda134fb8

sleep 20s

kubectl rollout status deployment/aws-load-balancer-controller -n kube-system --timeout=120s
kubectl get deployment aws-load-balancer-controller -n kube-system
kubectl logs -n kube-system deployment/aws-load-balancer-controller --tail=20
