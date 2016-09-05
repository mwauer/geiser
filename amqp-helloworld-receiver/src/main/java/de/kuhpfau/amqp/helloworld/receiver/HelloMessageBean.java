package de.kuhpfau.amqp.helloworld.receiver;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bean for representing the Hello World json message: <code>{"Hello": "World"}</code>
 * @author mwauer
 *
 */
public class HelloMessageBean {

	@JsonProperty("Hello")
	public String hello;

}
