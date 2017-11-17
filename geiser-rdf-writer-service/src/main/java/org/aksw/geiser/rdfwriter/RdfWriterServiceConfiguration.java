package org.aksw.geiser.rdfwriter;

import java.io.File;

import org.aksw.geiser.configuration.GeiserServiceConfiguration;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RdfWriterServiceConfiguration extends GeiserServiceConfiguration {
	
	@Value("${rdf_writer_store_path:/tmp}")
	private String path;
	
	@Bean
	public Repository repository() {
		File dataDir = new File(path);
		MemoryStore memStore = new MemoryStore(dataDir);
		memStore.setSyncDelay(1000L);
		Repository repo = new SailRepository(memStore);
		repo.initialize();
		return repo;
	}

}
