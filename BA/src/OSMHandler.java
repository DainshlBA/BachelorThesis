import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

//class that handles the XML content of the OSM Convert-API

public class OSMHandler extends DefaultHandler{
//VARIABLES:
	
	double lat;
	double lon;
	String id;

//CONSTRUCTOR:
    public OSMHandler()
    {
        super();
    }

//METHODS:
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
    	   