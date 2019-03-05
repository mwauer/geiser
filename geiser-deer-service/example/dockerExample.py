#!/usr/bin/env python
#
# assumes you have a docker container running locally using the "make run-docker-local" command.
#
import pika
import sys
import subprocess

# copy source and target files to /tmp in docker container
subprocess.call(["docker", "cp", "firstSource.ttl", "geiser-deer-service:/tmp/"])
subprocess.call(["docker", "cp", "secondSource.ttl", "geiser-deer-service:/tmp/"])

# assumes rabbitmq is running at localhost
connection = pika.BlockingConnection(pika.ConnectionParameters(
        host='localhost'))
channel = connection.channel()

channel.exchange_declare(exchange='geiser',
                         type='topic', durable=True, auto_delete=True)

routing_key = 'deer-v1.test'
#message = ' '.join(sys.argv[2:]) or 'Hello World!'
# load LIMES config file into variable
with open('config.ttl', 'r') as configfile:
    message=configfile.read()
channel.basic_publish(exchange='geiser',
                      routing_key=routing_key,
                      properties=pika.BasicProperties(
			message_id='1234',
                        reply_to='test'),
                      body=message)
print(" [x] Sent DEER request to geiser exchange, routing key %r:%r" % (routing_key, message))
connection.close()
