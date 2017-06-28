#!/usr/bin/env python
#
# Basic test script for sending a request to print the content of the repository.
#
import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '127.0.0.1'))
channel = connection.channel()
# disabled queue_declare for testing, needs an autodelete flag
#channel.queue_declare(queue='hello')

properties = pika.BasicProperties(content_type='text/turtle')

channel.basic_publish(exchange='',
                      routing_key='rdfwriterdebug',
                      body='does not matter',
		      properties=properties)
print(" [x] Sent RDF writer debug message, check server log")
connection.close()
