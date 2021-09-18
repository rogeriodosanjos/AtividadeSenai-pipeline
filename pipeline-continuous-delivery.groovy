#!/usr/bin/env groovy

def MAVEN_VERSION="maven-3.6.3"
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
                    git branch: 'master',
                        url: 'https://github.com/paulonill/exemplo-spring-mvc-thymeleaf.git'
                }
            }
        }
        
       stage('Validate') {
            steps {
                script {
                    withMaven(maven:MAVEN_VERSION){
                        sh "mvn clean validate"
                    }
                    //http://maven.apache.org/components/ref/3.3.9/maven-model/apidocs/org/apache/maven/model/Model.html
                    IMAGE = readMavenPom().getArtifactId()
                    VERSION = readMavenPom().getVersion()
                    PACKING = readMavenPom().getPackaging()

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
                         /*withEnv(["JAVA_HOME=${tool 'jdk11'}", "PATH=${tool 'jdk11'}/bin:${env.PATH}"]) {
                                
                         }*/
                        
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
                    
                    def discordImageSuccess = 'https://www.jenkins.io/images/logos/formal/256.png'
                    def discordImageError = 'https://www.jenkins.io/images/logos/fire/256.png'

                    def discordDesc =
                            "Result: ${currentBuild.currentResult}\n" +
                                    "Project: Nome projeto\n" +
                                    "Commit: Quem fez commit\n" +
                                    "Author: Autor do commit\n" +
                                    "Message: mensagem do changelog ou commit\n" +
                                    "Duration: ${currentBuild.durationString}"

                                    //Variaveis de ambiente do Jenkins - NOME DO JOB E NÚMERO DO JOB
                                    def discordFooter = "${env.JOB_BASE_NAME} (#${BUILD_NUMBER})"
                                    def discordTitle = "${env.JOB_BASE_NAME} (build #${BUILD_NUMBER})"
                                    def urlWebhook = "https://discord.com/api/webhooks/883733040646483978/1ww2MvJ4oHCKglPFAia1eFpB_2aNpSfjtZS-FOJTsLtDdY0lQFM2Zw_vLLTaDMT2SKLc"
                                    //def urlWebhook = "https://discord.com/api/webhooks/711712945934958603/tZiZgmNgW_lHleONDiPu5RVM24wbuxFKcpMBDJsY2WxSqjltAz3UCYupqSIE7q0rlmHP"

                    discordSend description: discordDesc,
                            footer: discordFooter,
                            link: env.JOB_URL,
                            result: currentBuild.currentResult,
                            title: discordTitle,
                            webhookURL: urlWebhook,
                            successful: currentBuild.resultIsBetterOrEqualTo('SUCCESS'),
                            thumbnail: 'SUCCESS'.equals(currentBuild.currentResult) ? discordImageSuccess : discordImageError
                    
                    
                    
                }
            }
        }         
    }
}
