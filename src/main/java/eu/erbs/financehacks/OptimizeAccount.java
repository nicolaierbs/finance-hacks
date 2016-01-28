package eu.erbs.financehacks;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import me.figo.FigoConnection;
import me.figo.FigoException;
import me.figo.FigoSession;
import me.figo.internal.TokenResponse;
import me.figo.models.Account;

public class OptimizeAccount{

	public static void main(String[] args) throws FigoException, IOException {
		FigoSession session;

		Properties properties = loadProperties("user");

		FigoConnection connection = new FigoConnection(properties.getProperty("CLIENT_ID"), properties.getProperty("CLIENT_SECRET"), "www.erbs.eu");
		TokenResponse response = connection.credentialLogin(properties.getProperty("FIGO_USER"), properties.getProperty("FIGO_PASSWORD"));
		session = new FigoSession(response.getAccessToken());

		for(Account account : session.getAccounts()){
			System.out.println(account.getAccountId()  + "\t" + account.getBalance().getBalance());
		}
	}

	private static Properties loadProperties(String name) throws IOException {
		Properties properties = new Properties();
		InputStream input = null;

		input = new FileInputStream("src/main/resources/" + name + ".properties");
		properties.load(input);

		return properties;
	}

}