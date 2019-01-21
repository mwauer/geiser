package org.aksw.geiser.classification.processor;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

public class ProcessorTestFixtures {
	
	public static Model createTestModel() {
		ValueFactory vf = SimpleValueFactory.getInstance();
		
		Model model = new LinkedHashModel();
		model.add(vf.createIRI("urn:r1"), RDF.TYPE, vf.createIRI("urn:c1"));
		model.add(vf.createIRI("urn:r1"), RDFS.LABEL, vf.createLiteral("Resource1", "de"));
		model.add(vf.createIRI("urn:r2"), RDFS.COMMENT, vf.createLiteral("no-type resource"));
		model.add(vf.createIRI("urn:r2"), RDFS.LABEL, vf.createLiteral("Resource2", XMLSchema.STRING));
		return model;
	}

}
