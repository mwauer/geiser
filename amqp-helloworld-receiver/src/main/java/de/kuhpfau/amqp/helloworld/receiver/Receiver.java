package de.kuhpfau.amqp.helloworld.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
	
	private final static Logger Log = LoggerFactory.getLogger(Receiver.class);

	public void handleMessage(HelloMessageBean bean) {
		Log.info("Got "+bean.hello);
	}
	
	public void handleMessage(String message) {
		Log.info("Got string "+message);
	}
	
	//public void handleMessage(byte[] message) {
		//Log.info("Got binary "+new String(message));
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	//}
	
	
}
