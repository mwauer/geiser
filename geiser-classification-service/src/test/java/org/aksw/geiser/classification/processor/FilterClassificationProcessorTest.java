package org.aksw.geiser.classification.processor;

import static org.junit.Assert.*;

import org.aksw.geiser.classification.ClassificationProcessingException;
import org.aksw.geiser.classification.GeiserClassificationConfig;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
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
		config.configuration = "null <"+RDF.TYPE.stringValue()+"> null";
		
		ValueFactory vf = SimpleValueFactory.getInstance();
		
		model = new LinkedHashModel();
		model.add(vf.createIRI("urn:r1"), RDF.TYPE, vf.createIRI("urn:c1"));
		model.add(vf.createIRI("urn:r1"), RDFS.LABEL, vf.createLiteral("Resource1", "de"));
		model.add(vf.createIRI("urn:r2"), RDFS.COMMENT, vf.createLiteral("no-type resource"));
		model.add(vf.createIRI("urn:r2"), RDFS.LABEL, vf.createLiteral("Resource2", XMLSchema.STRING));
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

	
	@Test(expected=ClassificationProcessingException.class)
	public void testProcessBadConfigTooShort() throws ClassificationProcessingException {
		config.configuration = "null null";
		new FilterClassificationProcessor().process(model, config);
	}

	@Test(expected=ClassificationProcessingException.class)
	public void testProcessBadConfigIRI() throws ClassificationProcessingException {
		config.configuration = "urn:r1 null null";
		new FilterClassificationProcessor().process(model, config);
	}
	
	@Test(expected=ClassificationProcessingException.class)
	public void testProcessBadLiteral() throws ClassificationProcessingException {
		config.configuration = "null null \"123\"^^xx";
		new FilterClassificationProcessor().process(model, config);
	}

}
