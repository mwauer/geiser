#!/usr/bin/env python
#
# Test script for sending a sample turtle message to classification service.
# Assumes that you have a "test" routing key for receiving the
# results and classification service running.
#
import pika
import gzip

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '127.0.0.1'))
channel = connection.channel()

properties = pika.BasicProperties(content_type='text/turtle')

print("Sending Turtle message...")

message = '<urn:r1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:c1>; <http://www.w3.org/2000/01/rdf-schema#label> "Resource". <urn:r2> <http://www.w3.org/2000/01/rdf-schema#label> "Resource2"; <urn:p1> <urn:o1>.' 

channel.basic_publish(exchange='geiser',
                      routing_key='classification-v1.test',
                      body=message,
	                  properties=properties)

print(" [x] Sent turtle message to Service")
connection.close()
