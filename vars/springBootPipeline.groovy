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

            dir(config.servicename) {
                stage('Build') {
                    sh './gradlew clean build'
                }

                stage('Deploy') {
                    deploy(config.filename, config.username, config.hostname, config.port)
                }

                stage('Healthcheck') {
                    healthcheck("http://${config.hostname}:${config.port}/health")
                }
            }
        }
        catch (exc) {
            echo "Caught: ${exc}"
            currentBuild.result = 'FAILURE'
        }
    }

}