package org.aksw.geiser.sparqlify;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class R2RMLConverterTest {
	
	private static final Logger log = LoggerFactory.getLogger(R2RMLConverterTest.class);

	@Test
	public void testConverFtToSml() throws IOException {
		String in = FileUtils.readFileToString(new File("src/test/resources/r2rml-example.ttl"));
		log.info("{}", in);
		String sml = R2RMLConverter.convertToSml(in);
		assertNotNull(sml);
		log.info("{}", sml);
	}

}
