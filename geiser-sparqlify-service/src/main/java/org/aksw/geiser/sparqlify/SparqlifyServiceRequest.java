package org.aksw.geiser.sparqlify;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SparqlifyServiceRequest {

	@JsonProperty
	public SparqlifyServiceRequestConfiguration config;
	
	@JsonProperty
	public String data;

	@Override
	public String toString() {
		return "SparqlifyServiceRequest [config=" + config + ", data=" + data + "]";
	}
	
	
}
