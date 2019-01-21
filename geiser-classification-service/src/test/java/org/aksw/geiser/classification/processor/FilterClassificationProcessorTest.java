package org.aksw.geiser.classification.processor;

import static org.junit.Assert.assertEquals;

import org.aksw.geiser.classification.ClassificationProcessingException;
import org.aksw.geiser.classification.GeiserClassificationConfig;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.junit.Before;
import org.junit.Test;

public class FilterClassificationProcessorTest {

	private GeiserClassificationConfig config;

	private Model model;

	@Before
	public void setUp() throws Exception {
		config = new GeiserClassificationConfig();
		config.cbd = true;
		config.mode = "filter";
		config.configuration = "null <" + RDF.TYPE.stringValue() + "> null";

		model = ProcessorTestFixtures.createTestModel();
	}

	@Test
	public void testProcessCbd() throws ClassificationProcessingException {
		Model filtered = new FilterClassificationProcessor().process(model, config);
		assertEquals(2, filtered.size());
	}

	@Test
	public void testProcessNoCbd() throws ClassificationProcessingException {
		config.cbd = false;
		Model filtered = new FilterClassificationProcessor().process(model, config);
		assertEquals(1, filtered.size());
	}

	@Test
	public void testProcessLiteralFilter() throws ClassificationProcessingException {
		config.configuration = "null null \"Resource1\"@de";
		Model filtered = new FilterClassificationProcessor().process(model, config);
		assertEquals(2, filtered.size());
	}

	@Test
	public void testProcessLiteralFilterDatatype() throws ClassificationProcessingException {
		config.configuration = "null null \"Resource2\"^^xsd:string";
		Model filtered = new FilterClassificationProcessor().process(model, config);
		assertEquals(2, filtered.size());
	}

	@Test(expected = ClassificationProcessingException.class)
	public void testProcessBadConfigTooShort() throws ClassificationProcessingException {
		config.configuration = "null null";
		new FilterClassificationProcessor().process(model, config);
	}

	@Test(expected = ClassificationProcessingException.class)
	public void testProcessBadConfigIRI() throws ClassificationProcessingException {
		config.configuration = "urn:r1 null null";
		new FilterClassificationProcessor().process(model, config);
	}

	@Test(expected = ClassificationProcessingException.class)
	public void testProcessBadLiteral() throws ClassificationProcessingException {
		config.configuration = "null null \"123\"^^xx";
		new FilterClassificationProcessor().process(model, config);
	}

}
