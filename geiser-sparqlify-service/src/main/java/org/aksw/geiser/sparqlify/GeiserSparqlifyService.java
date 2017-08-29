package org.aksw.geiser.sparqlify;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.aksw.geiser.util.ServiceUtils;
import org.antlr.runtime.RecognitionException;
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
public class GeiserSparqlifyService {

	public static final String ROUTING_KEY = "sparqlify.#";

	private static final Logger log = LoggerFactory.getLogger(GeiserSparqlifyService.class);

	private SparqlifyRequestProcessor processor;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public GeiserSparqlifyService() {
		super();
		this.processor = new SparqlifyRequestProcessor();
	}

	@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue))
	public void handleSparqlifyServiceRequest(SparqlifyServiceRequest request, @Payload Message message)
			throws IOException, RecognitionException {
		log.info("got {}", request);
		String resultTurtle = processor.process(request);
		Message result = MessageBuilder.withBody(resultTurtle.getBytes(StandardCharsets.UTF_8))
				.setContentType("text/turtle;charset=utf-8").build();
		rabbitTemplate.send(message.getMessageProperties().getReceivedExchange(), ServiceUtils.nextRoutingKey(message),
				result);
	}

}
