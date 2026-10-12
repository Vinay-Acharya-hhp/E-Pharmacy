
pipeline {

    agent any

  stages{
    stage('Checks Aws Authentication'){
       steps{
           withCredentials([[
               $class: 'AmazonWebServicesCredentialsBinding' ,
               credentialsId: 'aws-cred'
               ]]){
          sh '''  
               aws sts get-caller-identity '''
           }
}
}
}
}
