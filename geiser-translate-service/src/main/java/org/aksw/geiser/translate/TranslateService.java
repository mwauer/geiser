package org.aksw.geiser.translate;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.aksw.geiser.translate.Translator.TranslationResult;
import org.aksw.geiser.util.ServiceUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class TranslateService {

	public static final String ROUTING_KEY = "translate-v1.#";
	public static final String QUEUE_NAME = "translate-v1";

	private static final Logger log = LoggerFactory.getLogger(TranslateService.class);

	@Autowired
	private String[] attributePath; // = new String[] {"data", "text"};

	@Autowired
	private String[] resultPath; // default as above

	@Autowired
	private Translator translator;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Value("${simulate:false}")
	private boolean simulate;

	@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = QUEUE_NAME)))
	public void handleTranslateMessage(@Payload Message payload
	/* , @Headers Map<String, Object> headers */) throws JSONException {

		log.info("Got message {}", payload);

		// quick fix: if array, create JSONArray and pick first element
		String message = new String(payload.getBody(), StandardCharsets.UTF_8);
		JSONObject jsonObject;
		if (message.startsWith("[")) {
			log.debug("Trying to pick first object from incoming JSON array");
			JSONArray array = new JSONArray(message);
			jsonObject = array.getJSONObject(0);
		} else {
			jsonObject = new JSONObject(message);
		}

		log.debug(" parsed json object {}", jsonObject);

		JSONObject resultJson = translateMessage(jsonObject);
		String resultJsonString = resultJson.toString();
		log.debug(" updated in json: {}", resultJsonString);

		Message resultMessage = MessageBuilder.withBody(resultJsonString.getBytes())
				.setContentType("application/json;charset=utf-8").build();

		String nextRoutingKey = ServiceUtils.nextRoutingKey(payload);
		log.debug("Sending to {}: {}", nextRoutingKey, resultMessage);
		rabbitTemplate.send("geiser", nextRoutingKey, resultMessage);

	}

	protected JSONObject translateMessage(JSONObject jsonObject) {
		JSONObject parentElement = getParentElement(jsonObject);
		String attribute = attributePath[attributePath.length - 1];
		Object attributeValue = parentElement.get(attribute);
		log.debug(" fetched attribute value: {}", attributeValue);

		TranslationResult translated = translate(attributeValue.toString());
		log.debug(" translated into: {}", translated);

		JSONObject resultParentElement = createResultPath(jsonObject);
		String resultAttribute = resultPath[resultPath.length - 1];
		resultParentElement.put(resultAttribute, translated.getOutput());
		JSONObject translation = new JSONObject();
		translation.put("original", attributeValue.toString());
		if (translated.getDetectedLanguage().isPresent()) {
			translation.put("detectedSourceLanguage", translated.getDetectedLanguage().get());
		}
		resultParentElement.put("translation", translation);

		return jsonObject;
	}

	/**
	 * Creates empty JSON objects for the given {@link #resultPath} if
	 * necessary.
	 * 
	 * @param jsonObject
	 *            the incoming JSON root object
	 * @return the second-to-last element of the resultPath, e.g., the "data"
	 *         object for a "data\translated" resultpath
	 */
	private JSONObject createResultPath(JSONObject jsonObject) {
		int resultPathIndex = 0;
		JSONObject resultElement = jsonObject;
		while (resultPathIndex < resultPath.length - 1) {
			String nextResultPathElement = resultPath[resultPathIndex];
			if (!resultElement.has(nextResultPathElement)) {
				resultElement.put(nextResultPathElement, new JSONObject());
			}
			resultElement = resultElement.getJSONObject(nextResultPathElement);
			resultPathIndex++;
		}
		return resultElement;
	}

	private TranslationResult translate(String input) {
		return simulate ? new TranslationResult(input + " translated", Optional.of("de"))
				: translator.translate(input, "EN");
	}

	private JSONObject getParentElement(JSONObject payload) throws JSONException {
		JSONObject currentElement = payload;
		for (int i = 0; i < attributePath.length - 1; i++) {
			String attributePathElement = attributePath[i];
			if ((i < attributePath.length) && attributePath[i + 1].equals("[]")) {
				// next element is array, pick its first field
				i++;
				currentElement = currentElement.getJSONArray(attributePathElement).getJSONObject(0);
			} else {
				currentElement = currentElement.getJSONObject(attributePathElement);
			}
		}
		return currentElement;
	}

}
