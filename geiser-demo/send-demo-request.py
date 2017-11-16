#!/usr/bin/env python
#
# Test script for sending a request to sparql-feed-service.
# Assumes that all services are started via docker-compose up.
#
# Accesses remote SPARQL endpoint, modify endpoint and query
# below to avoid.
#
import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '127.0.0.1'))
channel = connection.channel()

properties = pika.BasicProperties(content_type='application/ld+json')

channel.basic_publish(exchange='geiser',
                      routing_key='sparqlfeed.test',
#                      routing_key='sparqlfeed.translate.fox-v1.rdfwriter',
                      body=('{"endpoint": "https://geiser-test.grapphs.com/sparql", "rdfFormat": "application/ld+json", '
			    '"query": "construct { ?s <http://rdfs.org/sioc/ns#content> ?o }'
			    ' where { graph <file://geiser_all_tweets.json.20170309-093913.ttl-09-03-2017-09-52-18> { ?s <http://rdfs.org/sioc/ns#content> ?o } } LIMIT 32"}'),
		      properties=properties)
print(" [x] Sent Sparql Feed Service request message")
connection.close()
