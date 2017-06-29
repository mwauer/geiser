#!/usr/bin/env python
#
# Basic test script for sending an RDF statement AMQP message, to be received by GeiserRdfWriterService.
#
import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '127.0.0.1'))
channel = connection.channel()
# disabled queue_declare for testing, needs an autodelete flag
#channel.queue_declare(queue='hello')

properties = pika.BasicProperties(content_type='text/turtle')

statements = raw_input('Please enter the statements in Turtle notation. Default: "<urn:r1> <urn:p1> <urn:r2>."')
if not statements:
	statements = '<urn:r1> <urn:p1> <urn:r2>.'

channel.basic_publish(exchange='',
                      routing_key='rdfwriter',
                      body=statements,
		      properties=properties)
print(" [x] Sent simple RDF statement(s) as turtle")
connection.close()
