# HelloWorld Service

This service tests how basic functionality for GEISER can be implemented based on AMQP.

I've used Spring AMQP on Spring Boot to implement a most basic service. 

Also, there's initial service discovery support. A python script can be used to test this.

Use of Spring Tools Suite for working on this is encouraged.

I'm running RabbitMQ as message broker in a Docker container without the respective port being forwarded to localhost, so make sure you adapt the application.properties accordingly.

# Usage
See src/test/python for a service discovery test script, including some hints on how to run it successfully.
