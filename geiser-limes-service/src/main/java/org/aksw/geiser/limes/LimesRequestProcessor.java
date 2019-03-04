package org.aksw.geiser.limes;

import java.io.File;
import java.io.IOException;

import org.aksw.limes.core.controller.Controller;
import org.aksw.limes.core.controller.LimesResult;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.serializer.ISerializer;
import org.aksw.limes.core.io.serializer.SerializerFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public class LimesRequestProcessor {

	private static final Logger log = LoggerFactory.getLogger(LimesRequestProcessor.class);

	private String STORAGE_DIR_PATH = "/tmp/limes/";

	private String LOCK_DIR_PATH = "/lock/";

	private String CONFIG_FILE_PREFIX = "config_";

	/**
	 * Stores configuration given in message locally and executes it. The AMQP
	 * message ID is used as a job identifier.
	 * 
	 * @param message
	 * @throws IOException
	 */
	public ResultFiles process(Message message) throws IOException {
		String id = message.getMessageProperties().getMessageId();
		String filePath = STORAGE_DIR_PATH + CONFIG_FILE_PREFIX + id + ".xml";
		log.info("Storing configuration locally as {}", filePath);
		FileUtils.writeByteArrayToFile(new File(filePath), message.getBody());

		AConfigurationReader reader = new XMLConfigurationReader(filePath);
		Configuration config = reader.read();
		File tempDir = new File(STORAGE_DIR_PATH + id + "/");
		File lockDir = new File(tempDir + LOCK_DIR_PATH);
		if (!lockDir.exists()) {
			lockDir.mkdirs();
		}
		FileUtils.writeStringToFile(new File(tempDir + "/status"), "1");
		log.info("Starting processing of {}", filePath);

		LimesResult mappings = Controller.getMapping(config);
		log.info("Finished processing, preparing output for {}", filePath);
		String outputFormat = config.getOutputFormat();
		ISerializer output = SerializerFactory.createSerializer(outputFormat);
		output.setPrefixes(config.getPrefixes());

		File _verificationFile = new File(lockDir + "/" + config.getVerificationFile());
		File _acceptanceFile = new File(lockDir + "/" + config.getAcceptanceFile());
		File verificationFile = new File(tempDir + "/" + config.getVerificationFile());
		File acceptanceFile = new File(tempDir + "/" + config.getAcceptanceFile());
		output.writeToFile(mappings.getVerificationMapping(), config.getVerificationRelation(),
				_verificationFile.getAbsolutePath());
		output.writeToFile(mappings.getAcceptanceMapping(), config.getAcceptanceRelation(),
				_acceptanceFile.getAbsolutePath());
		_verificationFile.renameTo(verificationFile);
		_acceptanceFile.renameTo(acceptanceFile);
		lockDir.delete();

		FileUtils.writeStringToFile(new File(tempDir + "/status"), "2");
		log.info("Completed processing of {}", filePath);

		return new ResultFiles(acceptanceFile.getAbsolutePath(), verificationFile.getAbsolutePath());
	}

	public boolean isJobFinished(String id) {
		String statusPath = STORAGE_DIR_PATH + id + "/status";
		try {
			String status = FileUtils.readFileToString(new File(statusPath));
			log.info("got status {}", status);
			return status.equals("2");
		} catch (IOException e) {
			log.info("Tried to read status file for job " + id + " at "+statusPath, e);
			return false;
		}
	}

	public String getResult(String filename) throws IOException {
		String result = FileUtils.readFileToString(new File(filename));
		return result;
	}

	public class ResultFiles {
		final String acceptance;
		final String verification;

		public ResultFiles(String acceptance, String verification) {
			super();
			this.acceptance = acceptance;
			this.verification = verification;
		}

		public String getAcceptance() {
			return acceptance;
		}

		public String getVerification() {
			return verification;
		}

		@Override
		public String toString() {
			return "ResultFiles [acceptance=" + acceptance + ", verification=" + verification + "]";
		}

	}

}
