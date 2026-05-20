pipeline {
    agent any

    stages{
        stage("Checkout"){
            steps{
                checkout scm
            }
        }
        stage("set-up"){
            steps{
                sh """
                    java -version
                    mvn -version
                    git --verion
                """
            }
        }
        stage("compile"){
            steps{
                sh "mvn clean compile"
            }
        }

        stage("Unit test"){
            steps{
                sh "mvn test"
            }
            post {
                always {
                    junit "target/surefire-reports/*.xml"
                }
            }
        }
        stage("Coverage report"){
            steps{
                sh "mvn jacoco:report"
            }
            post{
                always{
                    archiveArtifacts artifacts: "target/site/jacoco/jacoco.xml", allowEmptyArchive: true
                }
            }
        }

        stage("static analysis"){
            steps{
                sh "mvn checkstyle:checkstyle pmd:pmd spotbugs:spotbugs"
            }
            post {
                always{
                    archiveArtifacts artifacts: "target/site/**,target/spotbugs.xml", allowEmptyArchive: true
                }
            }
        }
        // stage("sonarqube analysis"){
        //     steps{
        //         withSonarQubeEnv('sonarqube'){
        //             sh "mvn sonar:sonar"
        //         }
        //     }
        // }
    }
}
