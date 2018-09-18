build:
	chmod +x mvnw
	./mvnw package dockerfile:build

run-docker-local:
	docker run -e "example_property=test-value" -e "amqp_server_host=localhost" --network host --name geiser-json-transformation-service mwauer/geiser-json-transformation-service

stop-docker:
	docker stop geiser-json-transformation-service

rm-docker:
	docker rm geiser-json-transformation-service
