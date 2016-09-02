package de.kuhpfau.amqp.helloworld.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AmqpHelloworldReceiverApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmqpHelloworldReceiverApplication.class, args);
	}
}
