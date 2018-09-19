package org.aksw.geiser.ner;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.aksw.geiser.util.Message2Rdf4jModel;
import org.aksw.geiser.util.ServiceUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Models;
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
	
	private static final Logger log = LoggerFactory.getLogger(GeiserNERApplication.class);


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
			log.debug("Got NER message: {}", new String(payload.getBody()));
			
			Model model = Message2Rdf4jModel.convertToModel(payload);

			Optional<org.eclipse.rdf4j.model.Value> textObject = Models.object(model.filter(null, SimpleValueFactory.getInstance().createIRI("http://rdfs.org/sioc/ns#content"), null));
			
			List<IRI> entities = wrapper.getEntities(textObject.get().stringValue());
			
			ModelBuilder modelBuilder = new ModelBuilder(model);
			//modelBuilder.subject(subject);
			for (IRI entity : entities) {
				log.debug("Adding NER entity: {}", entity);
				modelBuilder.add(SimpleValueFactory.getInstance().createIRI("http://rdfs.org/sioc/ns#topic_dbpedia"), entity);				
			}
			
			Message message = Message2Rdf4jModel.convertToMessage(model, RDFFormat.JSONLD);
			
			log.debug("Sending NER response: {}", new String(message.getBody()));

			// for sending a response message to the next routing key:
			rabbitTemplate.send(message.getMessageProperties().getReceivedExchange(),
					ServiceUtils.nextRoutingKey(payload), message);

		}
	}

	public static void main(String[] args) {
		SpringApplication.run(GeiserNERApplication.class, args);
	}
}
