package org.aksw.geiser.limes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.aksw.geiser.limes.LimesRequestProcessor.ResultFiles;
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
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class GeiserLimesService {

	public static final String QUEUE_NAME = "limes-v1";
	public static final String ROUTING_KEY = "limes-v1.#";

	private static final Logger log = LoggerFactory.getLogger(GeiserLimesService.class);

	@Autowired
	private LimesRequestProcessor processor;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public GeiserLimesService() {
		super();
		this.processor = new LimesRequestProcessor();
	}

	/**
	 * Initial version, takes a LIMES configuration for processing under the
	 * messageId job, sends accepted results as message to next routing key
	 * 
	 * @param message a LIMES xml configuration
	 * @throws IOException
	 */
	@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete="true", value=QUEUE_NAME)))
	public void handleLimesServiceRequest(@Payload Message message) { // throws IOException {
		log.info("got {}", message);
		try {
			ResultFiles resultFiles = processor.process(message);
			log.info("Got {}", resultFiles);
			String resultContent = processor.getResult(resultFiles.getAcceptance());
			Message result = MessageBuilder.withBody(resultContent.getBytes(StandardCharsets.UTF_8))
					.setContentType("text/turtle;charset=utf-8").build();
			rabbitTemplate.send(message.getMessageProperties().getReceivedExchange(), ServiceUtils.nextRoutingKey(message),
					result);
		} catch (IOException e) {
			log.error("Failed to handle LIMES request:", e);
			rabbitTemplate.send(message.getMessageProperties().getReceivedExchange(), message.getMessageProperties().getReplyTo(),
					MessageBuilder.withBody(("Failed to process LIMES request: "+e.getMessage()).getBytes(StandardCharsets.UTF_8))
					.setContentType("text/plain;charset=utf-8").build());
		}
	}

}
