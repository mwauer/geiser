package org.aksw.geiser.testwithrepo;

import org.aksw.geiser.configuration.GeiserServiceConfiguration;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class TestWithRepoApplication {

	@Configuration
	public class TestConfig extends GeiserServiceConfiguration {

	}
	
	@Component
	public class TestService {
		
		@RabbitListener(bindings = @QueueBinding(key = "test.#", exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue))
		public void handleTestMessage(
				@Payload Message payload/* , @Headers Map<String, Object> headers */) {
			System.out.println("Got test message: "+new String(payload.getBody()));
		}
	}
	
	public static void main(String[] args) {
		SpringApplication.run(TestWithRepoApplication.class, args);
	}
}
