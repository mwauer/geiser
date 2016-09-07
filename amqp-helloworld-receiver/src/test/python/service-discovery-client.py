#!/usr/bin/env python
import pika
import uuid

class ServiceDiscoveryRpcClient(object):
    def __init__(self):
        self.connection = pika.BlockingConnection(pika.ConnectionParameters(
                host='172.17.0.3'))

        self.channel = self.connection.channel()

        result = self.channel.queue_declare(exclusive=True)
        self.callback_queue = result.method.queue

        self.channel.basic_consume(self.on_response, no_ack=True,
                                   queue=self.callback_queue)

    def on_response(self=None, ch=None, method=None, props=None, body=None):
        if body is None:
            print(" [.] Receiving service discovery response time out")
            self.channel.stop_consuming()
            return
        if self.corr_id == props.correlation_id:
            self.response.append(body)
            print(" [.] received a response")

    def call(self, timeout):
        self.response = []
        self.corr_id = str(uuid.uuid4())
        self.connection.add_timeout(timeout, self.on_response)
        self.channel.basic_publish(exchange='service.discovery.request',
                                   routing_key='',#service.discovery.request',
                                   properties=pika.BasicProperties(
                                         reply_to = self.callback_queue,
                                         correlation_id = self.corr_id,
                                         content_type='application/json'
                                         ),
                                   body='{}')
        self.channel.start_consuming()
        return self.response

sd_rpc = ServiceDiscoveryRpcClient()

print(" [x] Requesting service discovery with 5 second timeout")
response = sd_rpc.call(5)
print(" [x] Got %s services:" % len(response))
for service in response:
    print(" [.] Got %s" % service)
