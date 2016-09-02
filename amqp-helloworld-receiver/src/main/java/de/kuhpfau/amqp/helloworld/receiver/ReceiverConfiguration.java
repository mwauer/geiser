package de.kuhpfau.amqp.helloworld.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReceiverConfiguration extends RabbitMqConfiguration {

	private final static Logger Log = LoggerFactory.getLogger(ReceiverConfiguration.class);

	protected final String receiverQueue = "hello";

	@Autowired
	private Receiver receiver;

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
		rabbitTemplate.setRoutingKey(receiverQueue);
		rabbitTemplate.setQueue(receiverQueue);
		// rabbitTemplate.setMessageConverter(jsonMessageConverter());
		Log.info("Created rabbit template");
		return rabbitTemplate;
	}

	@Bean
	public Queue receiverQueue() {
		return new Queue(receiverQueue, false);
	}

	@Bean
	public SimpleMessageListenerContainer listenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		container.setQueueNames(receiverQueue);
		container.setMessageListener(messageListenerAdapter());
		// container.setConsumerTagStrategy(new ConsumerTagStrategy() {
		//
		// @Override
		// public String createConsumerTag(String arg0) {
		// System.out.println("Creating consumer tag for "+arg0);
		// return "myConsumerTag-"+arg0;
		// }
		// });
		// container.setAcknowledgeMode(AcknowledgeMode.NONE);
		Log.info("Configured listener container");
		return container;
	}

	@Bean
	public MessageListenerAdapter messageListenerAdapter() {
		return new MessageListenerAdapter(receiver); // ,
														// jsonMessageConverter());
	}

}
