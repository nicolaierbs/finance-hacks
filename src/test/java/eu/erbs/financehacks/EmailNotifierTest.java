package eu.erbs.financehacks;

import java.io.IOException;

import javax.mail.MessagingException;

import org.junit.Test;

import eu.erbs.financehacks.notifier.EmailNotifier;
import eu.erbs.financehacks.notifier.Notifier;

public class EmailNotifierTest {

	@Test
	public void testSendEmail() throws IOException, MessagingException {
		Notifier notifier = new EmailNotifier();
		notifier.sendNotification(
				"nico@erbs.eu",
				"Test",
				"Dies ist ein kurzer Test.");
	}

}
