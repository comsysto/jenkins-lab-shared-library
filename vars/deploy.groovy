def call(String filename, String user, String server, String port, String deploymentPath = '~/deployment/') {
    httpRequest url: "http://${server}:${port}/shutdown", httpMode: 'POST', validResponseCodes: '200,408,404'
	sh "ssh ${user}@${server} \"mkdir -p ${deploymentPath}\""
	sh "scp build/libs/${filename} ${user}@${server}:${deploymentPath}"
	sh "ssh ${user}@${server} \"nohup java -jar ${deploymentPath}/${filename} --server.port=${port}\" &"
}