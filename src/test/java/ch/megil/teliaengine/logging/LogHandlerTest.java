package ch.megil.teliaengine.logging;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LogHandlerTest {
	private static final File TEMP_LOG_DIR = new File("tlog");
	private static final File LOG_DIR = new File(LogHandler.LOG_DIR);
	
	private static void deleteAllLogs() {
		if (LOG_DIR.exists()) {
			for (var f : LOG_DIR.listFiles()) {
				f.delete();
			}
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (LOG_DIR.exists()) {
			LOG_DIR.renameTo(TEMP_LOG_DIR);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (TEMP_LOG_DIR.exists()) {
			deleteAllLogs();
			LOG_DIR.delete();
			TEMP_LOG_DIR.renameTo(LOG_DIR);
		}
	}

	@Before
	public void setUp() throws Exception {
		deleteAllLogs();
	}

	@Test
	public void testLogStringLevel() {
		fail("Not yet implemented");
	}

	@Test
	public void testLogThrowableLevel() {
		fail("Not yet implemented");
	}

	@Test
	public void testSevere() {
		fail("Not yet implemented");
	}

	@Test
	public void testWarning() {
		fail("Not yet implemented");
	}

	@Test
	public void testInfo() {
		fail("Not yet implemented");
	}

}
