def call(String filename, String user, String server, String port) {
    httpRequest url: "http://${server}:${port}/shutdown", httpMode: 'POST', validResponseCodes: '200,408'
	sh "scp build/libs/${filename} ${user}@${server}:~/deployment"
	sh "ssh ${user}@${server} \"nohup java -jar deployment/${filename} --server.port=${port}\" &"
}