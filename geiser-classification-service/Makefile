build:
	./mvnw package dockerfile:build

run-docker-local:
	docker run -e "example_property=test-value" -e "amqp_server_host=localhost" --network host --name geiser-classification-service mwauer/geiser-classification-service

stop-docker:
	docker stop geiser-classification-service

rm-docker:
	docker rm geiser-classification-service
