package eu.erbs.financehacks;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import me.figo.FigoException;

public class GiroOptimizer extends AccountRobot {

	final static String ACCOUNT_GIRO = "ACCOUNT_GIRO";
	final static String ACCOUNT_SAVINGS = "ACCOUNT_SAVINGS";
	final static String CHECK_FREQUENCY_DAYS = "CHECK_FREQUENCY_DAYS";
	final static String GIRO_DYNAMIC_MIN = "GIRO_DYNAMIC_MIN";
	final static String GIRO_MIN = "GIRO_MIN";
	
	protected static final String USER_PROPERTIES_PATH = "src/main/resources/user.properties";

	private static final Logger log = Logger.getLogger(GiroOptimizer.class.getName());	

	public GiroOptimizer() throws IOException, FigoException {
		super(USER_PROPERTIES_PATH);
	}

	public static void main(String[] args) throws FigoException, IOException, MessagingException {

		GiroOptimizer optimizer = new GiroOptimizer();
		optimizer.start();
	}

	public void start() throws IOException, FigoException, MessagingException{

		//checkBalance of giro accounts
		BigDecimal balance = checkBalance(properties.getProperty(ACCOUNT_GIRO));

		//Automatically find the maximum deposit for a given span of days
		BigDecimal minRequiredBalance;
		if(Boolean.parseBoolean(properties.getProperty(GIRO_DYNAMIC_MIN, "false"))){
			minRequiredBalance = maxTransactionVolume(properties.getProperty(ACCOUNT_GIRO), Integer.valueOf(properties.getProperty(CHECK_FREQUENCY_DAYS)));
		}
		else{
			minRequiredBalance = new BigDecimal(properties.getProperty(GIRO_MIN));
		}

		//Transfer money
		BigDecimal transferAmount = balance.subtract(minRequiredBalance);
		boolean transferred = transferMoney(properties.getProperty(ACCOUNT_GIRO), properties.getProperty(ACCOUNT_SAVINGS), transferAmount);

		//Send notification
		sendNotification(transferAmount, transferred, balance, properties.getProperty(MAIL_RECIPIENT));
	}

	protected void sendNotification(BigDecimal transferAmount, boolean transferred, BigDecimal balance, String recipient) throws MessagingException, IOException {
		log.info("Sending notifications");
		if(notifier == null){
			notifier = new EmailNotifier();
		}
		String balanceInformation = "</br>Your current balance is " + balance.longValue() + " euro.";
		if(transferred){
			notifier.sendEmail(recipient, transferAmount.longValue() + " euro from giro account transferred",
					"I transferred " + transferAmount.longValue() + " euro from your giro account to your savings account." + balanceInformation);
		}
		else if(transferAmount.longValue() < 0){
			notifier.sendEmail(recipient, "[WARNING] Balance of your giro account is low",
					"this is a quick warning that your balance of your giro account is low. I recommend you to transfer " + transferAmount.negate().longValue() + " euro to your giro account." + balanceInformation);
		}
		else{
			notifier.sendEmail(recipient, "No money from giro account transferred",
					"I did not transfer any money from your giro to your savings account. Everything's fine :-)" + balanceInformation);
		}
	}

	private boolean transferMoney(String giroAccount, String savingsAccount, BigDecimal amount) {
		log.info("Transfer EUR " +  amount + " from " + giroAccount + " to " + savingsAccount);
		// TODO Auto-generated method stub		
		return false;
	}

	private BigDecimal maxTransactionVolume(String accountId, int daysIntervall) {
//		Payment payment = new Payment();
//		session.submitPayment(payment, tanSchemeId, state);
		// TODO Add payment and find a way to enable TAN verification	
		log.info("Checking maximum transaction volume over " +  daysIntervall + " for " + accountId);
		return new BigDecimal(0);
	}

	private BigDecimal checkBalance(String accountId) throws FigoException, IOException {
		BigDecimal amount = session.getAccount(accountId).getBalance().getBalance();
		log.info("Balance of " + accountId + ": " + amount);
		return amount;
	}

}