package org.aksw.geiser.limes;

import org.aksw.geiser.configuration.GeiserServiceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class GeiserLimesServiceApplication {

	@Configuration
	public class GeiserSparqlifyServiceConfiguration extends GeiserServiceConfiguration {
		
	}
	
	public static void main(String[] args) {
		SpringApplication.run(GeiserLimesServiceApplication.class, args);
	}
}
