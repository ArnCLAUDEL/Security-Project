package util;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cheat {
	public static final String CAPTAIN_OBVIOUS_URL = "https://en.wiktionary.org/wiki/Captain_Obvious";
	public static final String ONISEP_URL = "http://www.onisep.fr";
	public static final String EXAMPLE_URL = "http://www.example.org";
	public static final long DEFAULT_ID = -1;
	public final static Random RANDOM = new Random();
	public final static Charset CHARSET = Charset.forName("UTF-8");
	public final static Logger LOGGER = Logger.getLogger("WEB-CRAWLER");
	
	public static void setLoggerLevelDisplay(Level level) {
		setLoggerLevelDisplay(LOGGER, level);
	}
	
	public static long getId() {
		return RANDOM.nextInt();
	}
	
	private static void setLoggerLevelDisplay(Logger logger, Level level) {
		Handler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(level);
		logger.addHandler(consoleHandler);
		logger.setLevel(level);
		logger.setUseParentHandlers(false);
		Logging.setLoggerLevelDisplay(level);
	}
	
	private void httpRequest() {
		/*
		SocketChannel s = SocketChannel.open(new InetSocketAddress("www.onisep.fr", 80));
		ByteBuffer b = ByteBuffer.allocate(2048);
		b.put(("GET /Formation-et-handicap HTTP/1.1\r\n" +  
		"Host: www.onisep.fr\r\n" + 
		"Connection: Keep-Alive\r\n\r\n").getBytes());
		
		b.flip();
		s.write(b);
		
		Set<String> links = getContent(s.socket().getInputStream());
		*/
	}
}
