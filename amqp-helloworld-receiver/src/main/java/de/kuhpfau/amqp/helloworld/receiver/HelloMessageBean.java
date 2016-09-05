package de.kuhpfau.amqp.helloworld.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HelloMessageBean {
	
	static final Logger log = LoggerFactory.getLogger(HelloMessageBean.class);
	
	public HelloMessageBean() {
		super();
		log.debug("Created HelloMessageBean");
	}

	@JsonProperty("Hello")
	private String hello;

	public String getHello() {
		log.debug("Returning hello: {}", hello);
		return hello;
	}

	public void setHello(String hello) {
		log.debug("Setting hello to {}", hello);
		this.hello = hello;
	}

}
