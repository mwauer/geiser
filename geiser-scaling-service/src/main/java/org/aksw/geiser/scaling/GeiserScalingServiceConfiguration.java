package org.aksw.geiser.scaling;

import java.util.Map;

import org.aksw.geiser.configuration.GeiserServiceConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Maps;

@Configuration
public class GeiserScalingServiceConfiguration extends GeiserServiceConfiguration {

	private static final Logger log = LoggerFactory.getLogger(GeiserScalingServiceConfiguration.class);

	/**
	 * Parses the queue service mapping property into a map
	 * 
	 * @param queueServiceMapping
	 *            a string of format
	 *            queueName:dockerSwarmService,queueName2:dockerSwarmService2,..
	 *            .
	 * @return a {@link Map} of queue to service names
	 */
	@Bean
	public Map<String, String> mapping(@Value("${mapping}") String queueServiceMapping) {
		Map<String, String> map = Maps.newHashMap();
		String[] mappings = StringUtils.split(queueServiceMapping, ',');
		for (String mapping : mappings) {
			String[] fields = StringUtils.split(mapping, ':');
			if (fields.length != 2) {
				log.warn("Invalid mapping: {} ({} fields at {})", queueServiceMapping, fields.length, mapping);
			} else {
				log.info("Configured mapping from queue {} to service {}", fields[0], fields[1]);
				map.put(fields[0], fields[1]);
			}
		}
		return map;
	}

	@Bean
	public RabbitAdmin rabbitAdmin(@Autowired AmqpAdmin amqpAdmin) {
		if (amqpAdmin instanceof RabbitAdmin) {
			return (RabbitAdmin) amqpAdmin;
		} else
			throw new RuntimeException(
					"GEISER scaling service requires RabbitAdmin instance, but has configured " + amqpAdmin.getClass());
	}
}
