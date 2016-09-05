package de.kuhpfau.amqp.helloworld.receiver;

public class ServiceDiscoveryResponseBean {

	/**
	 * service name
	 */
	public String service;
	
	/**
	 * class of service
	 */
	public String type;

	/**
	 * JSON schema the service expects
	 */
	public String accept;
		
}
