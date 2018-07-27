

import com.opencsv.CSVWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;


public class wr {
	
	    private static final String STRING_ARRAY_SAMPLE = "./log123.csv";
	    
	    public static void main(String[] args) throws IOException {
	    	  PrintWriter pw = new PrintWriter(new File(STRING_ARRAY_SAMPLE));
	    	  pw.close();
	        try (
	        		
	        		

	        		
	        		Writer writer = Files.newBufferedWriter(Paths.get(STRING_ARRAY_SAMPLE));

	            CSVWriter csvWriter = new CSVWriter(writer,
	                    CSVWriter.DEFAULT_SEPARATOR,
	                    CSVWriter.NO_QUOTE_CHARACTER,
	                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	                    CSVWriter.DEFAULT_LINE_END);
	        ) {
	        	
	                
	            String[] headerRecord = {"Name", "Email", "Phone", "Country"};
	            csvWriter.writeNext(headerRecord);

	            csvWriter.writeNext(new String[]{"Sundar Pichai ", "sundar.pichai@gmail.com", "+1-1111111111", "India"});
	            csvWriter.writeNext(new String[]{"Satya Nadella", "satya.nadella@outlook.com", "+1-1111111112", "India"});
	       
	        }
try (
		Writer writer = Files.newBufferedWriter(Paths.get(STRING_ARRAY_SAMPLE));
	     CSVWriter csvWriter = new CSVWriter(writer,
	                    CSVWriter.DEFAULT_SEPARATOR,
	                    CSVWriter.NO_QUOTE_CHARACTER,
	                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	                    CSVWriter.DEFAULT_LINE_END);
	        ) 
	{
	            String[] headerRecord = {"Name", "Email", "Phone", "Country"};
	            csvWriter.writeNext(headerRecord);

	            csvWriter.writeNext(new String[]{"PENIS ", "sundar.pichai@gmail.com", "+1-1111111111", "India"});
	            csvWriter.writeNext(new String[]{"PENIS", "satya.nadella@outlook.com", "+1-1111111112", "India"});
	       
	        }
	    }
}