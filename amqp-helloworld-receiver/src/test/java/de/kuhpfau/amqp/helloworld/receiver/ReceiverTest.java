package de.kuhpfau.amqp.helloworld.receiver;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AmqpHelloworldReceiverApplication.class)
public class ReceiverTest {

	@Autowired
	private Receiver receiver;
	
	/**
	 * Validates that the last hello message is stored when handling a message.
	 */
	@Test
	public void testHandleMessage() {
		assertNull(receiver.lastHello);
		HelloMessageBean helloMessageBean = new HelloMessageBean();
		helloMessageBean.hello = "test";
		receiver.handleMessage(helloMessageBean);
		assertEquals("test", receiver.lastHello.hello);
	}

}
