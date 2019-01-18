package org.aksw.geiser.classification;

import org.eclipse.rdf4j.model.Model;

public interface ClassificationProcessor {

	/**
	 * Processes a classification on the given model.
	 * 
	 * @param model a given RDF model
	 * @param config a classification configuration
	 * @return the resulting model, can be the same instance as the given model attribute
	 * @throws ClassificationProcessingException
	 */
	public Model process(Model model, GeiserClassificationConfig config) throws ClassificationProcessingException;
	
}
