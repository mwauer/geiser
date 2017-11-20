package org.aksw.geiser.rdfwriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RdfWriterService {

	private static final Logger log = LoggerFactory.getLogger(RdfWriterService.class);

	private static final String BASE_URI = "urn:geiser-default-graph";

	@Autowired
	private Repository repository;

	@Value("${log_only:false}")
	private boolean logOnly;

	@RabbitListener(bindings = @QueueBinding(key = "rdfwriter.#", exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = "rdfwriter")))
	public void handleRdfWriterMessage(
			@Payload Message payload/* , @Headers Map<String, Object> headers */) {
		log.info("RdfWriter got message: {}", payload);
		if (logOnly) {
			log.info("Message content: {}", new String(payload.getBody()));
		} else {
			writeRdf(payload);
		}
	}

	private void writeRdf(Message payload) {
		Optional<RDFFormat> rdfFormat = Rio.getParserFormatForMIMEType(payload.getMessageProperties().getContentType());
		if (rdfFormat.isPresent()) {
			try (RepositoryConnection con = repository.getConnection()) {
				// this would be the simple option, but for debugging purposes I
				// split the parsing and writing:
				// con.add(new ByteArrayInputStream(payload.getBody()),
				// BASE_URI, rdfFormat.get());
				Model model = Rio.parse(new ByteArrayInputStream(payload.getBody()), BASE_URI, rdfFormat.get());
				con.add(model);
				log.info("Successfully wrote statements from message: {}", model);
			} catch (RDFParseException e) {
				log.error("Failed to parse incoming message", e);
				log.error("Message payload: {}", new String(payload.getBody()));
			} catch (UnsupportedRDFormatException e) {
				log.error("Found RDF format, but it is not supported", e);
			} catch (IOException e) {
				log.error("Failed to add incoming message to repository", e);
			}
		} else {
			log.error("Unsupported RDF format of message: {}", payload.getMessageProperties().getContentType());
		}
	}

}
