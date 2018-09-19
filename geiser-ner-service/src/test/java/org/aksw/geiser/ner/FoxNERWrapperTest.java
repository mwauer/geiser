package org.aksw.geiser.ner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.aksw.geiser.ner.fox.FoxNERWrapper;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FoxNERWrapperTest {

	@Autowired
	private FoxNERWrapper wrapper;
	
	@Ignore
	@Test
	public void testInvokeFox() throws MalformedURLException {
//		new FoxNERWrapper().invokeFox();
	}
	
	@Ignore("required fox docker running locally")
	@Test
	public void testGetEntities() throws IOException {
		List<IRI> entities = wrapper.getEntities("Angela Merkel hat in Leipzig studiert.");
		assertEquals(2, entities.size());
		assertTrue(entities.contains(SimpleValueFactory.getInstance().createIRI("http://dbpedia.org/resource/Angela_Merkel")));
	}

}
