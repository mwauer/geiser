package org.aksw.geiser.sparqlify;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SparqlifyServiceRequestConfiguration {
	
	/**
	 * Required content type of the given configuration. Accepted formats:
	 * text/sml for SML syntax, text/turtle for R2RML vocabulary in Turtle syntax
	 * @see https://github.com/AKSW/SML
	 */
	@JsonProperty(required = true)
	public String contentType;
	
	/**
	 * Required mapping configuration, e.g., the SML config
	 */
	@JsonProperty(required = true)
	public String data;
	
	/**
	 * Optional, if the mapping contains multiple views, provide the view to be used here
	 */
	@JsonProperty
	public String viewName;
	
	/**
	 * Optional (default=true), whether the given CSV file contains a header row
	 */
	@JsonProperty
	public Boolean headers = true;
	
	// CSV parsing parameters:
	@JsonProperty
	public Character fieldSeparator = ',';
	
	@JsonProperty
	public Character fieldDelimiter = '"';
	
	@JsonProperty
	public Character escapeCharacter = '\\';

	@Override
	public String toString() {
		return "SparqlifyServiceRequestConfiguration [contentType=" + contentType + ", data=" + data + ", viewName="
				+ viewName + ", headers=" + headers + ", fieldSeparator=" + fieldSeparator + ", fieldDelimiter="
				+ fieldDelimiter + ", escapeCharacter=" + escapeCharacter + "]";
	}

}
