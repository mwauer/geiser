build:
	chmod +x mvnw
	./mvnw package dockerfile:build

run-docker-local:
	docker run -e "example_property=test-value" -e "amqp_server_host=localhost" --network host --name geiser-deer-service mwauer/geiser-deer-service

stop-docker:
	docker stop geiser-deer-service

rm-docker:
	docker rm geiser-deer-service
