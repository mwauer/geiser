package org.aksw.geiser.deer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

import org.aksw.deer.helper.datastructure.RunContext;
import org.aksw.deer.workflow.rdfspecs.RDFConfigExecutor;
import org.aksw.geiser.util.ServiceUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jena.rdf.model.Model;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Basic GEISER DEER service implementation generated from archetype.
 * 
 * @author wauer
 *
 */
@SpringBootApplication
public class GeiserDeerApplication {

	private static final Logger log = LoggerFactory.getLogger(GeiserDeerApplication.class);

	@Component
	public class GeiserDeerService {

		public static final String ROUTING_KEY = "deer-v1.#";
		public static final String QUEUE_NAME = "deer-v1";

		// required for sending a message
		@Autowired
		private RabbitTemplate rabbitTemplate;

		// use value annotations to get environment properties, e.g., set on
		// docker invocation
		@Value("${example_property:default-value}")
		private String exampleProperty;

		@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = QUEUE_NAME)))
		public void handleMessage(
				@Payload Message payload/*
										 * , @Headers Map<String, Object>
										 * headers
										 */) {

			try {
				// store incoming config into temp file
				RDFConfigExecutor executor = getExecutor(payload);

				// run executor
				Set<Model> resultModels = executor.execute();

				// write out first model as TTL
				String fusedModel = getFusedTurtle(resultModels);

				// Sending the result as a response message to the next routing key:
				Message result = MessageBuilder.withBody(fusedModel.getBytes(StandardCharsets.UTF_8))
						.setContentType("text/turtle;charset=utf-8").build();
				rabbitTemplate.send(payload.getMessageProperties().getReceivedExchange(),
						ServiceUtils.nextRoutingKey(payload), result);

			} catch (Exception e) {
				String errorMessage = "Failed to execute DEER fusion: " + e.getMessage();
				log.error(errorMessage, e);
				Message result = MessageBuilder.withBody(errorMessage.getBytes(StandardCharsets.UTF_8))
						.setContentType("text/plain;charset=utf-8").build();
				rabbitTemplate.send(payload.getMessageProperties().getReceivedExchange(),
						ServiceUtils.nextRoutingKey(payload), result);
			}

		}

		private static final String STORAGE_DIR_PATH = "/tmp/deer/";
		private static final String CONFIG_FILE_PREFIX = "deer_cfg_";
		private static final String CONFIG_FILE_SUFFIX = ".ttl";

		private RDFConfigExecutor getExecutor(Message payload) throws IOException {
			// prepare temporary paths
			Path storageDirPath = new File(STORAGE_DIR_PATH).toPath();
			if (!Files.isDirectory(storageDirPath)) {
				Files.createDirectory(storageDirPath);
			}
			Path tempFile = Files.createTempFile(storageDirPath, CONFIG_FILE_PREFIX,
					CONFIG_FILE_SUFFIX);
			
			log.debug("Storing configuration at {}", tempFile);
			Files.copy(new ByteArrayInputStream(payload.getBody()), tempFile, StandardCopyOption.REPLACE_EXISTING);
			log.debug("Parsing configuration ID");
			String id = StringUtils.substringBetween(tempFile.toString(), CONFIG_FILE_PREFIX, CONFIG_FILE_SUFFIX);
			log.debug("Got configuration ID: {}", id);
			
			RDFConfigExecutor executor = new RDFConfigExecutor(tempFile.toString(),
					new RunContext(Long.parseLong(id), ""));
			log.info("Prepared DEER executor for ID: {}", id);
			return executor;
		}
		
		private String getFusedTurtle(Set<Model> resultModels) {
			if (resultModels.size() == 0) {
				log.warn("No result models, will return empty result");
				return "";
			}
			if (resultModels.size() > 1) {
				log.warn("Configuration returned {} result models, will only use the first", resultModels.size());
			}
			StringWriter stringWriter = new StringWriter();
			Model model = resultModels.iterator().next();
			model.write(stringWriter, "TTL");
			log.debug("Got fused result of size {}", stringWriter.getBuffer().length());
			return stringWriter.toString();
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(GeiserDeerApplication.class, args);
	}
}
