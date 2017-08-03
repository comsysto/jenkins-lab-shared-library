def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {
        try {
            stage('Checkout') {
                checkout scm
                sh 'git clean -dfx'
            }

            dir('service-1') {
                stage('Build') {
                    sh './gradlew clean build'
                }

                stage('Deploy') {
                    withCredentials([usernamePassword(credentialsId: 'Deployserver', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        deploy(${config.filename}, '$USERNAME', ${config.hostname}, ${config.port})
                    }
                }

                stage('Healthcheck') {
                    healthcheck('http://jenkinslab-deployserver:9090/health')
                }
            }
        }
        catch (exc) {
            echo "Caught: ${exc}"
            currentBuild.result = 'FAILURE'
        }
    }

}