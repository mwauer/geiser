build:
	./mvnw package dockerfile:build

run-docker:
	docker run -e "translate_api_key=TEST" -e "amqp_server_host=localhost" --network host --name geiser-sparql-feed-service mwauer/geiser-sparql-feed-service

stop-docker:
	docker stop geiser-sparql-feed-service

rm-docker:
	docker rm geiser-sparql-feed-service
