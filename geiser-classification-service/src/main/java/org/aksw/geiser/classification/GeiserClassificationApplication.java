package org.aksw.geiser.classification;

import java.io.IOException;

import org.aksw.geiser.util.Message2Rdf4jModel;
import org.aksw.geiser.util.ServiceUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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

	private static final Logger log = LoggerFactory.getLogger(GeiserClassificationApplication.class);
	
	@Component
	public class GeiserClassificationService {

		public static final String ROUTING_KEY = "classification-v1.#";
		public static final String QUEUE_NAME = "classification-v1";

		@Autowired
		private GeiserClassificationConfig config;

		// required for sending a message
		@Autowired
		private RabbitTemplate rabbitTemplate;

		@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = QUEUE_NAME)))
		public void handleClassificationMessage(
				@Payload Message payload/*
										 * , @Headers Map<String, Object>
										 * headers
										 */) throws IOException, ClassificationProcessingException {
			log.debug("Got classification message: " + new String(payload.getBody()));
			Model model = Message2Rdf4jModel.convertToModel(payload);
			long numStatements = model.size();
			Model result = process(model);
			log.info("Classification result: Retained {} of {} statements", result.size(), numStatements);
			Message message = Message2Rdf4jModel.convertToMessage(result, RDFFormat.TURTLE);
			// for sending a response message to the next routing key:
			rabbitTemplate.send(payload.getMessageProperties().getReceivedExchange(),
					ServiceUtils.nextRoutingKey(payload), message);
		}

		private Model process(Model model) throws ClassificationProcessingException {
			ClassificationProcessor processor = ClassificationProcessorFactory.getInstance(config);
			return processor.process(model, config);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(GeiserClassificationApplication.class, args);
	}
}
