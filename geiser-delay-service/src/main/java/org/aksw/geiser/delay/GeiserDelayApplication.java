package org.aksw.geiser.delay;

import java.util.Map;

import org.aksw.geiser.util.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Basic GEISER service implementation for delaying a message.
 * 
 * @author wauer
 *
 */
@SpringBootApplication
public class GeiserDelayApplication {

	private static final Logger log = LoggerFactory.getLogger(GeiserDelayApplication.class);

	@Component
	public class GeiserDelayService {

		public static final String ROUTING_KEY = "delay-v1.#";
		public static final String QUEUE_NAME = "delay-v1";

		// required for sending a message
		@Autowired
		private RabbitTemplate rabbitTemplate;

		@Value("${delay:500}")
		private long defaultDelay;

		@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = QUEUE_NAME)))
		public void handleTestMessage(@Payload Message payload, @Headers Map<String, Object> headers) {
			log.info("Got message {}", new String(payload.getBody()));

			// check if there's a custom delay header
			long delay = defaultDelay;
			Object delayObject = headers.get("delay");
			if (delayObject != null) {
				try {
					delay = new Long(delayObject.toString());
					log.info("Using custom delay {}ms", delay);
				} catch (Exception e) {
					log.warn("Message had invalid custom delay header: "+delayObject, e);
				}
			}
			
			// delay
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				log.warn("Delay got interrupted, may not have the expected delay of " + delay + "ms", e);
			}
			
			// sending a response message to the next routing key:
			log.info("Sending message delayed by {}ms", delay);
			Message result = MessageBuilder.fromMessage(payload).build();
			rabbitTemplate.send(result.getMessageProperties().getReceivedExchange(),
					ServiceUtils.nextRoutingKey(payload), result);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(GeiserDelayApplication.class, args);
	}
}
