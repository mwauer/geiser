package org.aksw.geiser.json.transformation;

import org.aksw.geiser.configuration.GeiserServiceConfiguration;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonTransformationConfig extends GeiserServiceConfiguration {

	/**
	 * @return empty {@link Model} with necessary namespaces (for GEISER demo use case)
	 */
	@Bean
	public Model baseModel() {
		ModelBuilder modelBuilder = new ModelBuilder();
		
		modelBuilder.setNamespace(FOAF.NS);
		modelBuilder.setNamespace(DCTERMS.NS);
		modelBuilder.setNamespace("geoliteral", "http://www.bigdata.com/rdf/geospatial/literals/v1#");
		modelBuilder.setNamespace(RDF.NS);
		modelBuilder.setNamespace(RDFS.NS);
		modelBuilder.setNamespace("sioc", "http://rdfs.org/sioc/ns#");
		modelBuilder.setNamespace("wgs84", "http://www.w3.org/2003/01/geo/wgs84_pos#");
		modelBuilder.setNamespace("xml", "http://www.w3.org/XML/1998/namespace");
		modelBuilder.setNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");		
		
		return modelBuilder.build();
	}
	
	@Bean
	public JsonRdfTransformator transformator() {
		return new JsonRdfTransformator();
	}
	
}
