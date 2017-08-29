package org.aksw.geiser.sparqlify;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.aksw.jena_sparql_api.utils.SparqlFormatterUtils;
import org.aksw.sparqlify.config.syntax.NamedViewTemplateDefinition;
import org.aksw.sparqlify.config.syntax.TemplateConfig;
import org.aksw.sparqlify.config.syntax.ViewTemplateDefinition;
import org.aksw.sparqlify.csv.CsvMapperCliMain;
import org.aksw.sparqlify.csv.CsvParserConfig;
import org.aksw.sparqlify.csv.InputSupplierCSVReader;
import org.aksw.sparqlify.csv.TripleIteratorTracking;
import org.aksw.sparqlify.validation.LoggerCount;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;
//import org.apache.jena.rdf.model.Model;
//import org.apache.jena.rdf.model.ModelFactory;
//import org.apache.tools.ant.filters.StringInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.InputSupplier;

import au.com.bytecode.opencsv.CSVReader;

public class SparqlifyRequestProcessor {

	private static final Logger log = LoggerFactory.getLogger(SparqlifyRequestProcessor.class);

	/**
	 * Transforms the CSV in the given request into Turtle.
	 * @param request a {@link SparqlifyServiceRequest}
	 * @return the resulting text/turtle document
	 * @throws IOException
	 * @throws RecognitionException
	 */
	public String process(SparqlifyServiceRequest request) throws IOException, RecognitionException {

		// parse config
		TemplateConfig templateConfig = readConfig(request);

		// stuff copied from csv mapper CLI main:
		List<NamedViewTemplateDefinition> views = templateConfig.getDefinitions();

		if (views.isEmpty()) {
			log.warn("No view definitions found");
		}

		// Index the views by name
		Map<String, NamedViewTemplateDefinition> viewIndex = CsvMapperCliMain.indexViews(views, log);
		ViewTemplateDefinition view;

		view = CsvMapperCliMain.pickView(viewIndex, request.config.viewName);

		// read input data
		InputSupplier<StringReader> inputSupplier = new InputSupplier<StringReader>() {

			@Override
			public StringReader getInput() throws IOException {
				return new StringReader(request.data);
			}

		};

		CsvParserConfig csvConfig = new CsvParserConfig();
		csvConfig.setEscapeCharacter(request.config.escapeCharacter);
		csvConfig.setFieldDelimiter(request.config.fieldDelimiter);
		csvConfig.setFieldSeparator(request.config.fieldSeparator);

		InputSupplier<CSVReader> csvReaderSupplier = new InputSupplierCSVReader(inputSupplier, csvConfig);

		ResultSet resultSet = CsvMapperCliMain.createResultSetFromCsv(csvReaderSupplier, request.config.headers, 100);


		TripleIteratorTracking trackingIt = CsvMapperCliMain.createTripleIterator(resultSet, view);

		// writeTriples(System.out, trackingIt);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		SparqlFormatterUtils.writeTurtle(outputStream, trackingIt);
		CsvMapperCliMain.writeSummary(System.err, trackingIt.getState());

		return outputStream.toString(Charsets.UTF_8.name());
	}

	private TemplateConfig readConfig(SparqlifyServiceRequest request) throws IOException, RecognitionException {
		LoggerCount loggerCount = new LoggerCount(log);

		String serializedConfig = request.config.data;
		
		// convert from R2RML to SML if necessary
		if (request.config.contentType.startsWith("text/turtle")) {
			log.info("Detected text/turtle configuration, assuming R2RML. Attempting conversion to SML.");

			serializedConfig = R2RMLConverter.convertToSml(serializedConfig);
			log.info("Using translated SML:\n{}", serializedConfig);
		}

		TemplateConfig templateConfig = CsvMapperCliMain.readTemplateConfig(IOUtils.toInputStream(request.config.data),
				loggerCount);

		log.info("Errors: " + loggerCount.getErrorCount() + ", Warnings: " + loggerCount.getWarningCount());

		if (loggerCount.getErrorCount() > 0) {
			throw new RuntimeException(
					"Encountered " + loggerCount.getErrorCount() + " errors that need to be fixed first.");
		}
		return templateConfig;
	}

}
