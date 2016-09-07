package de.kuhpfau.amqp.helloworld.receiver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Context loading test for amqp-helloworld-receiver.
 * 
 * TODO: use an amqp mock such as described in <a href=
 * "https://dzone.com/articles/mocking-rabbitmq-for-integration-tests?utm_medium=feed&utm_source=feedpress.me&utm_campaign=Feed:%20dzone">
 * DZone</a>.
 * 
 * @author mwauer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AmqpHelloworldReceiverApplication.class)
public class AmqpHelloworldReceiverApplicationTests {

	@Test
	public void contextLoads() {
	}

}
