package org.aksw.geiser.sparqlfeed;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class SparqlFeedService {

	private static final Logger log = LoggerFactory.getLogger(SparqlFeedService.class);

	private static final RDFFormat DEFAULT_FORMAT = RDFFormat.TURTLE;

	@Value("${routing.key}")
	private String routingKey;

	private TaskScheduler taskScheduler = new ConcurrentTaskScheduler();
	
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Bean
	private Queue sparqlFeedQueue() {
		return new Queue("sparqlfeed", false, false, false);
	}

	@RabbitListener(queues = "sparqlfeed")
	public void handleSparqlFeedRequest(@Payload SparqlFeedRequest request) throws IOException {
		log.info("SparqlFeed got message: {}", request);
		List<String> preparedMessages = prepareMessages(request);
//		ArrayList<String> preparedMessages = Lists.newArrayList("<urn:r1> <urn:p1> <urn:r2>.",
//				"<urn:r1> <urn:p1> <urn:r3>.", "<urn:r1> <urn:p1> <urn:r4>.");
		MessageProducerTask task = new MessageProducerTask(request, preparedMessages, rabbitTemplate);
		ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(task, request.getMessageSendRate());
		task.scheduledFuture = future;
		log.info("SparqlFeed submitted message producer task for {}", request);
	}

	private List<String> prepareMessages(SparqlFeedRequest request) throws IOException {
		Optional<RDFFormat> rdfFormat = Rio.getWriterFormatForMIMEType(request.getRdfFormat());
		if (!rdfFormat.isPresent()) {
			log.warn("Invalid RDF format {} in request, using default text/turtle", request.getRdfFormat(),
					DEFAULT_FORMAT);
		}
		SPARQLRepository repository = new SPARQLRepository(request.getEndpoint());
		repository.initialize();
		try (RepositoryConnection conn = repository.getConnection()) {
			GraphQuery graphQuery = conn.prepareGraphQuery(request.getQuery());
			GraphQueryResult queryResult = graphQuery.evaluate();
			List<String> messages = Lists.newArrayList();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			while (queryResult.hasNext()) {
				Statement statement = queryResult.next();
				Rio.write(statement, outputStream, rdfFormat.orElse(DEFAULT_FORMAT));
				outputStream.flush();
				messages.add(outputStream.toString());
				outputStream.reset();
			}
			return messages;
		}
	}

	private class MessageProducerTask implements Runnable {

		public MessageProducerTask(SparqlFeedRequest request, List<String> preparedMessages, RabbitTemplate rabbitTemplate) {
			super();
			this.rabbitTemplate = rabbitTemplate;
			this.request = request;
			this.queue.addAll(preparedMessages);
		}

		private volatile RabbitTemplate rabbitTemplate;

		private final List<String> queue = Collections.synchronizedList(Lists.newArrayList());

		private SparqlFeedRequest request;

		private ScheduledFuture<?> scheduledFuture;

		public void run() {
			if (request != null) {
				ListIterator<String> it = queue.listIterator();
				if (it.hasNext()) {
					String nextMessage = it.next();
					it.remove();
					log.info("Sending message {}", nextMessage);
					rabbitTemplate.send(routingKey, MessageBuilder.withBody(nextMessage.getBytes())
							.setContentType(request.getRdfFormat()).build());
				} else {
					if (scheduledFuture != null) {
						scheduledFuture.cancel(false);
					}
				}
			}
		}

	}

}
