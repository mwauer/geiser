package de.kuhpfau.amqp.helloworld.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
	
	private final static Logger Log = LoggerFactory.getLogger(Receiver.class);

	public HelloMessage lastHello;
	
	@Bean
	public Queue helloQueue() {
		return new Queue("hello", false, false, true);
	}
	
	@RabbitListener(queues="hello")
	public void handleMessage(HelloMessage bean) {
		this.lastHello = bean;
		Log.info("Got Hello "+bean.hello);
	}
	
}
