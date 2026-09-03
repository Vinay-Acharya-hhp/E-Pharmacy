pipeline {

    agent any

    environment {
        DOCKERHUB_USERNAME = credentials('dockerhub-username')
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

        stage('Test Microservices') {
            steps {
                sh '''
                    set -e
                    
                    
                    cd pharmacy-eureka-server
                    mvn clean test -DskipTests
                    cd ..

                    cd pharmacy-medicine-service
                    mvn clean test
                    cd ..
              
                  
                    cd pharmacy-user-service
                    mvn clean test
                    cd ..

                    cd pharmacy-cart-service
                    mvn clean test
                    cd ..

                    cd pharmacy-order-service
                    mvn clean test
                    cd ..

                    cd pharmacy-payment-service
                    mvn clean test
                    cd ..

                    cd pharmacy-api-gateway-service
                    mvn clean test
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
                        -u "$DOCKERHUB_USERNAME" \
                        --password-stdin
                    '''
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh '''
                    docker compose build
                '''
            }
        }

        stage('Push Images to Docker Hub') {
            steps {
                sh '''
                    docker compose push
                '''
            }
        }
    }

    post {
        always {
            sh 'docker logout || true'
        }

        success {
            echo 'All images successfully pushed to Docker Hub!'
        }

        failure {
            echo 'Pipeline failed.'
        }
    }
}
