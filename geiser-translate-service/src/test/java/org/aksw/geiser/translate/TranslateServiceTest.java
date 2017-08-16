package org.aksw.geiser.translate;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.minidev.json.parser.ParseException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TranslateServiceTest {

	private JSONObject testMessage;

	// @Bean
	// private String[] attributePath() {
	// return new String[] { "data", "text" };
	// }

	// @Autowired
	// private GeiserTranslateServiceConfiguration
	// geiserTranslateServiceConfiguration;

	@Autowired
	private TranslateService translateService;

	@Before
	public void setUp() throws Exception {
		testMessage = new JSONObject(
				"{\"route\": \"fox-v1\",\"data\": {\"text\": \"Der Philosoph und Mathematiker Leibniz.\"}}");
	}

	@Test
	@Ignore("needs API key, TODO mock translator")
	public void testTranslateMessage() throws JSONException, ParseException {
		JSONObject translatedMessage = translateService.translateMessage(testMessage);
		assertEquals(
				"{\"route\":\"fox-v1\",\"data\":{\"text\":\"The philosopher and mathematician Leibniz.\",\"translation\":{\"original\":\"Der Philosoph und Mathematiker Leibniz.\",\"detectedSourceLanguage\":\"DE\"}}}",
				translatedMessage.toString());
	}

}
