package org.aksw.geiser.testwithrepo;

import java.util.Map;

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
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Example service for GEISER consortium meeting. For details on the Spring AMQP annotations, see
 * http://docs.spring.io/spring-amqp/reference/html/_reference.html#async-annotation-driven .
 * 
 * @author wauer
 *
 */
//@Component
public class TestWithRepoService {

	private static final Logger log = LoggerFactory.getLogger(TestWithRepoService.class);

//	@Bean
//	public Queue testQueue() {
//		return new Queue("test", true, false, true);
//	}
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@RabbitListener(bindings = @QueueBinding(key = "test.#", exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue))
	public void handleTestMessage(
			@Payload Message payload/* , @Headers Map<String, Object> headers */) {
		log.info("got message: {}", payload);
	}
	
	@RabbitListener(bindings = @QueueBinding(key = "trash.#", exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue))
	public void handleTrashMessage(
			@Payload Message payload) {
		log.info("got trash message: {}", payload);
	}
	
	@RabbitListener(bindings = @QueueBinding(key = "forward.#", exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue))
	public void handleForwardMessage(
			@Payload Message payload) {
		log.info("got forward message: {}", payload);
		String routingKey = payload.getMessageProperties().getReceivedRoutingKey();
		int firstSeparatorIndex = routingKey.indexOf(".");
		if (firstSeparatorIndex >= 0) {
			String forwardRoutingKey = routingKey.substring(firstSeparatorIndex+1);
			log.info("forwarding message to {}", forwardRoutingKey);
			rabbitTemplate.send("geiser", forwardRoutingKey, MessageBuilder.fromMessage(payload).build());
		} else {
			log.warn("forwarding requires next routing key element! got {}", routingKey);
		}
	}

}