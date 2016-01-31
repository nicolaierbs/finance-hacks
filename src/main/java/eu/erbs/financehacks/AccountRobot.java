package eu.erbs.financehacks;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import me.figo.FigoConnection;
import me.figo.FigoException;
import me.figo.FigoSession;
import me.figo.internal.TokenResponse;
import me.figo.models.Transaction;

public abstract class AccountRobot {

	protected static final String CONFIG_PROPERTIES_PATH = "config.properties";
	protected static final String MAIL_RECIPIENT = "MAIL_RECIPIENT";
	protected static EmailNotifier notifier;

	protected FigoSession session;
	protected Properties properties;	

	private static final Logger log = Logger.getLogger(AccountRobot.class.getName());

	public static void loadProperties(String file, Properties properties) throws IOException {

		final InputStream input;
		if(AccountRobot.class.getClassLoader().getResource("resources/"+file) != null){
			input = AccountRobot.class.getClassLoader().getResourceAsStream("resources/"+file);
		}
		else{
			input = AccountRobot.class.getClassLoader().getResourceAsStream(file);
		}
		properties.load(input);
		input.close();
	}

	public AccountRobot(String userPropertiesPath) throws IOException, FigoException{

		properties = new Properties();
		loadProperties(CONFIG_PROPERTIES_PATH, properties);

		loadProperties(userPropertiesPath, properties);

		try{
			FigoConnection connection = new FigoConnection(properties.getProperty("CLIENT_ID"), properties.getProperty("CLIENT_SECRET"), "www.erbs.eu");
			TokenResponse response = connection.credentialLogin(properties.getProperty("FIGO_USER"), properties.getProperty("FIGO_PASSWORD"));
			session = new FigoSession(response.getAccessToken());
		}
		catch(FigoException e){
			e.printStackTrace();
			log.info("Could not create user session. Falling back to demo session...");
			session = new FigoSession("ASHWLIkouP2O6_bgA2wWReRhletgWKHYjLqDaqb0LFfamim9RjexTo22ujRIP_cjLiRiSyQXyt2kM1eXU2XLFZQ0Hro15HikJQT_eNeT_9XQ");
		}			
		log.info("FigoSession created");
	}

	protected BigDecimal checkBalance(String accountId) throws FigoException, IOException {
		BigDecimal amount = session.getAccount(accountId).getBalance().getBalance();
		log.info("Balance of " + accountId + ": " + amount);
		return amount;
	}

	protected List<Transaction> getTransactions(String accountId, int days) throws FigoException, IOException {
		Date date = new Date();
		date.setTime(date.getTime() - days * 1000 * 60 * 60 * 24);
		List<Transaction> transactions = new ArrayList<Transaction>();
		for (Transaction transaction : session.getTransactions(session.getAccount(accountId))) {
			if(transaction.getBookingDate().after(date)){
				transactions.add(transaction);
			}
		}
		return transactions;
	}

	protected static String filterBookingText(String bookingText) {
		bookingText = bookingText
				.replaceAll("Svwz\\+"," ")
				.replaceAll("Eref\\+"," ")
				.replaceAll("Mref\\+"," ");
		return bookingText;
	}



}