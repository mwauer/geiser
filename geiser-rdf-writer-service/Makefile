build:
	./mvnw package dockerfile:build

run-docker:
	docker run -e "amqp_server_host=localhost" --network host --name geiser-rdf-writer-service mwauer/geiser-rdf-writer-service

stop-docker:
	docker stop geiser-rdf-writer-service

rm-docker:
	docker rm geiser-rdf-writer-service
