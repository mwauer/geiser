package org.aksw.geiser.translate;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationTest {

	private static final Logger log = LoggerFactory.getLogger(ConfigurationTest.class);

	@Test
	public void test() {
		String[] test = { "a", "b", "c" };
		log.info("Test {}", (Object) test);
	}

}
