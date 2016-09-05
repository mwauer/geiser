package de.kuhpfau.amqp.helloworld.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Global receiver of service discovery ping messages.
 * @author mwauer
 *
 */
@Component
public class ServiceDiscoveryReceiver {
	
	private static final Logger log = LoggerFactory.getLogger(ServiceDiscoveryReceiver.class);
	
	@Bean
	public Queue serviceDiscoveryResponseQueue() {
		return new Queue("service.discovery.response", false, false, true);
	}
	
	/**
	 * Prints incoming service discovery response messages.
	 * @param response
	 */
	@RabbitListener(queues="service.discovery.response")
	public void handleServiceDiscoveryResponse(ServiceDiscoveryResponseBean response) {
		log.info("got service discovery message from {} service {}, which accepts {}", response.type, response.service, response.accept);
	}
	
	@Bean
	public Queue serviceDiscoveryRequestQueue() {
		return new Queue("service.discovery.request", false, false, true);
	}
	
	@RabbitListener(queues="service.discovery.request")
	public ServiceDiscoveryResponseBean handleServiceDiscoveryRequest(ServiceDiscoveryRequestBean request) {
		log.info("got a service discovery request");
		ServiceDiscoveryResponseBean responseBean = new ServiceDiscoveryResponseBean();
		responseBean.service = "HelloWorld";
		responseBean.type = "example";
		responseBean.accept = "{\"title\": \"Hello World Service Schema\", \"type\": \"object\", \"properties\": { \"Hello\": { \"type\": \"string\" } }, \"required\": [\"Hello\"]}";
		return responseBean;	
	}

}
