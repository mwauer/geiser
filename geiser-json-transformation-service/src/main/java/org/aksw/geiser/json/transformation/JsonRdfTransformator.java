package org.aksw.geiser.json.transformation;

import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.jq.ImmutableJqLibrary;
import com.arakelian.jq.ImmutableJqRequest;
import com.arakelian.jq.JqRequest;
import com.arakelian.jq.JqResponse;

/**
 * Transforms a JSON document into RDF.
 * 
 * @author wauer
 *
 */
public class JsonRdfTransformator {

	private static final Logger log = LoggerFactory.getLogger(JsonRdfTransformator.class);

	static ImmutableJqLibrary lib = ImmutableJqLibrary.of();

	public Model transform(String json, String jqUriMapping, Map<String, String> jqRdfMapping, Model baseModel) {
		ModelBuilder modelBuilder = new ModelBuilder(baseModel);

		// create URI

		JqRequest request = ImmutableJqRequest.builder() //
				.lib(lib) //
				.input(json) //
				.filter(jqUriMapping) //
				.build();

		JqResponse response = request.execute();
		if (response.hasErrors()) {
			throw new IllegalArgumentException("Failed to generate IRI for jq filter " + jqUriMapping
					+ " on JSON input: " + json + ". Errors from jq:" + errorListAsString(response));
		}

		SimpleValueFactory vf = SimpleValueFactory.getInstance();
		IRI iri = vf.createIRI(removeOuterQuotes(response.getOutput()));
		log.debug("transforming into resource {}", iri);
		modelBuilder.subject(iri);

		for (String jqFilter : jqRdfMapping.keySet()) {
			request = ImmutableJqRequest.builder() //
					.lib(lib) //
					.input(json) //
					.filter(jqFilter) //
					.build();
			
			response = request.execute();
			
			if (response.hasErrors()) {
				throw new IllegalArgumentException("Failed to transform property for jq filter " + jqFilter
						+ " on JSON input: " + json + ". Errors from jq:" + errorListAsString(response));
			}
			
			IRI predicate = vf.createIRI(jqRdfMapping.get(jqFilter));
			String objectString = removeOuterQuotes(response.getOutput());
			
			if (objectString.isEmpty()) {
				log.warn("Empty result for property {}, maybe bad jq filter {}", predicate, jqFilter);
			}
			
			// very bad IRI detection
			if (objectString.startsWith("http://") || objectString.startsWith("https://")) {
				modelBuilder.add(predicate, vf.createIRI(objectString));
			}
			else {
				modelBuilder.add(predicate, vf.createLiteral(objectString));
			}
		}

		return modelBuilder.build();
	}
	
	private String errorListAsString(JqResponse response) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String error : response.getErrors()) {
			stringBuilder.append(error);
			stringBuilder.append('\n');
		}
		return stringBuilder.toString();
	}
	
	private String removeOuterQuotes(String input) {
		return input.replaceAll("^\"|\"$", "");
	}

}
