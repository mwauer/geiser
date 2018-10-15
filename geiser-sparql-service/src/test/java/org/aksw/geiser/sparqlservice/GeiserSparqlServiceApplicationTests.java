package org.aksw.geiser.sparqlservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.resultio.QueryResultIO;
import org.eclipse.rdf4j.query.resultio.QueryResultParseException;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.UnsupportedQueryResultFormatException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

//@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { GeiserSparqlServiceApplication.class, GeiserSparqlServiceApplication.GeiserSparqlSelectService.class, GeiserSparqlServiceApplicationTests.TestConfiguration.class})
public class GeiserSparqlServiceApplicationTests {
	
	@Autowired
	private Repository repository;
	
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    private static final ValueFactory vf = SimpleValueFactory.getInstance();
    
    private static final IRI testPersonIRI = vf.createIRI("http://example.com/person1");
    
	@Before
	public void before() throws Exception {
//		try(RepositoryConnection con = repository.getConnection()){
//			con.add(vf.createStatement(testPersonIRI, RDF.TYPE, FOAF.PERSON));
//			assertEquals(1, con.size());
//		}
		
	}
	
	@After
	public void after() throws Exception {
//		try(RepositoryConnection con = repository.getConnection()){
//			con.prepareUpdate(QueryLanguage.SPARQL, "DROP ALL").execute();
//		}
	}


	

	@Test
	public void testHandleServiceDiscoveryRequest() throws TupleQueryResultHandlerException, QueryEvaluationException, QueryResultParseException, UnsupportedQueryResultFormatException, IOException {
		
		String query = "SELECT * WHERE {?s a ?o}";
		Message message = MessageBuilder.withBody(query.getBytes())
				.build();
		rabbitTemplate.send("sparql-select-v1.sparqlresult", message);
        String stringResult = (String) rabbitTemplate.receiveAndConvert("test");

        try(TupleQueryResult tr = QueryResultIO.parseTuple(IOUtils.toInputStream(stringResult, "UTF-8"), TupleQueryResultFormat.JSON)){
            assertTrue(tr.hasNext());
            while(tr.hasNext()){
            	assertEquals((IRI)tr.next().getBinding("o"),testPersonIRI);
            }
        }

        
	}
	
	@Configuration
	public static class TestConfiguration {

//		@Bean
//		public Repository repository() {
//			Repository r =  new SailRepository(new MemoryStore());
//			r.initialize();
//			return r;
//		}
		
		@Bean
	    public ConnectionFactory connectionFactory() {
	        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
	        connectionFactory.setUsername("guest");
	        connectionFactory.setPassword("guest");
	        return connectionFactory;
	    }
		
		@Bean
	    public RabbitTemplate rabbitTemplate() {
	        return new RabbitTemplate(connectionFactory());
	    }


	    @Bean
	    public RabbitAdmin rabbitAdmin() {
	        return new RabbitAdmin(connectionFactory());
	    };
	}
	
}
