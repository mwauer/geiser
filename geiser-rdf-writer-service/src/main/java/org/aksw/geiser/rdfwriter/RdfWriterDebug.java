package org.aksw.geiser.rdfwriter;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RdfWriterDebug {

	private static final Logger log = LoggerFactory.getLogger(RdfWriterDebug.class);

	@Autowired
	private Repository repository;

	@Bean
	public Queue rdfWriterDebugQueue() {
		log.info("Creating rdfwriterdebug queue");
		return new Queue("rdfwriterdebug", false, false, false);
	}

	@RabbitListener(queues = "rdfwriterdebug")
	public void handleRdfWriterDebugMessage(
			@Payload Message payload/* , @Headers Map<String, Object> headers */) {
		log.info("RdfWriterDebug got message: {}", payload);
		Model model = Repositories.graphQuery(repository, "CONSTRUCT WHERE { ?s ?p ?o }",
				r -> QueryResults.asModel(r));
		log.info("Store contents: {}", model);
	}

}