package org.aksw.geiser.service.discovery;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;

public class ServiceDiscoveryResponse {

	/**
	 * service name
	 */
	public String service;

	/**
	 * class of service
	 */
	public String type;

	/**
	 * JSON schema the service expects
	 */
	public JsonSchema requestSchema;

	/**
	 * JSON schema of the response the service creates, optional
	 */
	public JsonSchema responseSchema;

}