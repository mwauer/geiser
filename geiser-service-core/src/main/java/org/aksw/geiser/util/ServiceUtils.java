package org.aksw.geiser.util;

import org.springframework.amqp.core.Message;

/**
 * Utility class for writing GEISER services.
 */
public class ServiceUtils {
	
	public static String nextRoutingKey(String receivedRoutingKey) {
		if (receivedRoutingKey == null) {
			return null;
		}
		int firstDot = receivedRoutingKey.indexOf(".");
		String nextRoutingKey = receivedRoutingKey.substring(firstDot+1);
		return nextRoutingKey;
	}
	
	/**
	 * @param message an incoming message
	 * @return the next routing key
	 */
	public static String nextRoutingKey(Message message) {
		return nextRoutingKey(message.getMessageProperties().getReceivedRoutingKey());
	}

}
