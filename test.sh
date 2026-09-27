#!/bin/bash


cd epharmacy-frontend 
docker build -t vinayacharya07/pharmacy-web:latest .
docker push  vinayacharya07/pharmacy-web:latest
cd ..

cd pharmacy-user-service
docker build -t vinayacharya07/pharmacy-user-api:latest .
docker push  vinayacharya07/pharmacy-user-api:latest
cd ..

cd pharmacy-medicine-service
docker build -t vinayacharya07/pharmacy-medicine-api:latest .
docker push vinayacharya07/pharmacy-medicine-api:latest
cd ..

cd pharmacy-cart-service
docker build -t vinayacharya07/pharmacy-cart-api:latest .
docker push vinayacharya07/pharmacy-cart-api:latest
cd ..

cd pharmacy-order-service
docker build -t vinayacharya07/pharmacy-order-api:latest .
docker push vinayacharya07/pharmacy-order-api:latest
cd ..

cd pharmacy-payment-service
docker build -t vinayacharya07/pharmacy-payment-api:latest .
docker push  vinayacharya07/pharmacy-payment-api:latest
cd ..

cd pharmacy-api-gateway-service 
docker build -t vinayacharya07/pharmacy-api-gateway:latest .
docker push  vinayacharya07/pharmacy-api-gateway:latest
cd ..
