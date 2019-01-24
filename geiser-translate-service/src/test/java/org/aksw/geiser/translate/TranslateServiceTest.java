package org.aksw.geiser.translate;

import static org.junit.Assert.assertTrue;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.minidev.json.parser.ParseException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TranslateServiceTest {

	private Model testModel;
	
	private ValueFactory vf;

	@Autowired
	private TranslateService translateService;
	

	@Before
	public void setUp() throws Exception {
		testModel = TranslateServiceTestFixtures.createTestModel();
		vf = SimpleValueFactory.getInstance();
	}

	@Test
	public void testTranslateMessage() throws ParseException {
		Model translatedModel = translateService.translate(testModel);
		// create expected result model
		Model expectedModel = TranslateServiceTestFixtures.createTestModel();
		expectedModel.add(vf.createIRI("urn:r1"), RDFS.LABEL, vf.createLiteral("Resource1 translated", "en"));
		assertTrue(Models.isomorphic(translatedModel, expectedModel));
	}
	
}
