package org.aksw.geiser.json.transformation;

import java.util.HashMap;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

public class JsonRdfTransformatorTest {

	JsonRdfTransformator jrt;

	@Before
	public void setUp() throws Exception {
		jrt = new JsonRdfTransformator();
	}

	@Test
	public void testTransform() {
		Model baseModel = new JsonTransformationConfig().baseModel();
		HashMap<String, String> mapping = Maps.newHashMap();
		mapping.put(".name", RDFS.NAMESPACE.concat("label"));
		mapping.put("\"http://example.org/property/\"+.ref", OWL.NAMESPACE.concat("sameAs"));
		Model transformed = jrt.transform("{\"id\":\"123\",\"name\":\"Tester\",\"ref\":\"234\"}", "\"http://example.org/\"+.id", mapping,
				baseModel);
		System.out.println(transformed);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testTransformError() {
		Model baseModel = new JsonTransformationConfig().baseModel();
		HashMap<String, String> mapping = Maps.newHashMap();
		mapping.put(".name", RDFS.NAMESPACE.concat("label"));
		// this won't generate a warning even though refxxx does not match anything
		mapping.put("\"http://example.org/property/\"+.refxxx", OWL.NAMESPACE.concat("sameAs"));
		// but this will, invalid jq filter
		mapping.put("...", OWL.NAMESPACE.concat("failure"));
			Model transformed = jrt.transform("{\"id\":\"12345\",\"name\":\"Tester\",\"ref\":\"234\"}", "\"http://example.org/\"+.id", mapping,
				baseModel);
		System.out.println(transformed);
	}

}
