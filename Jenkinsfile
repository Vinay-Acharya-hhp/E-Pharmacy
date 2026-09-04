pipeline {

    agent any

    environment {
        DOCKER_USERNAME = 'vinayacharya07'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Create .env') {
            steps {
                withCredentials([
                    file(
                        credentialsId: 'epharmacy-env',
                        variable: 'ENV_FILE'
                    )
                ]) {
                    sh '''
                        cp "$ENV_FILE" .env
                    '''
                }
            }
        }

        stage('Make Maven Wrapper Executable') {
            steps {
                sh '''
                    chmod +x pharmacy-eureka-server/mvnw
                    chmod +x pharmacy-user-service/mvnw
                    chmod +x pharmacy-medicine-service/mvnw
                    chmod +x pharmacy-cart-service/mvnw
                    chmod +x pharmacy-order-service/mvnw
                    chmod +x pharmacy-payment-service/mvnw
                    chmod +x pharmacy-api-gateway-service/mvnw
                '''
            }
        }

        stage('Test Microservices') {
            steps {
                sh '''
                    set -e

                    echo "Testing Eureka..."
                    cd pharmacy-eureka-server
                    ./mvnw clean test -Dspring.profiles.active=test
                    cd ..

                    echo "Testing User Service..."
                    cd pharmacy-user-service
                    ./mvnw clean test -Dspring.profiles.active=test
                    cd ..

                    echo "Testing Medicine Service..."
                    cd pharmacy-medicine-service 
                    ./mvnw clean test -Dspring.profiles.active=test
                    cd ..

                    echo "Testing Cart Service..."
                    cd pharmacy-cart-service
                    ./mvnw clean test -Dspring.profiles.active=test
                    cd ..

                    echo "Testing Order Service..."
                    cd pharmacy-order-service
                    ./mvnw clean test -Dspring.profiles.active=test
                    cd ..

                    echo "Testing Payment Service..."
                    cd pharmacy-payment-service
                    ./mvnw clean test -Dspring.profiles.active=test
                    cd ..

                    echo "Testing API Gateway..."
                    cd pharmacy-api-gateway-service
                    ./mvnw clean test -Dspring.profiles.active=test
                    cd ..
                '''
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([
                    string(
                        credentialsId: 'dockerhub-token',
                        variable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    sh '''
                        echo "$DOCKERHUB_TOKEN" | docker login \
                            -u "$DOCKER_USERNAME" \
                            --password-stdin
                    '''
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh '''
                   echo " docker compose build "
                '''
            }
        }

        stage('Push User Service Images to Docker Hub') {
            steps {
                sh '''
                    docker compose push user-api
                '''
            }
        }

     stage('Push Medicine Docker Images') {
            steps {
                sh '''
                   docker compose push medicine-api "
                '''
            }
        }

    stage('Push Cart Docker Images') {
            steps {
                sh '''
                   docker compose push cart-api "
                '''
            }
        }
    stage('Push Order Docker Images') {
            steps {
                sh '''
                   docker compose push order-api "
                '''
            }
        }
     stage('Push Api-Gateway Docker Images') {
            steps {
                sh '''
                   docker compose push api-gateway "
                '''
            }
        }
      stage('Push Payment Docker Images') {
            steps {
                sh '''
                   docker compose push payment-api "
                '''
            }
        }
       stage('Push Eureka Docker Images') {
            steps {
                sh '''
                   docker compose push eureka "
                '''
            }
        }



    }

    post {

        always {
            sh 'docker logout || true'
        }

        success {
            echo '======================================'
            echo ' Jenkins Pipeline Successful!'
            echo ' Docker images pushed successfully.'
            echo '======================================'
        }

        failure {
            echo '======================================'
            echo ' Jenkins Pipeline Failed!'
            echo ' Check the stage logs above.'
            echo '======================================'
        }
    }
}
