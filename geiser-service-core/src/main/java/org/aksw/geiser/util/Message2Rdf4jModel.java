package org.aksw.geiser.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;


public class Message2Rdf4jModel {

	private static final Logger log = LoggerFactory.getLogger(Message2Rdf4jModel.class);

	private static final String BASE_URI = "urn:geiser-default-graph";
	
	public static Model convertToModel(Message payload) throws IOException{
		Optional<RDFFormat> rdfFormat = Rio.getParserFormatForMIMEType(payload.getMessageProperties().getContentType());
		if (rdfFormat.isPresent()) {
			try{
				return Rio.parse(new ByteArrayInputStream(payload.getBody()), BASE_URI, rdfFormat.get());
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
		throw new IOException("Failed to parse RDF4J model from message payload.");
		
	}

	public static Message convertToMessage(Model model, RDFFormat rdfFormat) throws IOException{
		RDFFormat format = rdfFormat!=null ? rdfFormat : RDFFormat.JSONLD;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		Rio.write(model, bao, format);
		return  MessageBuilder.withBody(bao.toByteArray()).setContentType(format.getDefaultMIMEType()).build();
	}

}
