package org.aksw.geiser.configuration;

import org.aksw.geiser.configuration.RabbitMqConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitListenerConfiguration extends RabbitMqConfiguration {

	private final static Logger Log = LoggerFactory.getLogger(RabbitListenerConfiguration.class);
	
	@Value("${amqp.consumers.concurrent:3}")
	private Integer amqpConcurrentConsumers;
	
	@Value("${amqp.consumers.max:10}")
	private Integer amqpMaxConcurrentConsumers;


	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory());
		factory.setConcurrentConsumers(amqpConcurrentConsumers);
		factory.setMaxConcurrentConsumers(amqpMaxConcurrentConsumers);
		return factory;
	}

}