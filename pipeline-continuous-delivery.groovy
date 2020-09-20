#!/usr/bin/env groovy

def MAVEN_VERSION="MAVEN3.6.3"
def IMAGE=""
def VERSION=""
def PACKING=""
def APP=""

pipeline {
    //Agent é o NÓ que vai rodar o job
    agent any

    //Fases do pipeline
    stages {
        
       stage('Checkout') {
            steps {
                script {
                    checkout([
                            $class           : 'GitSCM',
                            branches         : [[name: 'repo/${BRANCH}']],
                            userRemoteConfigs: scm.userRemoteConfigs])
                }
            }
        }
        
       stage('Validate') {
            steps {
                script {
                    withMaven(maven:MAVEN_VERSION){
                        //sh está rodando dentro do container, não no jenkins.
                        sh "mvn clean validate"
                    }
                    //http://maven.apache.org/components/ref/3.3.9/maven-model/apidocs/org/apache/maven/model/Model.html
                    IMAGE = readMavenPom().getArtficactId()
                    VERSION = readMavenPom().getVersion()
                    PACKING = readMavenPom().getPacking()

                    APP = "${IMAGE}.${PACKING}"
                    //Instrução ECHO irá sair no CONSOLE do Jenkins    
                    echo "Nome da aplicação: ${APP}"
                }
            }
        }        

        stage('Build') {
            steps {
                script{
                    withMaven(maven:MAVEN_VERSION){
                        //sh está rodando dentro do container, não no jenkins.
                        //Fazer o build do projeto COMPILAR
                        sh "mvn clean package"
                    }                    
                }
            }
        }

        stage('Continuous Delivery') {
            parallel {
                stage('Teste unitários') {
                    steps {
                        echo "Trecho 1"
                    }
                }

                stage('Testes de aceitação') {
                    steps {
                        echo "Trecho 2"
                    }
                }

                stage('Testes de negócio') {
                    steps {
                        echo "Trecho 3"
                    }
                }

            }
        }

        stage('Deploy to Stage') {
            steps {
                echo 'Fazer deploy em ambiente de homologação (staging).'
            }
        }
        stage('Acceptance Tests') {
            steps {
                echo 'Fazer deploy em ambiente de homologação (staging).'
            }
        }
        stage('Deploy em Produção') {
            steps {
                script {
                    def deploymentDelay = input id: 'Deploy', message: 'Deploy to production?', submitter: 'rkivisto,admin', parameters: [choice(choices: ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24'], description: 'Hours to delay deployment?', name: 'deploymentDelay')]
                    sleep time: deploymentDelay.toInteger(), unit: 'HOURS'
                    echo 'Deploy em produção'
                }
            }
        }         
    }
}
