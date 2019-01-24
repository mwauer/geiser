package org.aksw.geiser.translate;

import java.io.IOException;
import java.util.Optional;

import org.aksw.geiser.translate.Translator.TranslationResult;
import org.aksw.geiser.util.Message2Rdf4jModel;
import org.aksw.geiser.util.ServiceUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
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

	public static final String ROUTING_KEY = "translate-v2.#";
	public static final String QUEUE_NAME = "translate-v2";

	private static final Logger log = LoggerFactory.getLogger(TranslateService.class);

	@Autowired
	private Translator translator;

	@Autowired
	private ValueFactory vf;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	/** RDF type of the subject **/
	@Value("${subject_type:http://rdfs.org/sioc/ns#Post}")
	private String subjectType;

	/**
	 * Input property containing the text, can be comma separated. If multiple
	 * properties are found, their content will be joined by whitespace.
	 **/
	@Value("${text_properties:http://rdfs.org/sioc/ns#content,http://www.w3.org/2000/01/rdf-schema#label}")
	private String[] textProperties;

	/**
	 * Optional property for adding the translated text. If none is provided,
	 * the original property will be used.
	 **/
	@Value("${annotation_property}")
	private String annotationProperty;

	/**
	 * ISO language code (2 characters) for the target language, e.g., EN or DE.
	 * Will also be used for the language annotation of the new literal.
	 **/
	@Value("${target_language_code:EN}")
	private String targetLanguageCode;

	/**
	 * Only simulate translation. For testing purposes.
	 */
	@Value("${simulate:false}")
	private boolean simulate;

	@RabbitListener(bindings = @QueueBinding(key = ROUTING_KEY, exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "geiser", durable = "true", autoDelete = "true"), value = @org.springframework.amqp.rabbit.annotation.Queue(autoDelete = "true", value = QUEUE_NAME)))
	public void handleTranslateMessage(@Payload Message payload
	/* , @Headers Map<String, Object> headers */) throws IOException {

		log.info("Got message {}", payload);
		Model model = Message2Rdf4jModel.convertToModel(payload);

		Model result = translate(model);

		Message message = Message2Rdf4jModel.convertToMessage(result, RDFFormat.TURTLE);
		rabbitTemplate.send(payload.getMessageProperties().getReceivedExchange(), ServiceUtils.nextRoutingKey(payload),
				message);
	}

	public Model translate(Model model) {
		Model matchingResources = StringUtils.isEmpty(subjectType) ?
				model :
				model.filter(null, RDF.TYPE, vf.createIRI(subjectType));

		long addedStatements = 0;
		for (IRI subject : Models.subjectIRIs(matchingResources)) {
			for (String property : textProperties) {
				IRI prop = vf.createIRI(property);
				Model propertyStatements = model.filter(subject, prop, null);
				for (Literal l : Models.objectLiterals(propertyStatements)) {
					String value = l.stringValue();
					TranslationResult result = translateText(value);
					IRI targetProperty = StringUtils.isEmpty(annotationProperty) ? prop
							: vf.createIRI(annotationProperty);
					Literal translatedObject = vf.createLiteral(result.getOutput(), targetLanguageCode);
					log.info("Adding translation for {} {}: {}", subject, targetProperty, translatedObject);
					model.add(subject, targetProperty, translatedObject);
					addedStatements++;
				}
			}
		}
		log.info("Added {} translated statements for {} subjects", addedStatements, matchingResources.size());

		return model;
	}

	private TranslationResult translateText(String input) {
		return simulate ? new TranslationResult(input + " translated", Optional.of("de"))
				: translator.translate(input, targetLanguageCode);
	}

}
