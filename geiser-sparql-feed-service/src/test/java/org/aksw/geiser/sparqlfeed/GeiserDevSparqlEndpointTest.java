package org.aksw.geiser.sparqlfeed;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.junit.Before;
import org.junit.Test;

public class GeiserDevSparqlEndpointTest {

	private static final String USERNAME_AND_PASSWORD = "guest";

	private static final String ENDPOINT = "https://geiser-dev.metaphacts.com/sparql";

	private SPARQLRepository sparqlRepository;

	@Before
	public void setup() {
		sparqlRepository = new SPARQLRepository(ENDPOINT);
		sparqlRepository.setUsernameAndPassword(USERNAME_AND_PASSWORD, USERNAME_AND_PASSWORD);
		sparqlRepository.initialize();
	}
	
	/**
	 * FIXME this should not throw an exception
	 */
	@Test(expected=RepositoryException.class)
	public void testGraphQueryWithRepositories() {
		// FIXME the following line does not appear to work properly
		Model m = Repositories.graphQuery(sparqlRepository, "CONSTRUCT WHERE { ?s ?p ?o } LIMIT 1",
				r -> QueryResults.asModel(r));
		assertNotNull(m);
		assertTrue(m.size() > 0);
	}

	/**
	 * Does not throw an exception
	 */
	@Test
	public void testGraphQueryVerbose() {
		try (RepositoryConnection conn = sparqlRepository.getConnection()) {
			GraphQuery graphQuery = conn.prepareGraphQuery("CONSTRUCT WHERE { ?s ?p ?o } LIMIT 1");
			GraphQueryResult queryResult = graphQuery.evaluate();
			assertTrue(queryResult.hasNext());
			assertNotNull(queryResult.next());
		}
	}

}
