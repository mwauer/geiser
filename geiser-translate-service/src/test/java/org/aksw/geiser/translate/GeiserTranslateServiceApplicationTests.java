package org.aksw.geiser.translate;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.aksw.geiser.util.Message2Rdf4jModel;
import org.eclipse.rdf4j.model.Model;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GeiserTranslateServiceApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void testParseTurtle() throws IOException {
		Message message = MessageBuilder.withBody("<urn:r1> <urn:p1> <urn:o1>.".getBytes(StandardCharsets.UTF_8)).setContentType("text/turtle").build();
		Model model = Message2Rdf4jModel.convertToModel(message);
		assertEquals(1, model.size());
	}

}
