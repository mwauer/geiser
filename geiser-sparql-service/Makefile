build:
	chmod +x mvnw
	./mvnw package dockerfile:build

run-docker-local:
	docker run -e "example_property=test-value" -e "amqp_server_host=localhost" --network host --name geiser-sparql-service mwauer/geiser-sparql-service

stop-docker:
	docker stop geiser-sparql-service

rm-docker:
	docker rm geiser-sparql-service
