import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//Logger class for creating logFiles 
public class MyLogger {
	
//VARIABLES:
	private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

//METHODS:
	public void setLogger(){
		System.setProperty("java.util.logging.SimpleFormatter.format",
	              "[%1$tF %1$tT] [%4$-7s] %5$s %n");
		Handler fileHandler = null;
		Formatter simpleFormatter = null;
		try{
			Calendar c=Calendar.getInstance();
			Date d= c.getTime();
			DateFormat df= new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
			String s =df.format(d);
			// Creating FileHandler
			fileHandler = new FileHandler(".logfile "+s+".log");
			
			// Creating SimpleFormatter
			simpleFormatter = new SimpleFormatter();
			
			
			
	
			
			// Setting formatter to the handler
			fileHandler.setFormatter(simpleFormatter);
			// Assigning handler to logger
						LOGGER.addHandler(fileHandler);
			// Setting Level to ALL
			fileHandler.setLevel(Level.ALL);
			LOGGER.setLevel(Level.ALL);
			
			// Logging message of Level finest (this should be publish in the simple format)
		
			}
		catch(IOException exception){
			LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
		}
	}
	
	
	
	public Logger getLogger() {
		return LOGGER;
	}
	
	public void writeInfo(String s) {
		LOGGER.info(s);
	}
	public void writeWarning(String s) {
		LOGGER.warning(s);
		
	}
	public void writeConfig(String s) {
		LOGGER.config(s);
		
	}
	public void writeFine(String s) {
		LOGGER.fine(s);
		
	}
	public void writeFinest(String s) {
		LOGGER.finest(s);
		
	}
}
