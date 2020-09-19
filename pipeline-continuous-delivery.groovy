pipeline {
    //Agent é o NÓ que vai rodar o job
    agent any

    //Fases do pipeline
    stages {
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
    }
}
