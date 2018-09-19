package org.aksw.geiser.ner;

import org.aksw.geiser.configuration.GeiserServiceConfiguration;
import org.aksw.geiser.ner.fox.FoxNERWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeiserNERConfig extends GeiserServiceConfiguration {
	
	@Value("${ner_framework:fox}")
	private String nerFramework;
	
	@Bean
	public SimpleNERWrapper wrapper() {
		switch (nerFramework) {
		case "fox":
			return foxNERWrapper;

		default:
			throw new IllegalArgumentException("Unsupported NER framework: "+nerFramework);
		}
	}
	
	@Autowired
	private FoxNERWrapper foxNERWrapper;

}
