#!/usr/bin/env python
#
# Basic test script for sending a request to the translate
# service.
# Assumes that you have a geiser-translate-service running
# locally.
#
import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '127.0.0.1'))
channel = connection.channel()

properties = pika.BasicProperties(content_type='text/turtle')

request='''
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix dc: <http://purl.org/dc/elements/1.1/> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix sioc: <http://rdfs.org/sioc/ns#> .

<urn:r1> rdf:type sioc:Post;
	rdfs:label "Teetasse"@de.
<urn:r2> rdfs:comment "Dieser Text wird in der Standardkonfiguration nicht uebersetzt.";
	rdfs:label "Nur uebersetzen ohne subject_type-Definition."@de.
'''
print('Sending request: '+request)

channel.basic_publish(exchange='geiser',
                      routing_key='translate-v2.test',
                      body=request,
		      properties=properties)
print(" [x] Sent Translate Service request message, check server log")
connection.close()
