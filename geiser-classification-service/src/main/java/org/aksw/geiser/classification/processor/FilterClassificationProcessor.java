package org.aksw.geiser.classification.processor;

import java.util.Set;

import org.aksw.geiser.classification.ClassificationProcessingException;
import org.aksw.geiser.classification.ClassificationProcessor;
import org.aksw.geiser.classification.GeiserClassificationConfig;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

/**
 * Basic implementation of a RDF4J filter processor.
 * 
 * @author wauer
 *
 */
public class FilterClassificationProcessor implements ClassificationProcessor {

	@Override
	public Model process(Model model, GeiserClassificationConfig config) throws ClassificationProcessingException {
		String[] configurationElements = StringUtils.split(config.configuration);
		if (configurationElements.length < 3) {
			throw new ClassificationProcessingException("Invalid filter classification configuration, has less than 3 elements: "+config.configuration);
		}
		ValueFactory vf = SimpleValueFactory.getInstance();
		Resource subj = parseResource(configurationElements[0], vf);
		IRI pred = (IRI) parseResource(configurationElements[1], vf);
		Value obj = parseValue(configurationElements[2], vf);
		// TODO add context filtering
		Model filtered = model.filter(subj, pred, obj);
		if (config.cbd) {
			return filterCbd(model, filtered);
		}
		return filtered;
	}
	
	/**
	 * Computes the concise bounded description of the given model, using all the subjects in the filtered model. Currently does not include content of blank nodes.
	 * @param model
	 * @param filtered
	 * @return a subset of the given model with all subjects in the filtered model
	 */
	private Model filterCbd(Model model, Model filtered) {
		Set<Resource> filteredSubjects = filtered.subjects();
		model.subjects().retainAll(filteredSubjects);
		return model;
	}

	private Resource parseResource(String configurationElement, ValueFactory vf) throws ClassificationProcessingException {
		if (configurationElement == null) {
			return null;			
		}
		if (StringUtils.equals("null", configurationElement)) {
			return null;
		}
		if (StringUtils.startsWith(configurationElement, "<")) {
			String iri = StringUtils.strip(configurationElement, "<>");
			return vf.createIRI(iri);
		}
		// else: prefixed
//		String prefix = StringUtils.substringBefore(configurationElement, ":");
//		Optional<Namespace> namespace = model.getNamespace(prefix);
//		if (namespace.isPresent()) {
//			IRI resource = vf.createIRI(namespace.get().getName(), StringUtils.substringAfter(configurationElement, ":"));
//			return resource;
//		}
//		else {
//			throw new ClassificationProcessingException("Received model does not contain prefix definition for configuration element "+configurationElement);
//		}
		throw new ClassificationProcessingException("Invalid resource in filter classifier configuration: "+configurationElement);
	}
	
	private Value parseValue(String configurationElement, ValueFactory vf) throws ClassificationProcessingException {
		if (StringUtils.startsWith(configurationElement, "\"")) {
			// literal
			int secondquoteindex = StringUtils.lastIndexOf(configurationElement, "\"");
			String label = StringUtils.substring(configurationElement, 1, secondquoteindex);
			String datatype = StringUtils.substringAfter(configurationElement, "^^");
			if (StringUtils.isEmpty(datatype)) {
				// check for language tag
				String language = StringUtils.substringAfterLast(configurationElement, "@");
				if (StringUtils.isNotEmpty(language)) {
					return vf.createLiteral(label, language);
				}
				else {
					return vf.createLiteral(label);
				}
			}
			else {
				// parse datatype, support xsd prefix
				if (StringUtils.startsWith(datatype, "xsd:")) {
					return vf.createLiteral(label, vf.createIRI(XMLSchema.NAMESPACE, datatype.substring(4)));
				}
				String datatypeIri = StringUtils.strip(datatype, "<>");
				try {
					vf.createIRI(datatypeIri);
					return vf.createLiteral(label, datatype);
				} catch (IllegalArgumentException e) {
					throw new ClassificationProcessingException("invalid datatype for filter classification configuration: "+configurationElement);
				}
			}
		}
		// no literal
		return parseResource(configurationElement, vf);
	}

}
