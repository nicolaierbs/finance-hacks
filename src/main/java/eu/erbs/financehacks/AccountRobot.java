package eu.erbs.financehacks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import me.figo.FigoConnection;
import me.figo.FigoException;
import me.figo.FigoSession;
import me.figo.internal.TokenResponse;

public abstract class AccountRobot {

	protected static final String CONFIG_PROPERTIES_PATH = "src/main/resources/config.properties";
	protected static final String MAIL_RECIPIENT = "MAIL_RECIPIENT";
	protected static EmailNotifier notifier;
	
	protected FigoSession session;
	protected Properties properties;	

	private static final Logger log = Logger.getLogger(AccountRobot.class.getName());

	public static void loadProperties(File file, Properties properties) throws IOException {
		InputStream input = new FileInputStream(file);
		properties.load(input);
		input.close();
	}
	
	public AccountRobot(String userPropertiesPath) throws IOException, FigoException{
		
		properties = new Properties();
		loadProperties(new File(CONFIG_PROPERTIES_PATH), properties);

		File file = new File(userPropertiesPath);
		if(file.exists()){

			loadProperties(new File(userPropertiesPath), properties);

			FigoConnection connection = new FigoConnection(properties.getProperty("CLIENT_ID"), properties.getProperty("CLIENT_SECRET"), "www.erbs.eu");
			TokenResponse response = connection.credentialLogin(properties.getProperty("FIGO_USER"), properties.getProperty("FIGO_PASSWORD"));
			session = new FigoSession(response.getAccessToken());
		}
		else{
			session = new FigoSession("ASHWLIkouP2O6_bgA2wWReRhletgWKHYjLqDaqb0LFfamim9RjexTo22ujRIP_cjLiRiSyQXyt2kM1eXU2XLFZQ0Hro15HikJQT_eNeT_9XQ");			
		}
		log.info("FigoSession created");
	}

}