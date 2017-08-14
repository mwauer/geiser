package org.aksw.geiser.translate;

import java.util.Optional;

public interface Translator {
	
	/**
	 * Translates the given input text into the target language.
	 * 
	 * @param input text in an arbitrary language
	 * @param targetLanguage the language of the expected result, in ISO format (e.g., EN for English, DE for German)
	 * @return a {@link TranslationResult}
	 */
	public TranslationResult translate(String input, String targetLanguage);
	
	public class TranslationResult {
		
		private final String output;
		
		private final Optional<String> detectedLanguage;

		public TranslationResult(String output, Optional<String> detectedLanguage) {
			super();
			this.output = output;
			this.detectedLanguage = detectedLanguage;
		}

		public String getOutput() {
			return output;
		}

		public Optional<String> getDetectedLanguage() {
			return detectedLanguage;
		}
		
	}

}
