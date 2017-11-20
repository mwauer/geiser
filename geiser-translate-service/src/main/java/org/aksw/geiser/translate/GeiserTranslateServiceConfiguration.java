package org.aksw.geiser.translate;

import org.aksw.geiser.configuration.GeiserJsonServiceConfiguration;
import org.aksw.geiser.translate.yandex.YandexTranslator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeiserTranslateServiceConfiguration extends GeiserJsonServiceConfiguration {

	private static final Logger log = LoggerFactory.getLogger(GeiserTranslateServiceConfiguration.class);

	@Bean
	public String[] attributePath(@Value("${translate_input_attribute:data\\text}") String attributeToTranslate) {
		log.info("Splitting attribute path {}", attributeToTranslate);
		String[] split = StringUtils.split(attributeToTranslate, '\\');
		log.debug("Input attribute path: {}", (Object) split);
		return split;
	}

	@Bean
	public Translator translator() {
		log.info("Configuring YandexTranslator as translator bean");
		return new YandexTranslator();
	}
	
	@Bean
	public String[] resultPath(@Value("${translate_result_attribute:data\\text}") String attributeToStoreResult) {
		log.info("Splitting result attribute path {}", attributeToStoreResult);
		String[] split = StringUtils.split(attributeToStoreResult, '\\');
		log.debug("Result attribute path: {}", (Object) split); 
		return split;
	}

}
