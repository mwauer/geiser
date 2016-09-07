package de.kuhpfau.amqp.helloworld.receiver;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AmqpHelloworldReceiverApplication.class)
public class ServiceDiscoveryReceiverTest {

	@Autowired
	public ServiceDiscoveryReceiver receiver;
	
	@Test
	public void testHandleServiceDiscoveryRequest() {
		ServiceDiscoveryResponse response = receiver.handleServiceDiscoveryRequest(new ServiceDiscoveryRequest(), new HashMap<>());
		assertNotNull(response.service);
		assertNotNull(response.type);
		assertNotNull(response.requestSchema);
		assertEquals("A sample hello world service", response.requestSchema.getDescription());
	}

}
