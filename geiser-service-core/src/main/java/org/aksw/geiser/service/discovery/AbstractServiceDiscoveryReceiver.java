package org.aksw.geiser.service.discovery;

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
public abstract class AbstractServiceDiscoveryReceiver {

	private static final Logger log = LoggerFactory.getLogger(AbstractServiceDiscoveryReceiver.class);

	protected static JsonSchemaGenerator generator = new JsonSchemaGenerator(new ObjectMapper());

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

	// @Autowired
	// private AmqpAdmin amqpAdmin;

	@Bean
	public Queue serviceDiscoveryRequestQueue() {
		return new Queue("service.discovery.request", false, false, true);
	}

	/**
	 * Provides a service description of the hello world service for incoming
	 * requests.
	 * 
	 * TODO: test if we can override the exchange key (routing key) in concrete
	 * classes.
	 * 
	 * @param request
	 * @return the service discovery response
	 */
	@RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = "service.discovery.request", type = ExchangeTypes.TOPIC), value = @org.springframework.amqp.rabbit.annotation.Queue))
	public ServiceDiscoveryResponse handleServiceDiscoveryRequest(ServiceDiscoveryRequest request,
			@Headers Map<String, Object> headers) {
		
		ServiceDiscoveryResponse responseBean = new ServiceDiscoveryResponse();
		responseBean.service = getServiceName();
		responseBean.type = getServiceType();
		try {
			responseBean.requestSchema = generator.generateSchema(getRequestMessageClass());
			responseBean.requestSchema.setDescription(getRequestMessageDescription());
			if (getResponseMessageClass() != null) {
				responseBean.responseSchema = generator.generateSchema(getResponseMessageClass());
				responseBean.responseSchema.setDescription(getResponseMessageDescription());
			}
		} catch (JsonMappingException e) {
			log.warn("failed to generate json schema for hello message bean:", e);
		}
		return responseBean;
	}

	/**
	 * @return the service name to be included in the service discovery response
	 */
	public abstract String getServiceName();

	/**
	 * @return the service tpe to be included in hte service discovery response
	 */
	public abstract String getServiceType();

	/**
	 * @return the class describing a request to the service, from which the
	 *         JSON schema will be generated.
	 */
	public abstract Class<?> getRequestMessageClass();

	/**
	 * @return a description of a request to the service, to be added to the
	 *         JSON schema.
	 */
	public abstract String getRequestMessageDescription();

	/**
	 * @return the class describing the response of the service, from which the
	 *         JSON schema will be generated. May return <code>null</code>.
	 */
	public Class<?> getResponseMessageClass() {
		return null;
	}

	/**
	 * @return a description of a response of the service, to be added to the
	 *         JSON schema.
	 */
	public String getResponseMessageDescription() {
		return null;
	}

}
