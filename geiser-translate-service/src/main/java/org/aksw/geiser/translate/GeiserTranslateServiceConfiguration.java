package org.aksw.geiser.translate;

import org.aksw.geiser.configuration.GeiserJsonServiceConfiguration;
import org.aksw.geiser.translate.yandex.YandexTranslator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeiserTranslateServiceConfiguration extends GeiserJsonServiceConfiguration {
	
	@Value("${translate.api.key}")
	private String apiKey;
	
	@Value("${translate.input.attribute:data/text}")
	private String attributeToTranslate;
	
	@Bean
	public String[] attributePath() {
		System.out.println("Splitting attribute path "+attributeToTranslate);
		return StringUtils.split(attributeToTranslate, '/');
	}
	
	@Bean
	public Translator translator() {
		System.out.println("Configuring YandexTranslator as translator bean");
		return new YandexTranslator();
	}

}
