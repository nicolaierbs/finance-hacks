package eu.erbs.financehacks;

import java.io.IOException;

import javax.mail.MessagingException;

import org.junit.Test;

public class EmailNotifierTest {

	@Test
	public void testSendEmail() throws IOException, MessagingException {
		EmailNotifier notifier = new EmailNotifier();
		notifier.sendEmail(
				"nico@erbs.eu",
				"Test",
				"Dies ist ein kurzer Test.");
	}

}
