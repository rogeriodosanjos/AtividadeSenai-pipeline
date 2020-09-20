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

        stage('Build') {
            steps {
                echo 'Fazendo a build do projeto'
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
