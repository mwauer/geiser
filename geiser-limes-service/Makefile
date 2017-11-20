build:
	./mvnw package dockerfile:build

run-docker:
	docker run -e "amqp_server_host=localhost" --network host --name geiser-limes-service mwauer/geiser-limes-service

stop-docker:
	docker stop geiser-limes-service

rm-docker:
	docker rm geiser-limes-service
