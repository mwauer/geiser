package org.aksw.geiser.translate.yandex;

import static org.junit.Assert.assertEquals;

import org.aksw.geiser.translate.Translator.TranslationResult;
import org.junit.Ignore;
import org.junit.Test;

public class YandexTranslatorTest {

	@Test
	@Ignore
	public void test() {
		YandexTranslator yandexTranslator = new YandexTranslator();
		yandexTranslator.setApiKey("DEFINE YOURSELF");
		TranslationResult translated = yandexTranslator.translate("Heute ist ein schöner Tag, ich gehe in den Park.", "EN");
		assertEquals("Today is a beautiful day, I go to the Park.", translated.getOutput());
		assertEquals("DE", translated.getDetectedLanguage().get());
		// my first test sentence, Today really is a beautiful day, dude
		// actually returned: Today is a beautiful day, the age is real.
	}

}
