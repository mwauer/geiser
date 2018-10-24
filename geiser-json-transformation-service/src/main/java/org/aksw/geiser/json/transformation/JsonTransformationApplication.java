package org.aksw.geiser.json.transformation;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.aksw.geiser.util.ServiceUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageBuilderSupport;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

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


		@Value("${response_content_type:application/ld+json;charset=utf-8}")
		private String contentType;

		LoadingCache<URL, String> jqFilterCache = CacheBuilder.newBuilder()
			       .maximumSize(1000)
			       .expireAfterWrite(10, TimeUnit.MINUTES)
			       .build(
			           new CacheLoader<URL, String>() {
						@Override
						public String load(URL jqResource) throws Exception {
							log.info("Setting up jqFilter from {}", jqResource);
							String jqFilter = IOUtils.toString(jqResource.openStream());
							log.info("Set up jqFilter: {}", jqFilter);
							return jqFilter;
						}
			           });

		@Autowired
		private JsonRdfTransformator jsonRdfTransformator;

		@Autowired
		private Model baseModel;

		@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = QUEUE_NAME)))
		public void handleTestMessage(
				@Payload Message message, @Headers Map<String, String> headers) throws IOException, ExecutionException {

			if(!headers.containsKey("jqFile")){
				throw new IOException("jqFile declaration missing in header request header.");
			}
			
			URL jqResource = JsonTransformationService.class.getClassLoader().getResource(headers.get("jqFile"));
			String jqFilter = jqFilterCache.get(jqResource);
			String transformed = jsonRdfTransformator.transform(new String(message.getBody()), jqFilter, baseModel);

			// sending a response message to the next routing key:
			MessageBuilderSupport<Message> messageBuilder = MessageBuilder.withBody(transformed.toString().getBytes(StandardCharsets.UTF_8))
					.setContentType(contentType);
			
			//forward initial headers
			for( Entry<String, Object> h : message.getMessageProperties().getHeaders().entrySet()){
				messageBuilder.setHeaderIfAbsent(h.getKey(), h.getValue());
			}
			Message newMessage = messageBuilder.build();
			rabbitTemplate.send(message.getMessageProperties().getReceivedExchange(),
					ServiceUtils.nextRoutingKey(message), newMessage);

		}


	}

	public static void main(String[] args) {
		SpringApplication.run(JsonTransformationApplication.class, args);
	}
}
