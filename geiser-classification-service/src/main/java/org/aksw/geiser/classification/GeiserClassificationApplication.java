package org.aksw.geiser.classification;

import java.nio.charset.StandardCharsets;

import org.aksw.geiser.util.ServiceUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Basic GEISER service implementation generated from archetype.
 * 
 * @author wauer
 *
 */
@SpringBootApplication
public class GeiserClassificationApplication {
	
	@Component
	public class GeiserClassificationService {
		
		public static final String ROUTING_KEY = "classification-v1.#";
		public static final String QUEUE_NAME = "classification-v1";
		
		/* // required for sending a message
		 * @Autowired
		 * private RabbitTemplate rabbitTemplate;
		 */
		
		@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = QUEUE_NAME)))
		public void handleTestMessage(
				@Payload Message payload/* , @Headers Map<String, Object> headers */) {
			System.out.println("Got test message: "+new String(payload.getBody()));
			/* // for sending a response message to the next routing key:
			 * Message result = MessageBuilder.withBody("result".getBytes(StandardCharsets.UTF_8))
				.setContentType("text/plain;charset=utf-8").build();
		rabbitTemplate.send(message.getMessageProperties().getReceivedExchange(), ServiceUtils.nextRoutingKey(message),
				result);
			 */
		}
	}
	
	public static void main(String[] args) {
		SpringApplication.run(GeiserClassificationApplication.class, args);
	}
}
