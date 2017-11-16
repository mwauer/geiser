# targets to run, stop, and remove FOX and FOX-Proxy containers locally.

run-fox:
	docker run -e "LNG=en" --network host --name geiser-fox mwauer/fox:2.3.0

run-fox-proxy:
	docker run -e "MICROPROXY_RABBIT_HOST=amqp://localhost:5672" -e "MICROPROXY_EXCHANGE=geiser" -e "FOX_URL=http://localhost:4444/api" -e "MICROPROXY_SERVER_LISTENER=8088" --network host --name geiser-fox-proxy mwauer/fox-proxy

stop-fox:
	docker stop geiser-fox

rm-fox:
	docker rm geiser-fox

stop-fox-proxy:
	docker stop geiser-fox-proxy

rm-fox-proxy:
	docker rm geiser-fox-proxy
