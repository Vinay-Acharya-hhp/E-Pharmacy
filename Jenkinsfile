
pipeline {

    agent any

  stages{
    stage('Checks Aws Authentication'){
       steps{
          sh '''  
               aws sts get-caller-identity '''

}
}
}
}
