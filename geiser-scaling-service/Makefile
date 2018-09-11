build:
	./mvnw package dockerfile:build

run-docker-local:
	docker run -e "example_property=test-value" -e "amqp_server_host=localhost" --network host --name geiser-scaling-service mwauer/geiser-scaling-service

stop-docker:
	docker stop geiser-scaling-service

rm-docker:
	docker rm geiser-scaling-service
