package eu.erbs.financehacks;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import eu.erbs.financehacks.notifier.EmailNotifier;
import me.figo.FigoException;
import me.figo.models.Transaction;

public class BalanceInformationRobot extends AccountRobot {

	final static String ACCOUNT_GIRO = "ACCOUNT_GIRO";
	final static String MAIL_RECIPIENT = "MAIL_RECIPIENT";
	final static String NOTIFICATION_FREQUENCY_DAYS = "NOTIFICATION_FREQUENCY_DAYS";
	
	private final static String TRANSACTIONS_TABLE_FORMAT = "<tr><td>%8.2f</td> <td>%23s</td> <td>%10tB %2te, %tY</td> <td>%80s</td></td></tr><br/>";

	protected static final String USER_PROPERTIES_PATH = "accountnotification.properties";

	private static final Logger log = Logger.getLogger(BalanceInformationRobot.class.getName());	

	public BalanceInformationRobot() throws IOException, FigoException {
		super(USER_PROPERTIES_PATH);
	}

	public static void main(String[] args) throws FigoException, IOException, MessagingException {

		BalanceInformationRobot informer = new BalanceInformationRobot();
		informer.start();
	}

	private void start() throws FigoException, IOException, MessagingException {
		String accountId = properties.getProperty(ACCOUNT_GIRO);
		BigDecimal balance = checkBalance(accountId);
		List<Transaction> transactions = getTransactions(accountId, Integer.valueOf(properties.getProperty(NOTIFICATION_FREQUENCY_DAYS)));
		log.info("Sending notifications");
		notify(balance, transactions);
	}

	private void notify(BigDecimal balance, List<Transaction> transactions) throws IOException, MessagingException {
		if(notifier == null){
			notifier = new EmailNotifier();
		}
		String balanceInformation = "</br>your current balance is " + balance.longValue() + " euro.";
		StringBuffer transactionInformation = new StringBuffer();
		Formatter formatter = new Formatter(transactionInformation);
		transactionInformation.append("<table cellspacing='10'><br/>");
		Calendar c = Calendar.getInstance();

		float sum = 0;
		for(Transaction transaction : transactions){
			transactionInformation.append("<br/>");
			c.setTime(transaction.getBookingDate());
			formatter.format(TRANSACTIONS_TABLE_FORMAT, transaction.getAmount(), filterBookingText(transaction.getBookingText()), c, c, c, transaction.getPurposeText());
			sum += transaction.getAmount().floatValue();
		}
		formatter.close();
		transactionInformation.append("</table>");
		
		transactionInformation.append("<br/>");
		transactionInformation.append("<br/>");
		transactionInformation.append("The total expense in this week were " + Math.round(sum*(-100)/100) + " euro.");

		notifier.sendNotification(
				properties.getProperty(MAIL_RECIPIENT),
				"Your balance is " + Math.round(balance.floatValue()*(-100)/100) + " euro",
				balanceInformation + transactionInformation.toString());
	}
}
