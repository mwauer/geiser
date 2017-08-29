package org.aksw.geiser.sparqlify;

import org.aksw.geiser.configuration.GeiserJsonServiceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class GeiserSparqlifyServiceApplication {
	
	@Configuration
	public class GeiserSparqlifyServiceConfiguration extends GeiserJsonServiceConfiguration {
		
	}

	public static void main(String[] args) {
		SpringApplication.run(GeiserSparqlifyServiceApplication.class, args);
	}
}
