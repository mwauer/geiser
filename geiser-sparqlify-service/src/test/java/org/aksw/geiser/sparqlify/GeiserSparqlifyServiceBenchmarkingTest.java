package org.aksw.geiser.sparqlify;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GeiserSparqlifyServiceBenchmarkingTest {

	private static final int BENCHMARK_MESSAGES = 10000;

	private static final Logger log = LoggerFactory.getLogger(GeiserSparqlifyServiceBenchmarkingTest.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private RabbitAdmin rabbitAdmin;

	@Value("classpath:/benchmark/input-fcd-realtime.json")
	private Resource inputFcdRt;

	@Value("classpath:/benchmark/input-fcd-historic1.json")
	private Resource inputFcdHist1;

	@Value("classpath:/benchmark/input-fcd-historic2.json")
	private Resource inputFcdHist2;

	@Value("classpath:/benchmark/input-fcd-historic3.json")
	private Resource inputFcdHist3;

	@Test
	public void benchmarkFcdRealtime() throws IOException {
		log.info("Generating {} messages for realtime FCD benchmark", BENCHMARK_MESSAGES);
		String inputContent = IOUtils.toString(inputFcdRt.getInputStream());
		Message message = MessageBuilder.withBody(inputContent.getBytes(StandardCharsets.UTF_8))
				.setContentType("application/json").build();

		MessageGenerator generator = new MessageGenerator() {

			@Override
			Message getMessage(int count) {
				return message;
			}

		};

		runBenchmark(generator);
	}

	@Test
	public void benchmarkFcdHistoric() throws IOException {
		log.info("Generating {} messages for historic FCD benchmark", BENCHMARK_MESSAGES);
		String inputContent = IOUtils.toString(inputFcdHist1.getInputStream());
		Message messageH1 = MessageBuilder.withBody(inputContent.getBytes(StandardCharsets.UTF_8))
				.setContentType("application/json").build();
		inputContent = IOUtils.toString(inputFcdHist2.getInputStream());
		Message messageH2 = MessageBuilder.withBody(inputContent.getBytes(StandardCharsets.UTF_8))
				.setContentType("application/json").build();
		inputContent = IOUtils.toString(inputFcdHist3.getInputStream());
		Message messageH3 = MessageBuilder.withBody(inputContent.getBytes(StandardCharsets.UTF_8))
				.setContentType("application/json").build();

		MessageGenerator generator = new MessageGenerator() {

			@Override
			Message getMessage(int count) {
				// distribution: 2x half size message, 1x average size, 1x
				// double size
				switch (count % 4) {
				case 0:
				case 1:
					return messageH1;
				case 2:
					return messageH2;
				default:
					return messageH3;
				}
			}

		};

		runBenchmark(generator);
	}

	protected void runBenchmark(MessageGenerator generator) {
		log.info("Purging test queue");
		rabbitAdmin.purgeQueue("test", false);
		log.info("Sending {} messages for sparqlify...", BENCHMARK_MESSAGES);
		for (int i = 0; i < BENCHMARK_MESSAGES; i++) {
			rabbitTemplate.send("geiser", "sparqlify.test", generator.getMessage(i));
		}
		log.info("Sent {} messages to sparqlify.test on geiser exchange", BENCHMARK_MESSAGES);
		log.info("Waiting for sparqlify service to process them");
		Object firstProcessed = null;
		long start = System.currentTimeMillis();
		try {
			Object processed = null;
			do {
				Properties queueProperties = rabbitAdmin.getQueueProperties("test");
				processed = queueProperties.get(RabbitAdmin.QUEUE_MESSAGE_COUNT);
				if (firstProcessed == null) {
					firstProcessed = processed;
				}
				log.info("Processed {}", processed);
				Thread.sleep(500);
			} while (!processed.toString().equals(BENCHMARK_MESSAGES + ""));
			long end = System.currentTimeMillis();
			log.info("Sparqlify service finished processing requests");
			// compute messages / second
			long duration = end - start;
			long measuredMessages = new Long(processed.toString()) - new Long(firstProcessed.toString());
			log.info("Finished benchmark in {}ms for {} processed messages = {} messages/second", duration,
					measuredMessages, measuredMessages * 1000 / duration);
		} catch (InterruptedException e) {
			log.info("Interrupted wait, shutting down test");
		}
	}

	abstract class MessageGenerator {

		abstract Message getMessage(int count);

	}

}
