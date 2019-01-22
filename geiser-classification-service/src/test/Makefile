benchmark-filter:
	docker run -e "configuration=null <http://purl.org/dc/terms/spatial> null" -e "cbd=false" -e "amqp_server_host=localhost" --network host --name geiser-classification-service mwauer/geiser-classification-service | tee benchmark-filter.log

benchmark-filter-cbd:
	docker run -e "configuration=null <http://purl.org/dc/terms/spatial> null" -e "amqp_server_host=localhost" --network host --name geiser-classification-service mwauer/geiser-classification-service | tee benchmark-filter-cbd.log

benchmark-query:
	docker run -e "mode=query" -e "configuration=CONSTRUCT { ?s ?p ?o } WHERE { ?s <http://purl.org/dc/terms/spatial> ?spatial; ?p ?o }" -e "amqp_server_host=localhost" --network host --name geiser-classification-service mwauer/geiser-classification-service | tee benchmark-query.log

benchmark-logs-into-csv:
	cat benchmark-filter.log | grep -oP 'XXXXX \K.*' > benchmark-filter-times.csv
	cat benchmark-filter-cbd.log | grep -oP 'XXXXX \K.*' > benchmark-filter-cbd-times.csv
	cat benchmark-query.log | grep -oP 'XXXXX \K.*' > benchmark-query-times.csv

stop-docker:
	docker stop geiser-classification-service

rm-docker:
	docker rm geiser-classification-service
