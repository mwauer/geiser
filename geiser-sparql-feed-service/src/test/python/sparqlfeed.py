#!/usr/bin/env python
#
# Basic test script for sending a request to sparql feed.
# Assumes that you have a Virtuoso server running locally,
# and both SparqlFeedService and RdfWriterService are
# running.
#
import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '127.0.0.1'))
channel = connection.channel()
# disabled queue_declare for testing, needs an autodelete flag
#channel.queue_declare(queue='sparqlfeed')

properties = pika.BasicProperties(content_type='application/json')

channel.basic_publish(exchange='',
                      routing_key='sparqlfeed-v1',
                      body='{"endpoint": "http://localhost:8890/sparql"}',
		      properties=properties)
print(" [x] Sent Sparql Feed Service reuqest message, check server log")
connection.close()
