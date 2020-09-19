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

        parallel {
            stage('Teste unitários') {
                agent {
                    agent any
                }
                steps {
                    echo "Trecho 1"
                }
            }

            stage('Testes de aceitação') {
                agent {
                    agent any
                }
                steps {
                    echo "Trecho 2"
                }
            }

            stage('Testes de negócio') {
                agent {
                    agent any
                }
                steps {
                    echo "Trecho 3"
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
