#!/usr/bin/env python
#
# Test script for sending all 6199 USU tweets to transformation service.
# Assumes that you have a "test" routing key for receiving the
# results.
#
import pika
import gzip
import time
from argparse import ArgumentParser

parser = ArgumentParser()
parser.add_argument("-l", "--limit", dest="limit",  type=int,
                    help="Max number of messages to send")
parser.add_argument("-d", "--delay", dest="delay",  type=int,
                    help="Delay in seconds between the messages")
parser.add_argument("--jqFile", dest="jqfile",  type=str, default="jq-jsonld.txt",
                    help="dwd-json-jsonld.txt or jq-jsonld.txt")
parser.add_argument("--k", "--routingKey", dest="routing_key",  type=str, default="jsontransformation-v1.rdfwriter-v1.#",
                    help="The amqp routing key")
parser.add_argument("--jsonFile", dest="jsonFile",  type=str, default="all-separate.json.gz",
                    help="Json file with line separated objects")
parser.add_argument("--targetGraph", dest="targetGraph",  type=str, default="https://www.projekt-geiser.de/",
                    help="The target named graph")

args = parser.parse_args()
argsdict = vars(args)
print(argsdict)

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '127.0.0.1'))
channel = connection.channel()

properties = pika.BasicProperties(content_type='application/json',headers={'jqFile': argsdict['jqfile'], 'graph_uri': argsdict['targetGraph']})

print("Sending JSON messages...")

i = 0;  
with gzip.open(argsdict['jsonFile'], 'rb') as f:
  time.sleep(0)
  for line in f:
    channel.basic_publish(exchange='geiser', 
      #routing_key='jsontransformation-v1.test-v1',
                          routing_key=argsdict['routing_key'],
                          body=line,properties=properties)
    i += 1;
    if argsdict['delay']:
      time.sleep(argsdict['delay'])
    if argsdict['limit']: 
      if i>argsdict['limit']:
      	break;

print(" [x] Sent "+str(i)+" JSON messages to Service request message")
connection.close()
