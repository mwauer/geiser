package org.aksw.geiser.translate;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.aksw.geiser.translate.Translator.TranslationResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class TranslateService {

	private static final Logger log = LoggerFactory.getLogger(TranslateService.class);
	
	@Autowired
	private String[] attributePath; // = new String[] {"data", "text"};
	
	@Autowired
	private Translator translator;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Bean
	public Queue translateQueue() {
		return new Queue("translate", false, false, true);
	}

	@RabbitListener(queues = "translate")
	// TODO needs to be processed based on message header of input message
	// TODO error handling
//	@SendTo("${translate_target_routingkey:fox_v1}")
	public void handleTranslateMessage(
			@Payload Message payload
			/* , @Headers Map<String, Object> headers */) throws JSONException {
		
		log.debug("Got message {}", payload);
		
		JSONObject jsonObject = new JSONObject(new String(payload.getBody(), StandardCharsets.UTF_8));
		log.debug(" parsed json object {}", jsonObject);
		
		JSONObject resultJson = translateMessage(jsonObject);
		String resultJsonString = resultJson.toString();
		log.debug(" updated in json: {}", resultJsonString);
		
		Message message = MessageBuilder.withBody(resultJsonString.getBytes()).setContentType("application/json;charset=utf-8").build();
		String receivedRoutingKey = payload.getMessageProperties().getReceivedRoutingKey();
		String nextRoutingKey = receivedRoutingKey.substring(receivedRoutingKey.indexOf(".")+1);
		log.debug("Sending to {}: {}", nextRoutingKey, message);
		rabbitTemplate.send("geiser", nextRoutingKey, message);
		
	}
	
	protected JSONObject translateMessage(JSONObject jsonObject) {
		JSONObject parentElement = getParentElement(jsonObject);
		String attribute = attributePath[attributePath.length-1];
		Object attributeValue = parentElement.get(attribute);
		log.debug(" fetched attribute value: {}", attributeValue);
		
		TranslationResult translated = translate(attributeValue.toString());
		log.debug(" translated into: {}", translated);
		
		parentElement.put(attribute, translated.getOutput());
		JSONObject translation = new JSONObject();
		translation.put("original", attributeValue.toString());
		if (translated.getDetectedLanguage().isPresent()) {
			translation.put("detectedSourceLanguage", translated.getDetectedLanguage().get());
		}
		parentElement.put("translation", translation);
		
		return jsonObject;
	}
	
	private TranslationResult translate(String input) {
		return new TranslationResult(input + " translated", Optional.of("de"));//translator.translate(input, "EN");
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
