package de.kuhpfau.amqp.helloworld.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.kuhpfau.amqp.helloworld.receiver.AmqpHelloworldReceiverApplication;
import de.kuhpfau.amqp.helloworld.receiver.HelloWorldServiceDiscoveryReceiver;
import de.kuhpfau.amqp.servicediscovery.ServiceDiscoveryRequest;
import de.kuhpfau.amqp.servicediscovery.ServiceDiscoveryResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AmqpHelloworldReceiverApplication.class)
public class HelloWorldServiceDiscoveryReceiverTest {

	@Autowired
	public HelloWorldServiceDiscoveryReceiver receiver;

	@Test
	public void testHandleServiceDiscoveryRequest() {
		ServiceDiscoveryResponse response = receiver.handleServiceDiscoveryRequest(new ServiceDiscoveryRequest(),
				new HashMap<>());
		assertNotNull(response.service);
		assertNotNull(response.type);
		assertNotNull(response.requestSchema);
		assertEquals("A sample hello world service", response.requestSchema.getDescription());
	}

}
