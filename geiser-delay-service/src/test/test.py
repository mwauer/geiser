#!/usr/bin/env python
#
# Test script for sending sample messages to geiser-delay-service.
# Assumes that you have a "test" routing key for receiving the
# results.
#
import pika
import gzip
import time

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '127.0.0.1'))
channel = connection.channel()

properties = pika.BasicProperties(content_type='application/json')

print("Sending JSON messages...")

with gzip.open('many-test.json.gz', 'rb') as f:
  for line in f:
    channel.basic_publish(exchange='geiser',
                          routing_key='delay-v1.test',
                          body=line,
	                  properties=properties)
    time.sleep(0.1)

print(" [x] Sent JSON messages to Delay Service")
connection.close()
