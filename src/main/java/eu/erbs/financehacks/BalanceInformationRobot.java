package eu.erbs.financehacks;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import me.figo.FigoException;

public class BalanceInformationRobot extends AccountRobot {
	
	final static String ACCOUNT_GIRO = "ACCOUNT_GIRO";
	final static String MAIL_RECIPIENT = "MAIL_RECIPIENT";
	
	protected static final String USER_PROPERTIES_PATH = "schokokonto.properties";

	private static final Logger log = Logger.getLogger(BalanceInformationRobot.class.getName());	

	public BalanceInformationRobot() throws IOException, FigoException {
		super(USER_PROPERTIES_PATH);
	}
	
	public static void main(String[] args) throws FigoException, IOException, MessagingException {

		BalanceInformationRobot informer = new BalanceInformationRobot();
		informer.start();
	}

	private void start() throws FigoException, IOException, MessagingException {
		BigDecimal balance = checkBalance(properties.getProperty(ACCOUNT_GIRO));
		log.info("Sending notifications");
		notify(balance);
	}

	private void notify(BigDecimal balance) throws IOException, MessagingException {
		if(notifier == null){
			notifier = new EmailNotifier();
		}
		String balanceInformation = "</br>your current balance is " + balance.longValue() + " euro.";

		notifier.sendEmail(
				properties.getProperty(MAIL_RECIPIENT),
				"Your balance is " + balance.longValue() + " euro",
				balanceInformation);
	}
}
