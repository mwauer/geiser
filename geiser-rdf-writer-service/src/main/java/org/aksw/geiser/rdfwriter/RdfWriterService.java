package org.aksw.geiser.rdfwriter;

import java.util.Map;

import org.aksw.geiser.util.Message2Rdf4jModel;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
	
@Component
public class RdfWriterService {
	
	public static final String ROUTING_KEY = "rdfwriter-v1.#";
	public static final String QUEUE_NAME = "rdfwriter-v1";

	private static final Logger log = LoggerFactory.getLogger(RdfWriterService.class);

	private static final String BASE_URI = "urn:geiser-default-graph";

	@Autowired
	private Repository repository;

	@Value("${log_only:false}")
	private boolean logOnly;

	static final ValueFactory vf = SimpleValueFactory.getInstance();
	
	@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = QUEUE_NAME)))
	public void handleRdfWriterMessage(
			@Payload Message payload, @Headers Map<String, String> headers) {
		log.info("RdfWriter got message: {}", payload);
		if (logOnly) {
			log.info("Message content: {}", new String(payload.getBody()));
		} else {
			writeRdf(payload, headers);
		}
	}

	private void writeRdf(Message payload, Map<String, String> headers) {
	
		IRI namedGraphIri = vf.createIRI(headers.getOrDefault("graph_uri", BASE_URI));

		try(RepositoryConnection con = repository.getConnection()){
			con.add(Message2Rdf4jModel.convertToModel(payload), namedGraphIri);
		}catch(Exception e){
			log.error("Failed to parse incoming payload or writing RDF statements to the repository.", e);
		}
		

	}

}
