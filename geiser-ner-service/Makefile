build:
	chmod +x mvnw
	./mvnw package dockerfile:build

run-docker-local:
	docker run -e "example_property=test-value" -e "amqp_server_host=localhost" --network host --name geiser-ner-service mwauer/geiser-ner-service

stop-docker:
	docker stop geiser-ner-service

rm-docker:
	docker rm geiser-ner-service
