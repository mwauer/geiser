#!/usr/bin/env python
#
# Test script for sending a single USU tweets to transformation and ner service.
# Assumes that you have a "test" routing key for receiving the
# results, fox docker running locally and transformation service running.
#
import pika
import gzip

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '127.0.0.1'))
channel = connection.channel()

properties = pika.BasicProperties(content_type='application/ld+json')

print("Sending JSONLD message...")

with gzip.open('test.jsonld.gz', 'rb') as f:
  for line in f:
    channel.basic_publish(exchange='geiser',
                          routing_key='ner-v1.test',
                          body=line,
	                  properties=properties)

print(" [x] Sent JSONLD message to Service")
connection.close()
