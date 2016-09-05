package de.kuhpfau.amqp.helloworld.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
	
	private final static Logger Log = LoggerFactory.getLogger(Receiver.class);

	@RabbitListener(queues="hello")
	public void handleMessage(HelloMessageBean bean) {
		Log.info("Got Hello "+bean.hello);
	}
	
}
