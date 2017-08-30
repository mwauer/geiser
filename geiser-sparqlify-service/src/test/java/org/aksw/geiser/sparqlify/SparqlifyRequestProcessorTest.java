package org.aksw.geiser.sparqlify;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.antlr.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SparqlifyRequestProcessorTest {

	private static final Logger log = LoggerFactory.getLogger(SparqlifyRequestProcessorTest.class);

	@Value("classpath:/input.json")
	private Resource input;

	@Value("classpath:/input-fcd.json")
	private Resource inputFcd;

	@Test
	public void testProcessSml() throws JsonProcessingException, IOException, RecognitionException {
		SparqlifyServiceRequest request = readRequest(input);
		log.info("{}", request);

		String result = new SparqlifyRequestProcessor().process(request);

		log.info("result: {}", result);
		assertNotNull(result);
	}
	
	@Test
	public void testProcessR2rml() throws JsonParseException, JsonMappingException, IOException, RecognitionException {
		SparqlifyServiceRequest request = readRequest(inputFcd);
		log.info("{}", request);

		String result = new SparqlifyRequestProcessor().process(request);

		log.info("result: {}", result);
		assertNotNull(result);
		
	}
	
	private SparqlifyServiceRequest readRequest(Resource input) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper jsonObjectMapper = new ObjectMapper();
		jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		SparqlifyServiceRequest request = jsonObjectMapper.readValue(input.getInputStream(),
				SparqlifyServiceRequest.class);
		return request;
	}

}
