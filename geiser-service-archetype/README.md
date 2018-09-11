# GEISER service archetype

You can use this archetype to generate a new basic GEISER service.

## Installation

### For users without the AKSW internal maven repository configured

 * Clone the GEISER github: `git clone git@github.com:mwauer/geiser.git`
 * `cd geiser-service-archetype`
 * `mvn install`
 
Now you have the archetype available in your local Maven repository.
 
## Usage

Simply invoke: `mvn archetype:generate -Dfilter=org.aksw.geiser:geiser-service-archetype`

Then select the archetype (enter 1). Then you are presented with a number of default properties. Enter n to define your own:
 * _groupId_, _artifactId_, _version_: the basic Maven properties of your new service,
 * _package_: the Java package containing the service implementation,
 * _className_: the Java class prefix of the service implementation, e.g., "MyFirst" will result in "MyFirstServiceApplication.java"
 * _dockerRepo_: your repository name in docker hub (will be used in the dockerfile-maven-plugin and Makefile), e.g., "dude" will result in the docker image to be "dude/{artifactId}",
 * _queueName_: the AMQP queue name to be defined for your service,
 * _routingKey_: the AMQP routing key to be defined as the topic of your service.

The service will then be generated in a new `{artifactId}` subfolder. You can import that into, e.g., Eclipse to implement the service. Use `mvn install` to build the service. Have a look at the Makefile and Deliverable D6.2.1 for further detail.