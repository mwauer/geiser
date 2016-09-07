package de.kuhpfau.amqp.helloworld.receiver;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

/**
 * Global receiver of service discovery ping messages.
 * 
 * @author mwauer
 *
 */
@Component
public class ServiceDiscoveryReceiver {

	private static final Logger log = LoggerFactory.getLogger(ServiceDiscoveryReceiver.class);

	// @Bean
	// public Queue serviceDiscoveryResponseQueue() {
	// return new Queue("service.discovery.response", false, false, true);
	// }
	//
	// /**
	// * Prints incoming service discovery response messages.
	// * @param response
	// */
	// @RabbitListener(queues="service.discovery.response")
	// public void handleServiceDiscoveryResponse(ServiceDiscoveryResponseBean
	// response) {
	// log.info("got service discovery message from {} service {}, which accepts
	// {}", response.type, response.service, response.accept);
	// }

//	@Autowired
//	private AmqpAdmin amqpAdmin;

	@Bean
	public Queue serviceDiscoveryRequestQueue() {
		// amqpAdmin.
		return new Queue("service.discovery.request", false, false, true);
	}

	/**
	 * Provides a service description of the hello world service for incoming
	 * requests.
	 * 
	 * @param request
	 * @return the service discovery response
	 */
	@RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = "service.discovery.request", type = ExchangeTypes.TOPIC), value = @org.springframework.amqp.rabbit.annotation.Queue))
	public ServiceDiscoveryResponse handleServiceDiscoveryRequest(ServiceDiscoveryRequest request,
			@Headers Map<String, Object> headers) {
		log.info("got a service discovery request, headers: {}", headers);
		ServiceDiscoveryResponse responseBean = new ServiceDiscoveryResponse();
		responseBean.service = "HelloWorld";
		responseBean.type = "example";
		//responseBean.accept = null;//"{\"title\": \"Hello World Service Schema\", \"type\": \"object\", \"properties\": { \"Hello\": { \"type\": \"string\" } }, \"required\": [\"Hello\"]}";
		try {
			responseBean.requestSchema = new JsonSchemaGenerator(new ObjectMapper()).generateSchema(HelloMessage.class);
			responseBean.requestSchema.setDescription("A sample hello world service");
		} catch (JsonMappingException e) {
			log.warn("failed to generate json schema for hello message bean:", e);
		}
		return responseBean;
	}

}
