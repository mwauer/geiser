package org.aksw.geiser.sparqlfeed;

import org.aksw.geiser.configuration.GeiserJsonServiceConfiguration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparqlFeedServiceConfiguration extends GeiserJsonServiceConfiguration {
	
	@Autowired
	private ConnectionFactory connectionFactory;
	
	@Bean
	public RabbitTemplate rabbitTemplate () {
		return new RabbitTemplate(connectionFactory);
	}
	
}
