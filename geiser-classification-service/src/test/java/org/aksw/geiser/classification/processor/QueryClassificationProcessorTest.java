package org.aksw.geiser.classification.processor;

import static org.junit.Assert.assertEquals;

import org.aksw.geiser.classification.ClassificationProcessingException;
import org.aksw.geiser.classification.GeiserClassificationConfig;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.junit.Before;
import org.junit.Test;

public class QueryClassificationProcessorTest {

	private GeiserClassificationConfig config;

	private Model model;

	@Before
	public void setUp() throws Exception {
		config = new GeiserClassificationConfig();
		config.cbd = true;
		config.mode = "query";
		// config.configuration = "CONSTRUCT { ?s <"+RDF.TYPE.stringValue()+">
		// ?o } WHERE { ?s <"+RDF.TYPE.stringValue()+"> ?o }";
		config.configuration = "CONSTRUCT { ?s ?p ?o } WHERE { ?s <" + RDF.TYPE.stringValue() + "> ?type; ?p ?o }";

		model = ProcessorTestFixtures.createTestModel();
	}

	@Test
	public void testProcessQuery() throws ClassificationProcessingException {
		Model filtered = new QueryClassificationProcessor().process(model, config);
		assertEquals(2, filtered.size());
	}

	@Test(expected = ClassificationProcessingException.class)
	public void testProcessMalformedQuery() throws ClassificationProcessingException {
		config.configuration = "jumble wanglebork";
		new QueryClassificationProcessor().process(model, config);
	}

}
