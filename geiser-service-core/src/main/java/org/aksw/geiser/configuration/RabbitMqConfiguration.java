package org.aksw.geiser.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration
{
	static final Logger log = LoggerFactory.getLogger(RabbitMqConfiguration.class);
	
	@Value("${amqp.server.ip:127.0.0.1}")
	private String amqpServerIp;
	
	@Value("${amqp.server.port:5672}")
	private Integer amqpServerPort;
	
	@Value("${amqp.server.username:guest}")
	private String amqpUsername;
	
	@Value("${amqp.server.password:guest}")
	private String amqpPassword;
	
	
    @Bean
    public ConnectionFactory connectionFactory()
    {
    	log.info("Configured AMQP server IP is {}", amqpServerIp);
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(amqpServerIp, amqpServerPort);
        connectionFactory.setUsername(amqpUsername);
        connectionFactory.setPassword(amqpPassword);
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin()
    {
        return new RabbitAdmin(connectionFactory());
    }
    
}
