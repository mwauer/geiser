package de.kuhpfau.amqp.helloworld.receiver;

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
	
	@Value("${amqp.server.ip}")
	private String amqpServerIp;
	
    @Bean
    public ConnectionFactory connectionFactory()
    {
    	log.info("Configured AMQP server IP is {}", amqpServerIp);
    	//System.out.println("server ip is "+amqpServerIp);
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(amqpServerIp);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin()
    {
        return new RabbitAdmin(connectionFactory());
    }


//    @Bean
//    public MessageConverter jsonMessageConverter()
//    {
//        return new Jackson2JsonMessageConverter();
//    }
}
