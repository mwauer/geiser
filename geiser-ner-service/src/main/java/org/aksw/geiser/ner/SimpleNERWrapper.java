package org.aksw.geiser.ner;

import java.io.IOException;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;

public interface SimpleNERWrapper {
	
	/**
	 * Applies NER to the input string, returning entity IRIs.
	 * @param input the input text
	 * @return a {@link List} of {@link IRI}s.
	 */
	public List<IRI> getEntities(String input) throws IOException;

}
