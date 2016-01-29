package eu.erbs.financehacks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import me.figo.FigoConnection;
import me.figo.FigoException;
import me.figo.FigoSession;
import me.figo.internal.TokenResponse;

public class GiroOptimizer{

	final static String USER_PROPERTIES_PATH = "src/main/resources/user.properties";
	final static String CONFIG_PROPERTIES_PATH = "src/main/resources/config.properties";
	
	final static String ACCOUNT_GIRO = "ACCOUNT_GIRO";
	final static String ACCOUNT_SAVINGS = "ACCOUNT_SAVINGS";
	final static String CHECK_FREQUENCY_DAYS = "CHECK_FREQUENCY_DAYS";
	final static String GIRO_DYNAMIC_MIN = "GIRO_DYNAMIC_MIN";
	final static String GIRO_MIN = "GIRO_MIN";
	final static String MAIL_RECIPIENT = "MAIL_RECIPIENT";
	
	private static EmailNotifier notifier;
	
	
	private static final Logger log = Logger.getLogger(GiroOptimizer.class.getName());	

	public static void main(String[] args) throws FigoException, IOException, MessagingException {
		FigoSession session;

		Properties properties = new Properties();
		loadProperties(new File(CONFIG_PROPERTIES_PATH), properties);

		File file = new File(USER_PROPERTIES_PATH);
		if(file.exists()){
			
			loadProperties(new File(USER_PROPERTIES_PATH), properties);

			FigoConnection connection = new FigoConnection(properties.getProperty("CLIENT_ID"), properties.getProperty("CLIENT_SECRET"), "www.erbs.eu");
			TokenResponse response = connection.credentialLogin(properties.getProperty("FIGO_USER"), properties.getProperty("FIGO_PASSWORD"));
			session = new FigoSession(response.getAccessToken());
		}
		else{
			session = new FigoSession("ASHWLIkouP2O6_bgA2wWReRhletgWKHYjLqDaqb0LFfamim9RjexTo22ujRIP_cjLiRiSyQXyt2kM1eXU2XLFZQ0Hro15HikJQT_eNeT_9XQ");			
		}

		//checkBalance of giro accounts
		BigDecimal balance = checkBalance(session, properties.getProperty(ACCOUNT_GIRO));

		//Automatically find the maximum deposit for a given span of days
		BigDecimal minRequiredBalance;
		if(Boolean.parseBoolean(properties.getProperty(GIRO_DYNAMIC_MIN, "false"))){
			minRequiredBalance = maxTransactionVolume(session, properties.getProperty(ACCOUNT_GIRO), Integer.valueOf(properties.getProperty(CHECK_FREQUENCY_DAYS)));
		}
		else{
			minRequiredBalance = new BigDecimal(properties.getProperty(GIRO_MIN));
		}
		
		//Transfer money
		BigDecimal transferAmount = balance.subtract(minRequiredBalance);
		boolean transferred = transferMoney(session, properties.getProperty(ACCOUNT_GIRO), properties.getProperty(ACCOUNT_SAVINGS), transferAmount);

		//Send notification
		sendNotification(transferAmount, transferred, balance, properties.getProperty(MAIL_RECIPIENT));

	}

	protected static void sendNotification(BigDecimal transferAmount, boolean transferred, BigDecimal balance, String recipient) throws MessagingException, IOException {
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

	private static boolean transferMoney(FigoSession session, String giroAccount, String savingsAccount, BigDecimal amount) {
		log.info("Transfer EUR " +  amount + " from " + giroAccount + " to " + savingsAccount);
		// TODO Auto-generated method stub		
		return false;
	}

	private static BigDecimal maxTransactionVolume(FigoSession session, String accountId, int daysIntervall) {
		log.info("Checking maximum transaction volume over " +  daysIntervall + " for " + accountId);
		// TODO Auto-generated method stub		
		return new BigDecimal(0);
	}

	private static BigDecimal checkBalance(FigoSession session, String accountId) throws FigoException, IOException {
		BigDecimal amount = session.getAccount(accountId).getBalance().getBalance();
		log.info("Balance of " + accountId + ": " + amount);
		return amount;
	}

	public static void loadProperties(File file, Properties properties) throws IOException {
		InputStream input = new FileInputStream(file);
		properties.load(input);
		input.close();
	}

}