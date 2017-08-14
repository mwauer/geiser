package org.aksw.geiser.translate;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.minidev.json.parser.ParseException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TranslateServiceTest {

	private Message testPayload;

//	@Bean
//	private String[] attributePath() {
//		return new String[] { "data", "text" };
//	}
	
//	@Autowired
//	private GeiserTranslateServiceConfiguration geiserTranslateServiceConfiguration;
	
	@Autowired
	private TranslateService translateService;

	@Before
	public void setUp() throws Exception {
		testPayload = MessageBuilder.withBody(
				"{\"route\": \"fox-v1\",\"data\": {\"text\": \"Der Philosoph und Mathematiker Leibniz.\"}}".getBytes())
				.setContentType("application/json").build();
	}

	@Test
	@Ignore("needs API key, TODO mock translator")
	public void testHandleTranslateMessage() throws JSONException, ParseException {
		Message translatedMessage = translateService.handleTranslateMessage(testPayload);
		assertEquals(
				"{\"route\":\"fox-v1\",\"data\":{\"text\":\"The philosopher and mathematician Leibniz.\",\"translation\":{\"original\":\"Der Philosoph und Mathematiker Leibniz.\",\"detectedSourceLanguage\":\"DE\"}}}",
				new String(translatedMessage.getBody()));
	}

}
