package org.aksw.geiser.json.transformation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.aksw.geiser.util.ServiceUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.model.Model;
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
import org.springframework.core.io.Resource;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Basic GEISER service implementation generated from archetype.
 * 
 * @author wauer
 *
 */
@SpringBootApplication
public class JsonTransformationApplication {

	private static final Logger log = LoggerFactory.getLogger(JsonTransformationApplication.class);

	@Component
	public class JsonTransformationService {

		public static final String ROUTING_KEY = "jsontransformation-v1.#";
		public static final String QUEUE_NAME = "jsontransformation-v1";

		// required for sending a message
		@Autowired
		private RabbitTemplate rabbitTemplate;

		@Value("${jq_file}")
		private Resource jqFile;

		@Value("${response_content_type:application/ld+json;charset=utf-8}")
		private String contentType;

		private String jqFilter;

		@Autowired
		private JsonRdfTransformator jsonRdfTransformator;

		@Autowired
		private Model baseModel;

		@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = QUEUE_NAME)))
		public void handleTestMessage(
				@Payload Message message/*
										 * , @Headers Map<String, Object>
										 * headers
										 */) throws IOException {
			// System.out.println("Got test message: " + new
			// String(message.getBody()));

			getJqFilter();
			// old:
			// Model transformed = jsonRdfTransformator.transform(new
			// String(message.getBody()), jqUriMapping, jqRdfMapping,
			// baseModel);
			String transformed = jsonRdfTransformator.transform(new String(message.getBody()), jqFilter, baseModel);

			// sending a response message to the next routing key:
			Message result = MessageBuilder.withBody(transformed.toString().getBytes(StandardCharsets.UTF_8))
					.setContentType(contentType).build();
			rabbitTemplate.send(message.getMessageProperties().getReceivedExchange(),
					ServiceUtils.nextRoutingKey(message), result);

		}

		private void getJqFilter() throws IOException {
			if (this.jqFilter == null) {
				log.info("Setting up jqFilter from {}", jqFile);
				jqFilter = IOUtils.toString(jqFile.getInputStream());
				log.info("Set up jqFilter: {}", jqFilter);
			}
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(JsonTransformationApplication.class, args);
	}
}
