package org.aksw.geiser.configuration;

import org.springframework.context.annotation.Configuration;

/**
 * Extend this class in your service module to enable GEISER core services,
 * including JSON parsing.
 * 
 * @author wauer
 *
 */
@Configuration
public abstract class GeiserServiceConfiguration extends RabbitListenerConfiguration {

}
