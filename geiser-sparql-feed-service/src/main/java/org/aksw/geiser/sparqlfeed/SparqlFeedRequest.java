package org.aksw.geiser.sparqlfeed;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SparqlFeedRequest {

	@JsonProperty
	private String endpoint;
	
	@JsonProperty(defaultValue="CONSTRUCT WHERE { ?s ?p ?o } LIMIT 10")
	private String query = "CONSTRUCT WHERE { ?s ?p ?o } LIMIT 10";
	
	@JsonProperty(defaultValue="1000")
	private Integer messageSendRate = 1000;
	
	@JsonProperty(defaultValue="text/turtle")
	private String rdfFormat = "text/turtle";

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Integer getMessageSendRate() {
		return messageSendRate;
	}

	public void setMessageSendRate(Integer messageSendRate) {
		this.messageSendRate = messageSendRate;
	}

	public String getRdfFormat() {
		return rdfFormat;
	}

	public void setRdfFormat(String rdfFormat) {
		this.rdfFormat = rdfFormat;
	}

	@Override
	public String toString() {
		return "SparqlFeedRequest [endpoint=" + endpoint + ", query=" + query + ", messageSendRate=" + messageSendRate
				+ ", rdfFormat=" + rdfFormat + "]";
	}

}
