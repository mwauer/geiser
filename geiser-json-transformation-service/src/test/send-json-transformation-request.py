#!/usr/bin/env python
#
# Test script for sending all 6199 USU tweets to transformation service.
# Assumes that you have a "test" routing key for receiving the
# results.
#
import pika
import gzip

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '127.0.0.1'))
channel = connection.channel()

properties = pika.BasicProperties(content_type='application/json')

print("Sending JSON messages...")

with gzip.open('all-separate.json.gz', 'rb') as f:
  for line in f:
    channel.basic_publish(exchange='geiser',
                          routing_key='jsontransformation-v1.test',
                          body=line,
	                  properties=properties)

print(" [x] Sent JSON messages to Service request message")
connection.close()
