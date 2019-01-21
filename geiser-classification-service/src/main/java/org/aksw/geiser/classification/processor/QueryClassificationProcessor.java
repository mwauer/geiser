package org.aksw.geiser.classification.processor;

import org.aksw.geiser.classification.ClassificationProcessingException;
import org.aksw.geiser.classification.ClassificationProcessor;
import org.aksw.geiser.classification.GeiserClassificationConfig;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * Basic implementation of a SPARQL classification processor. Supports graph queries (CONSTRUCT). The CBD configuration option is not used here.
 * 
 * @author wauer
 *
 */
public class QueryClassificationProcessor implements ClassificationProcessor {

	@Override
	public Model process(Model model, GeiserClassificationConfig config) throws ClassificationProcessingException {
		try {
			Repository repo = new SailRepository(new MemoryStore());
			repo.initialize();
			RepositoryConnection connection = repo.getConnection();
			try {
				connection.add(model);
			} finally {
				connection.close();
			}
			Model result = Repositories.graphQuery(repo, config.configuration, r -> QueryResults.asModel(r));
			return result;
		} catch (Exception e) {
			throw new ClassificationProcessingException(
					"Failed to process query classification with configuration: " + config.configuration, e);
		}
	}

}
