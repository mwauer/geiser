package de.kuhpfau.amqp.helloworld.receiver;

import org.springframework.stereotype.Component;

import de.kuhpfau.amqp.servicediscovery.AbstractServiceDiscoveryReceiver;

/**
 * Provides service discovery for hello world service. Since hello world does
 * not respond with any message, we don't override the respective default
 * implementation.
 * 
 * @author mwauer
 *
 */
@Component
public class HelloWorldServiceDiscoveryReceiver extends AbstractServiceDiscoveryReceiver {

	@Override
	public String getServiceName() {
		return "HelloWorld";
	}

	@Override
	public String getServiceType() {
		return "example";
	}

	@Override
	public Class<?> getRequestMessageClass() {
		return HelloMessage.class;
	}

	@Override
	public String getRequestMessageDescription() {
		return "A sample hello world service";
	}

}
