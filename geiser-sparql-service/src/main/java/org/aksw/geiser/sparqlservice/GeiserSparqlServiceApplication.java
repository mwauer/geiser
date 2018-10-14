package org.aksw.geiser.sparqlservice;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.aksw.geiser.util.ServiceUtils;
import org.eclipse.rdf4j.common.lang.FileFormat;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriterRegistry;
import org.eclipse.rdf4j.query.resultio.QueryResultFormat;
import org.eclipse.rdf4j.query.resultio.QueryResultIO;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultWriterRegistry;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

/**
 * Basic GEISER service implementation generated from archetype.
 * 
 * @author Johannes Trame <jt@metaphacts.com>
 *
 */
@SpringBootApplication
public class GeiserSparqlServiceApplication {
	private static final Logger log = LoggerFactory.getLogger(GeiserSparqlServiceApplication.class);
	
	enum Operation{ ASK, DESCRIBE,SELECT, CONSTRUCT, UPDATE};

	@Autowired
	private Repository repository;
	
	// required for sending a message
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Component
	public class GeiserSparqlServiceService {
		
		public static final String ROUTING_KEY = "sparql-v1.#";
		public static final String QUEUE_NAME = "sparql-v1";
		
		@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = QUEUE_NAME)))
		public void handleTestMessage(
				@Payload Message payload, @Headers Map<String, String> headers) {
			String sparqlQuery = new String(payload.getBody());
			System.out.println("Got test message: "+sparqlQuery);
			processSparqlQuery(payload, sparqlQuery, headers);

		}
	}

	private void processSparqlQuery(Message message, String sparqlQuery, Map<String, String> headers){
		String typeHeader = headers.get("OPERATION_TYPE");
		String acceptHeader = headers.get("ACCEPT");
		
		Operation op = Operation.SELECT;
		if(typeHeader == null || Operation.valueOf(typeHeader) == null){
			log.warn("OPERATION_TYPE header is null or invalide value. {}", typeHeader);
			log.warn("Using default \"SELECT\". ");
		}else{
			op = Operation.valueOf(typeHeader);
		}
		
		FileFormat returnFormat = null;
		Set<FileFormat> allFormats = Sets.newLinkedHashSet();
        for(RDFFormat format : RDFWriterRegistry.getInstance().getKeys())
        	allFormats.add(format);
        for(QueryResultFormat format : BooleanQueryResultWriterRegistry.getInstance().getKeys())
        	allFormats.add(format);
        for(QueryResultFormat format : TupleQueryResultWriterRegistry.getInstance().getKeys())
        	allFormats.add(format);
        
		try{
			
		if (op.equals(Operation.ASK)) {
			returnFormat = BooleanQueryResultFormat.matchMIMEType(acceptHeader,
					allFormats).orElseGet(
					new Supplier<BooleanQueryResultFormat>() {
						@Override
						public BooleanQueryResultFormat get() {
							log.warn(
									"ACCEPT header is empty or unkown. Fallback to {}.",
									BooleanQueryResultFormat.TEXT
											.getDefaultMIMEType());
							return BooleanQueryResultFormat.TEXT;
						}
					});
			try(ByteArrayOutputStream boa = new ByteArrayOutputStream();){
				try(RepositoryConnection con = repository.getConnection()){
					QueryResultIO.writeBoolean(con.prepareBooleanQuery(QueryLanguage.SPARQL, sparqlQuery).evaluate(), (QueryResultFormat) returnFormat, boa);
				}
				writeResult(message, boa, returnFormat);
			}
		} else if (op.equals(Operation.CONSTRUCT) || op.equals(Operation.DESCRIBE)) {
			returnFormat = RDFFormat.matchMIMEType(acceptHeader, allFormats)
					.orElseGet(new Supplier<RDFFormat>() {
						@Override
						public RDFFormat get() {
							log.warn(
									"ACCEPT header is empty or unkown. Fallback to {}.",
									RDFFormat.JSONLD.getDefaultMIMEType());
							return RDFFormat.JSONLD;
						}
					});
			try(ByteArrayOutputStream boa = new ByteArrayOutputStream();){
				try(RepositoryConnection con = repository.getConnection()){
					QueryResultIO.writeGraph(con.prepareGraphQuery(QueryLanguage.SPARQL, sparqlQuery).evaluate(), (RDFFormat) returnFormat, boa);
				}
				writeResult(message, boa, returnFormat);
			}


		} else if (op.equals(Operation.SELECT)) {
			returnFormat = TupleQueryResultFormat.matchMIMEType(acceptHeader,
					allFormats).orElseGet(
					new Supplier<TupleQueryResultFormat>() {
						@Override
						public TupleQueryResultFormat get() {
							log.warn(
									"ACCEPT header is empty or unkown. Fallback to {}.",
									TupleQueryResultFormat.JSON
											.getDefaultMIMEType());
							return TupleQueryResultFormat.JSON;
						}
					});
			try(ByteArrayOutputStream boa = new ByteArrayOutputStream();){
				try(RepositoryConnection con = repository.getConnection()){
					QueryResultIO.writeTuple(con.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery).evaluate(), (QueryResultFormat) returnFormat, boa);
				}
				writeResult(message, boa, returnFormat);
			}

		}
			
		
			
		}catch(Exception e){
			log.error("Failed to execute SPARQL query on the repository connection.", e);
		}

	}
	
	private void writeResult(Message originalMessage, ByteArrayOutputStream boa, FileFormat format){
		Message result = MessageBuilder.withBody(boa.toByteArray())
		.setContentType(format.getDefaultMIMEType()).build();
		rabbitTemplate.send(originalMessage.getMessageProperties().getReceivedExchange(),
		ServiceUtils.nextRoutingKey(originalMessage), result);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(GeiserSparqlServiceApplication.class, args);
	}
}
