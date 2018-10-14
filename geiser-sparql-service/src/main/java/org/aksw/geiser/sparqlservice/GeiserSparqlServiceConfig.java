package org.aksw.geiser.sparqlservice;

import org.aksw.geiser.configuration.GeiserServiceConfiguration;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryFactory;
import org.eclipse.rdf4j.repository.config.RepositoryRegistry;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.repository.sparql.config.SPARQLRepositoryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeiserSparqlServiceConfig extends GeiserServiceConfiguration {

	@Value("${sparql-repository-query-url:http://metaphactory:8080/sparql}")
	private String sparqlRepositoryQueryUrl;

	@Value("${sparql-repository-update-url:http://metaphactory:8080/sparql}")
	private String sparqlRepositoryUpdateUrl;
	
	@Value("${sparql-repository-basicauth-user:admin}")
	private String sparqlRepositoryBasicAuthUser;

	@Value("${sparql-repository-basicauth-pw:admin}")
	private String sparqlRepositoryBasicAuthPw;
	
	@Bean
	public Repository repository() {
		RepositoryConfig repConfig = new RepositoryConfig("metaphactory","metaphactory HTTP SPARQL Repository");
		// important to use this constructor, since there is a bug in the other constructors missing to init the type
		SPARQLRepositoryConfig repImplConfig = new SPARQLRepositoryConfig();
		repImplConfig.setQueryEndpointUrl(sparqlRepositoryQueryUrl);
		repImplConfig.setUpdateEndpointUrl(sparqlRepositoryUpdateUrl);
		repConfig.setRepositoryImplConfig(repImplConfig);
		RepositoryFactory factory = RepositoryRegistry
				.getInstance()
				.get(repImplConfig.getType())
				.orElseThrow(
						() -> new RuntimeException("Unsupported repository type: "+ repImplConfig.getType()));
		SPARQLRepository repository = (SPARQLRepository) factory.getRepository(repImplConfig);
		repository.enableQuadMode(true);
		repository.setUsernameAndPassword(sparqlRepositoryBasicAuthUser, sparqlRepositoryBasicAuthPw);
		repository.initialize();
		return repository;
	}
}
