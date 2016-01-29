package eu.erbs.financehacks;

import java.io.IOException;
import java.math.BigDecimal;

import javax.mail.MessagingException;

import org.junit.Test;

import me.figo.FigoException;

public class GiroOptimizerTest {
	
	@Test
	public void testSendNotification() throws MessagingException, IOException, FigoException {
		
		GiroOptimizer optimizer = new GiroOptimizer();
		
		optimizer.sendNotification(new BigDecimal(2343245.45), true, new BigDecimal(100), "finance@erbs.eu");
		optimizer.sendNotification(new BigDecimal(234.56),  false, new BigDecimal(100), "finance@erbs.eu");
		optimizer.sendNotification(new BigDecimal(-45.56),  false, new BigDecimal(100),  "finance@erbs.eu");
	}

}
