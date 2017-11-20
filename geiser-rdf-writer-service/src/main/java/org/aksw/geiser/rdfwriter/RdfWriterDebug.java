package org.aksw.geiser.rdfwriter;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RdfWriterDebug {

	public static final String ROUTING_KEY = "rdfwriterdebug-v1.#";
	public static final String QUEUE_NAME = "rdfwriterdebug-v1";

	private static final Logger log = LoggerFactory.getLogger(RdfWriterDebug.class);

	@Autowired
	private Repository repository;

	@Bean
	public Queue rdfWriterDebugQueue() {
		log.info("Creating rdfwriterdebug queue");
		return new Queue("rdfwriterdebug", false, false, false);
	}

	@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = QUEUE_NAME)))
	public void handleRdfWriterDebugMessage(
			@Payload Message payload/* , @Headers Map<String, Object> headers */) {
		log.info("RdfWriterDebug got message: {}", payload);
		Model model = Repositories.graphQuery(repository, "CONSTRUCT WHERE { ?s ?p ?o }",
				r -> QueryResults.asModel(r));
		log.info("Store contents: {}", model);
	}

}
