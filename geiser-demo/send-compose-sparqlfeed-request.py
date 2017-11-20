#!/usr/bin/env python
#
# Test script for sending a request to sparql feed.
# Assumes that you have a Virtuoso server running locally
# with test data in graph <urn:geiser-tweets>,
# and the following services are running:
# - SparqlFeedService
# - TranslateService
# - Fox and Fox-Proxy running
# - RdfWriterService
#
import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '127.0.0.1'))
channel = connection.channel()

properties = pika.BasicProperties(content_type='application/ld+json')

channel.basic_publish(exchange='geiser',
                      routing_key='sparqlfeed.translate.fox-v1.rdfwriter',
                      body=('{"endpoint": "http://172.17.42.1:8890/sparql", "rdfFormat": "application/ld+json", '
			    '"query": "construct { ?s <http://rdfs.org/sioc/ns#content> ?o }'
			    ' where { graph <urn:geiser-tweets> { ?s <http://rdfs.org/sioc/ns#content> ?o } } LIMIT 32"}'),
		      properties=properties)
print(" [x] Sent Sparql Feed Service request message")
connection.close()
