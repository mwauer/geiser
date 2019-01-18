package org.aksw.geiser.classification;

public class ClassificationProcessingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClassificationProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClassificationProcessingException(String message) {
		super(message);
	}

	public ClassificationProcessingException(Throwable cause) {
		super(cause);
	}

}
