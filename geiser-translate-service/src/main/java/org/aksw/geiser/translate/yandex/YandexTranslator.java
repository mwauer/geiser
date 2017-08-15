package org.aksw.geiser.translate.yandex;

import java.util.Optional;

import org.aksw.geiser.translate.Translator;
import org.springframework.beans.factory.annotation.Value;

import com.github.vbauer.yta.model.Language;
import com.github.vbauer.yta.model.Translation;
import com.github.vbauer.yta.service.YTranslateApiImpl;

public class YandexTranslator implements Translator {

	@Value("${translate_api_key}")
	private String apiKey;
	
	@Override
	public TranslationResult translate(String input, String targetLanguage) {
		// TODO extract to field or configuration
		YTranslateApiImpl translateApiImpl = new YTranslateApiImpl(apiKey);
		Translation translation = translateApiImpl.translationApi().translate(input, Language.EN);
		Optional<Language> sourceLanguage = translation.direction().source();
		return new TranslationResult(translation.text(),
				Optional.of(sourceLanguage.isPresent() ? sourceLanguage.get().code() : null));
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

}
