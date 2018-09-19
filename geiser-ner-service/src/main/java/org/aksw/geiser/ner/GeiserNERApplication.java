package org.aksw.geiser.ner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.aksw.geiser.util.ServiceUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
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
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Basic GEISER service implementation generated from archetype.
 * 
 * @author wauer
 *
 */
@SpringBootApplication
public class GeiserNERApplication {

	@Autowired
	private SimpleNERWrapper wrapper;

	@Component
	public class GeiserNERService {

		public static final String ROUTING_KEY = "ner-v1.#";
		public static final String QUEUE_NAME = "ner-v1";

		// required for sending a message
		@Autowired
		private RabbitTemplate rabbitTemplate;

		//
		@Value("${example_property:default-value}")
		private String exampleProperty;

		@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = QUEUE_NAME)))
		public void handleTestMessage(@Payload Message payload) throws IOException {
			System.out.println("Got test message: " + new String(payload.getBody()));

			List<IRI> entities = wrapper.getEntities("test");
			
			ModelBuilder modelBuilder = new ModelBuilder();
			//modelBuilder.subject(subject);
			for (IRI entity : entities) {
				modelBuilder.add(SimpleValueFactory.getInstance().createIRI("http://rdfs.org/sioc/ns#topic_dbpedia"), entity);				
			}

			// for sending a response message to the next routing key:
//			Message result = MessageBuilder.withBody("result".getBytes(StandardCharsets.UTF_8))
//					.setContentType("text/plain;charset=utf-8").build();
//			rabbitTemplate.send(message.getMessageProperties().getReceivedExchange(),
//					ServiceUtils.nextRoutingKey(message), result);

		}
	}

	public static void main(String[] args) {
		SpringApplication.run(GeiserNERApplication.class, args);
	}
}
