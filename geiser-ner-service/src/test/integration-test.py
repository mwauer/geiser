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

properties = pika.BasicProperties(content_type='application/json')

print("Sending JSON message...")

with gzip.open('integration-test.json.gz', 'rb') as f:
  for line in f:
    channel.basic_publish(exchange='geiser',
                          routing_key='jsontransformation-v1.ner-v1.test',
                          body=line,
	                  properties=properties)

print(" [x] Sent JSON message to Service request message")
connection.close()
