package org.aksw.geiser.classification;

import org.aksw.geiser.configuration.GeiserServiceConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeiserClassificationConfig extends GeiserServiceConfiguration {
	
	/**
	 * Selects which classification mode is used. Available modes:
	 * <ul>
	 * <li><b>filter</b> uses the RDF4J filter method on a model to retain only statements of selected subject, predicate or object values (all of which can be <code>null</code> to accept any value).</li>
	 * <li><b>query</b> uses a SPARQL query to filter the received model.</li>
	 * <li><b>owl</b> uses OWL axioms to filter the received model, using owl2sparql library.</li>
	 * </ul>
	 */
	@Value("${mode:filter}")
	public String mode;
	
	/**
	 * <p>Contains the configuration to be used for the selected classification mode.</p>
	 * <p>For the filter mode, the config format is a space-separated string of subject, predicate and object values, optionally followed by possible graphs. E.g., "null null "123"^^<http://www.w3.org/2001/XMLSchema#integer>" to select all resources which contain an integer value 123. Filter config currently does not support literals containing spaces.</p>
	 * <p>For query and owl mode, the config format is a SPARQL query or an OWL axiom in Manchester syntax, respectively.</p> 
	 */
	@Value("${configuration}")
	public String configuration;
	
	/**
	 * <p>If <code>true</code>, the classification does not only choose the filtered statements, but the entire concise bounded descriptions of all the filtered subjects. Currently may not support blank nodes.</p>
	 * <p>For the example filter mode config above, this would not only return the ?s ?p "123"^^xsd:integer triple, but all triples pertaining to the respective ?s subject.</p>
	 */
	@Value("${cbd:true}")
	public boolean cbd;

}
