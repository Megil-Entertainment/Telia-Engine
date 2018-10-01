package ch.megil.teliaengine.logging;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogHandler {
	public static final String LOG_DIR = "log";
	public static final String LOG_FILE_FORMAT = LOG_DIR + "/telia-%u.%g.log";
	
	private static final Logger LOGGER = Logger.getGlobal();

	static {
		try {
			LOGGER.setLevel(Level.INFO);

			var file = new File(LOG_DIR);
			if (!file.exists()) {
				file.mkdir();
			}

			// up to ten log files with 5MB size each
			var fh = new FileHandler(LOG_FILE_FORMAT, 5_000_000, 10);
			fh.setFormatter(new SimpleFormatter());

			LOGGER.addHandler(fh);
		} catch (Exception e) {
			// runtime exception to stop program if something goes wrong
			// while setting up the log files
			throw new RuntimeException(e);
		}
	}

	public static void log(String msg, Level level) {
		LOGGER.log(level, msg);
	}

	public static void log(Throwable t, Level level) {
		var mb = new ThrowableMessageBuilder(System.out);
		t.printStackTrace(mb);
		log(mb.getMessage(), level);
	}

	public static void severe(String msg) {
		log(msg, Level.SEVERE);
	}

	public static void warning(String msg) {
		log(msg, Level.WARNING);
	}

	public static void info(String msg) {
		log(msg, Level.INFO);
	}
}
