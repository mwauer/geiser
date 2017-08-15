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
	
	@Value("${amqp_server_host:rabbit}")
	private String amqpServerHost;
	
	@Value("${amqp_server_port:5672}")
	private Integer amqpServerPort;
	
	@Value("${amqp_server_username:guest}")
	private String amqpUsername;
	
	@Value("${amqp_server_password:guest}")
	private String amqpPassword;
	
	
    @Bean
    public ConnectionFactory connectionFactory()
    {
    	log.info("Configured AMQP server IP is {}", amqpServerHost);
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(amqpServerHost, amqpServerPort);
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
