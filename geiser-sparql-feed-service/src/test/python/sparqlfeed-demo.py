#!/usr/bin/env python
#
# Test script for sending a request to sparql feed.
# Assumes that you have a Virtuoso server running locally
# with test data in graph <urn:test>,
# and the following services are running:
# - SparqlFeedService
# - TranslateService
# - FoxService
# - RdfWriterService
#
import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '127.0.0.1'))
channel = connection.channel()

properties = pika.BasicProperties(content_type='application/json')

channel.basic_publish(exchange='geiser',
                      routing_key='sparqlfeed.test',
                      body='{"endpoint": "http://localhost:8890/sparql", "rdfFormat": "application/ld+json", "query": "construct { ?s <http://rdfs.org/sioc/ns#content> ?o } where { graph <urn:geiser-tweets> { ?s <http://rdfs.org/sioc/ns#content> ?o } } LIMIT 10"}',
		      properties=properties)
print(" [x] Sent Sparql Feed Service request message")
connection.close()
