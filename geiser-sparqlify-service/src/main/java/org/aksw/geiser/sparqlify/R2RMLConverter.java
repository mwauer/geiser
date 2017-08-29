package org.aksw.geiser.sparqlify;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.aksw.sml.converters.r2rml2sml.R2RML2SMLConverter;
import org.aksw.sparqlify.core.domain.input.ViewDefinition;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class R2RMLConverter {

	public static String convertToSml(String r2rmlConfig) {
		StringWriter w = new StringWriter();
		Model r2rmlMappings = ModelFactory.createDefaultModel();
		r2rmlMappings.read(new StringReader(r2rmlConfig), null, "TURTLE");
		List<ViewDefinition> viewDefs = R2RML2SMLConverter.convert(r2rmlMappings);
		for (ViewDefinition viewDef : viewDefs) {
			w.write(viewDef.toString());
			w.write("\n");
			w.flush();
		}
		return w.toString();
	}

}
