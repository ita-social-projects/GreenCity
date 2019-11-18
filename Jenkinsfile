#!groovy
pipeline {
    agent none
    stages {

        stage('Building') {
            agent any
            steps {

                sh "echo Building"   // prints out 5
                sh "mvn package"
            }
        }
    
        stage('Testing') {
            agent any
            steps {
                echo 'Building..'
                sh "mvn test"
            }
        }
    }
}
