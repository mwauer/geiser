package org.aksw.geiser.json.transformation;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.arakelian.jq.ImmutableJqLibrary;
import com.arakelian.jq.ImmutableJqRequest;
import com.arakelian.jq.JqRequest;
import com.arakelian.jq.JqResponse;
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
	
	@Test
	public void readJqFromFileAndTransformToJsonLD() throws IOException{
		 ImmutableJqLibrary lib = ImmutableJqLibrary.of();
		
		String jqString = IOUtils.toString(this.getClass().getResourceAsStream("/jq-jsonld.txt")); 
		String jsonString = IOUtils.toString(this.getClass().getResourceAsStream("/test_tweets.json")); 
		JqRequest request = ImmutableJqRequest.builder() //
				.lib(lib) //
				.input(jsonString) //
				.filter(jqString) //
				.build();

		JqResponse response = request.execute();
		
		Assert.assertFalse(response.hasErrors());
		Model m = Rio.parse(IOUtils.toInputStream(response.getOutput()),"",RDFFormat.JSONLD);
		Assert.assertEquals(12, m.size());
		
		Assert.assertEquals("http://www.projekt-geiser.de/tweets/fest/10216944443837284000000000",Models.subject(m).get().stringValue());
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
