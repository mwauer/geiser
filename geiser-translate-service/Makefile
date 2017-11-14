build:
	./mvnw package dockerfile:build

run-docker:
	docker run -e "translate_api_key=TEST" -e "amqp_server_host=localhost" --network host --name geiser-translate-service mwauer/geiser-translate-service

stop-docker:
	docker stop geiser-translate-service

rm-docker:
	docker rm geiser-translate-service
