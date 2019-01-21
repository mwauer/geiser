package org.aksw.geiser.classification;

import org.aksw.geiser.classification.processor.FilterClassificationProcessor;
import org.aksw.geiser.classification.processor.QueryClassificationProcessor;
import org.apache.commons.codec.binary.StringUtils;

public class ClassificationProcessorFactory {
	
	public static ClassificationProcessor getInstance(GeiserClassificationConfig config) throws ClassificationProcessingException {
		if (config == null) {
			throw new ClassificationProcessingException("Configuration is null");
		}
		if (StringUtils.equals(config.mode, "filter")) {
			return new FilterClassificationProcessor();
		}
		if (StringUtils.equals(config.mode, "query")) {
			return new QueryClassificationProcessor();
		}
		throw new ClassificationProcessingException("Unsupported classification mode: "+config.mode);
	}

}
