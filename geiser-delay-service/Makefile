build:
	chmod +x mvnw
	./mvnw package dockerfile:build

run-docker-local:
	docker run -e "delay=2000" -e "amqp_server_host=localhost" --network host --name geiser-delay-service mwauer/geiser-delay-service

stop-docker:
	docker stop geiser-delay-service

rm-docker:
	docker rm geiser-delay-service
