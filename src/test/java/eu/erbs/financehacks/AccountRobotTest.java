package eu.erbs.financehacks;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

public class AccountRobotTest {

	@Test
	public void testPure() throws IOException {
		test("config.properties");
	}
	
	@Test
	public void testSlash() throws IOException {
		test("/config.properties");
	}
	
	@Test
	public void testClasspath() throws IOException {
		test("classpath:/config.properties");
	}
	
	@Test
	public void testResources() throws IOException {
		test("/resources/config.properties");
	}
	
	public void test(String name) throws IOException {
		Properties properties = new Properties();
		AccountRobot.loadProperties(name, properties);
		assertNotNull(properties.getProperty("GIRO_MIN"));
	}

}
