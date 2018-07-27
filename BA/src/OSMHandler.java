

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OSMHandler extends DefaultHandler
{
		double lat;
		double lon;
		String id;



    // Make sure that the code in DefaultHandler's
    // constructor is called:
    public OSMHandler()
    {
        super();
    }

    public City getNode() {
    	City n=new City(id,"Node",lon,lat);
    	return n;
    	
    }
   
    @Override
    public void startElement(String uri, String name, String qName, Attributes atts) throws SAXException
    {
    	 if (name.equals("node")) { 
    	     id=atts.getValue("id");
    	     lat=Double.parseDouble(atts.getValue("lat")); 
 	    	lon=Double.parseDouble(atts.getValue("lon"));
    	 }
    }
}
    	   