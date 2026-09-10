#!/bin/bash


cd epharmacy-frontend 
docker build -t vinayacharya07/pharmacy-web:v4 .
docker push  vinayacharya07/pharmacy-web:v4
cd ..

cd pharmacy-user-service
docker build -t vinayacharya07/pharmacy-user-api:v4 .
docker push  vinayacharya07/pharmacy-user-api:v4
cd ..

cd pharmacy-medicine-service
docker build -t vinayacharya07/pharmacy-medicine-api:v4 .
docker push vinayacharya07/pharmacy-medicine-api:v4
cd ..

cd pharmacy-cart-service
docker build -t vinayacharya07/pharmacy-cart-api:v4 .
docker push vinayacharya07/pharmacy-cart-api:v4
cd ..

cd pharmacy-order-service
docker build -t vinayacharya07/pharmacy-order-api:v4 .
docker push vinayacharya07/pharmacy-order-api:v4
cd ..

cd pharmacy-payment-service
docker build -t vinayacharya07/pharmacy-payment-api:v4 .
docker push  vinayacharya07/pharmacy-payment-api:v4
cd ..

cd pharmacy-api-gateway-service 
docker build -t vinayacharya07/pharmacy-api-gateway:v4 .
docker push  vinayacharya07/pharmacy-api-gateway:v4
cd ..
