package eu.erbs.financehacks;

import java.io.IOException;
import java.math.BigDecimal;

import javax.mail.MessagingException;

import org.junit.Test;

public class GiroOptimizerTest {

	@Test
	public void testSendNotification() throws MessagingException, IOException {
		
		GiroOptimizer.sendNotification(new BigDecimal(2343245.45), true, new BigDecimal(100), "finance@erbs.eu");
		GiroOptimizer.sendNotification(new BigDecimal(234.56),  false, new BigDecimal(100), "finance@erbs.eu");
		GiroOptimizer.sendNotification(new BigDecimal(-45.56),  false, new BigDecimal(100),  "finance@erbs.eu");
	}

}
