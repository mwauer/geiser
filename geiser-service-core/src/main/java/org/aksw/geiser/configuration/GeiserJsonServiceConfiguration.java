package org.aksw.geiser.configuration;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class GeiserJsonServiceConfiguration extends GeiserServiceConfiguration implements InitializingBean {
	
	@Autowired
	private SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory;
	
	public void afterPropertiesSet() throws Exception {
		// set json converter
		rabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
		log.info("Configured JSON converter");
	}

}
