#!/usr/bin/env python
#
# Test script for sending all test tweets to classification service.
# Assumes that you have a "test" routing key for receiving the
# results.
#
import pika
import gzip
import time

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '127.0.0.1'))
channel = connection.channel()

properties = pika.BasicProperties(content_type='text/turtle')

print("Sending Turtle messages...")

turtle = ""
last_subject = ""

with gzip.open('sorted-tweets.n3.gz', 'rb') as f:
  for line in f:
    subject = line.split()[0]
    if subject == last_subject:
	turtle += line
    else:
    	channel.basic_publish(exchange='geiser',
                              routing_key='classification-v1.test',
                              body=turtle,
	                      properties=properties)
	#print("Next message:")
	#print(turtle)
        time.sleep(0.3)
        turtle = line
    last_subject = subject

# send last resource
if turtle != "":
	channel.basic_publish(exchange='geiser',
                              routing_key='classification-v1.test',
                              body=turtle,
	                      properties=properties)
	#print("Final message:")
	#print(turtle)

print(" [x] Sent JSON message to Service request message")
connection.close()
