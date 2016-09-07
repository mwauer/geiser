package de.kuhpfau.amqp.helloworld.receiver;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bean for representing the Hello World json message: <code>{"Hello": "World"}</code>
 * @author mwauer
 *
 */
public class HelloMessage {

	@JsonProperty(value="Hello", required=true)
	public String hello;

}
