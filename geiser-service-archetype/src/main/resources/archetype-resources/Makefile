build:
	chmod +x mvnw
	./mvnw package dockerfile:build

run-docker-local:
	docker run -e "example_property=test-value" -e "amqp_server_host=localhost" --network host --name ${artifactId} ${dockerRepo}/${artifactId}

stop-docker:
	docker stop ${artifactId}

rm-docker:
	docker rm ${artifactId}
