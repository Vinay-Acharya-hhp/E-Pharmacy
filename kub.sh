#!/bin/bash


cd k8s/api-gateway
kubectl apply -f api-gateway-config.yaml -n epharmacy
kubectl apply -f api-gateway-deployment.yaml -n epharmacy
kubectl apply -f api-gateway-service.yaml -n epharmacy
kubectl rollout restart deployment api-gateway-service -n epharmacy
cd ../..

cd k8s/cart
kubectl apply -f cart-config.yaml  -n epharmacy
kubectl apply -f cart-secret.yaml  -n epharmacy
kubectl apply -f cart-deployment.yaml -n epharmacy
kubectl apply -f cart-service.yaml -n epharmacy
kubectl rollout restart deployment cart-service -n epharmacy
cd ../..

cd k8s/order
kubectl apply -f order-config.yaml  -n epharmacy
kubectl apply -f order-secret.yaml  -n epharmacy
kubectl apply -f order-deployment.yaml  -n epharmacy
kubectl apply -f order-service.yaml -n epharmacy
kubectl rollout restart deployment order-service -n epharmacy
cd ../..

cd k8s/medicine
kubectl apply -f medicine-config.yaml  -n epharmacy
kubectl apply -f medicine-secret.yaml  -n epharmacy
kubectl apply -f deployment.yaml -n epharmacy
kubectl apply -f service.yaml -n epharmacy
kubectl rollout restart deployment medicine-service -n epharmacy
cd ../..

cd k8s/user
kubectl apply -f user-config.yaml  -n epharmacy
kubectl apply -f user-secret.yaml  -n epharmacy
kubectl apply -f deployment.yaml -n epharmacy
kubectl apply -f service.yaml -n epharmacy
kubectl rollout restart deployment user-service -n epharmacy
cd ../..

cd k8s/payment
kubectl apply -f payment-config.yaml  -n epharmacy
kubectl apply -f payment-secret.yaml  -n epharmacy
kubectl apply -f payment-deployment.yaml -n epharmacy
kubectl apply -f payment-service.yaml -n epharmacy
kubectl rollout restart deployment payment-service -n epharmacy
cd ../..

cd k8s/web
# kubectl apply -f web-config.yaml  -n epharmacy
# kubectl apply -f web-secret.yaml  -n epharmacy
kubectl apply -f web-deployment.yaml -n epharmacy
kubectl apply -f web-service.yaml -n epharmacy
kubectl rollout restart deployment web-service -n epharmacy
cd ../..

