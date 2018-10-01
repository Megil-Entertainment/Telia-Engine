package ch.megil.teliaengine.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LogHandlerTest {
	private static final String LOG_0 = "/telia-0.0.log";
	private static final File LOG_DIR = new File(LogHandler.LOG_DIR);
	
	private String getLastLogLine() {
		var log = "";
		
		try (var reader = new BufferedReader(new FileReader(LogHandler.LOG_DIR + LOG_0))) {
			var line = "";
			while ((line = reader.readLine()) != null) {
				log = line;
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		return log;
	}
	
	private String getLastLogLineNoTab() {
		var log = "";
		
		try (var reader = new BufferedReader(new FileReader(LogHandler.LOG_DIR + LOG_0))) {
			var line = "";
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("\t") && !line.isEmpty()) {
					log = line;
				}
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		return log;
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (LOG_DIR.exists()) {
			for (var f : LOG_DIR.listFiles()) {
				f.delete();
			}
		}
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testLogStringLevel() {
		var logLine = "general log test";
		
		LogHandler.log(logLine, Level.SEVERE);
		var log = getLastLogLine();
		assertEquals("SEVERE: " + logLine, log);
		
		LogHandler.log(logLine, Level.WARNING);
		log = getLastLogLine();
		assertEquals("WARNING: " + logLine, log);
		
		LogHandler.log(logLine, Level.INFO);
		log = getLastLogLine();
		assertEquals("INFO: " + logLine, log);
	}

	@Test
	public void testLogThrowableLevel() {
		var testExeption = new IOException("test exception");
		
		LogHandler.log(testExeption, Level.SEVERE);
		var log = getLastLogLineNoTab();
		var last = getLastLogLine();
		assertEquals("SEVERE: java.io.IOException: " + testExeption.getMessage(), log);
		assertTrue(last.isEmpty());
	}

	@Test
	public void testSevere() {
		var severe = "severe log test";
		
		LogHandler.severe(severe);
		var log = getLastLogLine();
		assertEquals("SEVERE: " + severe, log);
	}

	@Test
	public void testWarning() {
		var warning = "warning log test";
		
		LogHandler.warning(warning);
		var log = getLastLogLine();
		assertEquals("WARNING: " + warning, log);
	}

	@Test
	public void testInfo() throws IOException {
		var info = "information log test";
		
		LogHandler.info(info);
		var log = getLastLogLine();
		assertEquals("INFO: " + info, log);
	}

}
