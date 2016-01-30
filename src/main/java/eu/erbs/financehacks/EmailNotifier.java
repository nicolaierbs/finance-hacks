package eu.erbs.financehacks;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static eu.erbs.financehacks.AccountRobot.loadProperties;

public class EmailNotifier {
	
	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
	
	final static String MAIL_PROPERTIES_PATH = "mail.properties";
	final static String MAIL_HOST = "MAIL_HOST";
	final static String MAIL_USER = "MAIL_USER";
	final static String MAIL_PASSWORD = "MAIL_PASSWORD";

	final static String SUBJECT_TAG = "[FINANCE HACKS]";

	
	private static final Logger log = Logger.getLogger(EmailNotifier.class.getName());
	public EmailNotifier() throws IOException{
		log.fine("Setup Mail Server Properties..");
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		
		loadProperties(MAIL_PROPERTIES_PATH, mailServerProperties);

		log.fine("Mail Server Properties have been setup successfully..");
	}

	public void sendEmail(String recipient, String subject, String emailBody) throws MessagingException{


		log.fine("Get Mail Session..");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.setFrom(new InternetAddress(mailServerProperties.getProperty(MAIL_USER)));
		generateMailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		generateMailMessage.setSubject(SUBJECT_TAG + " " + subject);
		generateMailMessage.setContent(getPoliteMail(emailBody), "text/html");
		log.info("Mail Session has been created successfully..");

		// Step3
		log.fine("Get Session and Send mail");
		Transport transport = getMailSession.getTransport("smtp");

		// Enter your correct gmail UserID and Password
		// if you have 2FA enabled then provide App Specific Password
		transport.connect(
				mailServerProperties.getProperty(MAIL_HOST),
				mailServerProperties.getProperty(MAIL_USER),
				mailServerProperties.getProperty(MAIL_PASSWORD));
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
		log.info("Sent email");
		
	}

	private String getPoliteMail(String emailBody) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Hi,");
		buffer.append("</br>");
		buffer.append("</br>");
		buffer.append(emailBody);
		buffer.append("</br>");
		buffer.append("</br>");
		buffer.append("Best wishes,");
		buffer.append("</br>");
		buffer.append("your stupid finance advisor");
		
		return buffer.toString();
	}
}