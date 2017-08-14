package org.aksw.geiser.translate;

import java.nio.charset.StandardCharsets;

import org.aksw.geiser.translate.Translator.TranslationResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class TranslateService {

	private static final Logger log = LoggerFactory.getLogger(TranslateService.class);
	
	@Autowired
	private String[] attributePath = new String[] {"data", "text"};
	
	@Autowired
	private Translator translator;
	
	@Bean
	public Queue translateQueue() {
		return new Queue("translate", false, false, false);
	}

	@RabbitListener(queues = "translate")
	// TODO don't hardcode
	@SendTo("fox-v1")
	public Message handleTranslateMessage(
			@Payload Message payload/* , @Headers Map<String, Object> headers */) throws JSONException {
		
		log.debug("Got message {}", payload);
		
		JSONObject jsonObject = new JSONObject(new String(payload.getBody(), StandardCharsets.UTF_8));
		log.debug(" parsed json object {}", jsonObject);
		
		JSONObject parentElement = getParentElement(jsonObject);
		String attribute = attributePath[attributePath.length-1];
		Object attributeValue = parentElement.get(attribute);
		log.debug(" fetched attribute value: {}", attributeValue);
		
		TranslationResult translated = translate(attributeValue.toString());
		log.debug(" translated into: {}", translated);
		
//		parentElement.remove(attribute);
		parentElement.put(attribute, translated.getOutput());
		JSONObject translation = new JSONObject();
		translation.put("original", attributeValue.toString());
		if (translated.getDetectedLanguage().isPresent()) {
			translation.put("detectedSourceLanguage", translated.getDetectedLanguage());
		}
		parentElement.append("translation", translation);
		
		String resultJsonString = jsonObject.toString();
		log.debug(" updated in json: {}", resultJsonString);
		
		return MessageBuilder.withBody(resultJsonString.getBytes()).setContentType("application/json").build();
		
	}
	
	private TranslationResult translate(String input) {
		return translator.translate(input, "EN");
	}

	private JSONObject getParentElement(JSONObject payload) throws JSONException {
		JSONObject currentElement = payload;
		for (int i = 0; i < attributePath.length-1; i++) {
			String attributePathElement = attributePath[i];
			currentElement = (JSONObject) currentElement.get(attributePathElement);
		}
		return currentElement;
	}

	
}
