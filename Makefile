local-rabbit:
	docker run -d --hostname rabbit --name rabbit -p 5672:5672 -p 8081:15672 rabbitmq:management
