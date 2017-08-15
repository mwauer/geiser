/**
 * 
 */
package org.aksw.geiser.service.discovery;

/**
 * @author wauer
 *
 */
public class ServiceDiscoveryRequest {
	
	/**
	 * service name pattern, can contain simple wildcards (*, ?), e.g., "*translate*" would match geiser-translate-service
	 */
	public String serviceQuery;
	
	/**
	 * type pattern, can contain simple wildcards (*, ?), e.g., "*link*" would match "interlinking"
	 */
	public String typeQuery;

}
