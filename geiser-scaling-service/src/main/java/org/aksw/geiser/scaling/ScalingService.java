package org.aksw.geiser.scaling;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.aksw.geiser.util.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;

@Component
@EnableScheduling
public class ScalingService {

	public static final String ROUTING_KEY = "scalingservice-v1.#";
	public static final String QUEUE_NAME = "scalingservice-v1";

	private static final Logger log = LoggerFactory.getLogger(ScalingService.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private RabbitAdmin rabbitAdmin;

	@Autowired
	private Map<String, String> mapping;

	@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = QUEUE_NAME)))
	public void handleServiceMessage(@Payload Message payload
	/* , @Headers Map<String, Object> headers */) {

		log.info("Got message {}", payload);

		String queueName = new String(payload.getBody(), Charsets.UTF_8);
		Properties queueProperties = rabbitAdmin.getQueueProperties(queueName);
		Object messageCount = queueProperties.get(RabbitAdmin.QUEUE_MESSAGE_COUNT);
		log.info("Message count in {}: {}", queueName, messageCount);
		// log.info("Message count class: {}", messageCount.getClass());
		int scale = 1 + ((Integer) messageCount) / 10;
		String serviceName = mapping.get(queueName);
		log.info("Setting service scale: {} to {}", serviceName, scale);
		try {
			scale(serviceName, scale);
		} catch (IOException e) {
			log.error("Scaling failed", e);
			e.printStackTrace();
		}

		Message resultMessage = MessageBuilder.withBody(("{\"messages\":\"" + messageCount + "\"}").getBytes())
				.setContentType("application/json;charset=utf-8").build();

		String nextRoutingKey = ServiceUtils.nextRoutingKey(payload);
		log.debug("Sending to {}: {}", nextRoutingKey, resultMessage);
		rabbitTemplate.send("geiser", nextRoutingKey, resultMessage);

	}

	private void scale(String service, int instances) throws IOException {
		String cmdLine = "/usr/bin/docker service scale " + service + "=" + instances;
		log.info("Attempting to execute: {}", cmdLine);
		Process proc = new ProcessBuilder("/usr/bin/docker", "service", "scale", service+"="+instances).inheritIO().directory(new File("/")).start();
		try {
			int exitCode = proc.waitFor();
			log.info("Scaling {} returned {}", service, exitCode);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Scheduled(fixedRate = 1000)
	private void checkQueueLengths() {
//		log.info("Checking");
	}

}
