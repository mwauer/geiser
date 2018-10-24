#GEISER Demo

##SETUP
Run `docker-compose up -d` in `./geiser-demo` to deploy the entire GEISER stack including FOX. Please note that FOX image requires sufficient disk space and RAM.

To run a minimal version of the GEISER bus (sending, transformation/integration, storage, querying), please navigate into the `./geiser-demo/quick` and run  `docker-compose up -d` from there.

##Initializion of Data Feeds
The python script `send-json-transformation-request.py` can be used to map over existing JSON data. It assumes a `*.gz` json file, where each line is a single valid JSON object.

* `-l, --limit`Max number of messages to send
* `-d, --delay`Delay in seconds between the messages
* `--k, --routingKey` The amqp routing key
* `--jqFile`       dwd-json-jsonld.txt or jq-jsonld.txt
* `--jsonFile`   Json file with line separated objects
* `--targetGraph` The target named graph

###DWD Data

Send individual messages (one dwd whether event/message) to the JSON2RDF transformation service. Transformed objects will be send as JSONLD to the rdfwriter service:

```
python send-json-transformation-request.py -l 1000 \
--jsonFile dwd-das-fest-all-line-based.json.gz --jqFile dwd-json-jsonld.txt \
--targetGraph https://www.projekt-geiser.de/dwd-events \
--routingKey jsontransformation-v1.rdfwriter-v1.#
```

###Tweet Data
Send individual messages (one tweet object/message) to the JSON2RDF transformation service. Transformed objects will be send as JSONLD to the rdfwriter service:

```
python send-json-transformation-request.py -l 1000 \
--jsonFile tweets-das-fest-all-line-based.json.gz --jqFile jq-jsonld.txt \
--targetGraph https://www.projekt-geiser.de/tweets \
--routingKey jsontransformation-v1.rdfwriter-v1.#
```

##Query Example
```
python sparql-direct-exchange-example.py 'SELECT * WHERE {?a ?b ?c} LIMIT 10'
```