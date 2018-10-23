import pika
import sys

credentials = pika.PlainCredentials('guest', 'guest')
configs = pika.ConnectionParameters('127.0.0.1',5672,'/',credentials)
connection = pika.BlockingConnection(configs)

channel = connection.channel()

channel.exchange_declare(exchange='ampq.direct', exchange_type='direct')
channel.basic_publish(exchange='geiser',
                      routing_key='sparql-select-v1.sparql-select-result',
                      body=sys.argv[1])

result = channel.queue_declare(exclusive=True)
queue_name = result.method.queue

channel.queue_bind(exchange='geiser',
                       queue=queue_name,
                       routing_key='sparql-select-result.#')

def callback(ch, method, properties, body):
    print(" [x] %r:%r" % (method.routing_key, body))
    connection.close()

channel.basic_consume(callback,
                      queue=queue_name,
                      no_ack=True)

channel.start_consuming()