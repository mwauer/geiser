#!/usr/bin/env python
import pika
import sys
from shutil import copy

# copy source and target files to /tmp
copy('restaurant1.nt', '/tmp/')
copy('restaurant2.nt', '/tmp/')

# assumes rabbitmq is running at localhost
connection = pika.BlockingConnection(pika.ConnectionParameters(
        host='localhost'))
channel = connection.channel()

channel.exchange_declare(exchange='geiser',
                         type='topic', durable=True, auto_delete=True)

routing_key = 'limes.test'
#message = ' '.join(sys.argv[2:]) or 'Hello World!'
# load LIMES config file into variable
with open('restaurants.xml', 'r') as configfile:
    message=configfile.read().replace('\n', '')
channel.basic_publish(exchange='geiser',
                      routing_key=routing_key,
                      properties=pika.BasicProperties(
			message_id='1234',
                        reply_to='test'),
                      body=message)
print(" [x] Sent LIMES request to geiser exchange, routing key %r:%r" % (routing_key, message))
connection.close()
