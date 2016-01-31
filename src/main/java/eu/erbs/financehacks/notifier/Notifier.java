package eu.erbs.financehacks.notifier;

import javax.mail.MessagingException;

public interface Notifier {

	void sendNotification(String recipient, String subject, String emailBody) throws MessagingException;

}