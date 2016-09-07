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
	public ServiceDiscoveryResponseBean handleServiceDiscoveryRequest(ServiceDiscoveryRequestBean request,
			@Headers Map<String, Object> headers) {
		log.info("got a service discovery request, headers: {}", headers);
		ServiceDiscoveryResponseBean responseBean = new ServiceDiscoveryResponseBean();
		responseBean.service = "HelloWorld";
		responseBean.type = "example";
		responseBean.accept = "{\"title\": \"Hello World Service Schema\", \"type\": \"object\", \"properties\": { \"Hello\": { \"type\": \"string\" } }, \"required\": [\"Hello\"]}";
		return responseBean;
	}

}
