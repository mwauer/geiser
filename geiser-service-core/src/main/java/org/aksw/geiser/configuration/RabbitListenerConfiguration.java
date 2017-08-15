package org.aksw.geiser.configuration;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitListenerConfiguration extends RabbitMqConfiguration {

//	private final static Logger Log = LoggerFactory.getLogger(RabbitListenerConfiguration.class);

	@Value("${amqp_consumers_concurrent:3}")
	private Integer amqpConcurrentConsumers;

	@Value("${amqp_consumers_max:10}")
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