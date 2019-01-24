package org.aksw.geiser.translate;

import org.aksw.geiser.configuration.GeiserServiceConfiguration;
import org.aksw.geiser.translate.yandex.YandexTranslator;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeiserTranslateServiceConfiguration extends GeiserServiceConfiguration {

	private static final Logger log = LoggerFactory.getLogger(GeiserTranslateServiceConfiguration.class);

	@Bean
	public Translator translator() {
		log.info("Configuring YandexTranslator as translator bean");
		return new YandexTranslator();
	}

	@Bean
	public ValueFactory valueFactory() {
		return SimpleValueFactory.getInstance();
	}
}
