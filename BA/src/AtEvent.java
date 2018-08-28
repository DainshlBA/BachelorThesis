import java.util.EventObject;

// class for all events that represent an "arrived" status 
// the event can be one out of three diffrent types of arriving: "AtCity", "AtIntersection" or "GPS_SinEAl"
public class AtEvent extends EventObject {

//VARIABLES:
	private static final long serialVersionUID = 1L;
	
	//String that represents that type of arriving
	public String status;
	
	//Time at which the event occurs in Millis
	long EventTime;
	
	//City object with location coordinates, ID and type
	City location;
	
//CONSTRUCTOR	
	public AtEvent(Simulator salesman, City loc, long Time) {
		super(salesman);
		this.location=loc;
		this.EventTime=Time;
	
		
	}
	
//METHODS:
	public long getEventTime(){
		return EventTime;
	}
	public String getEventType(){
		return location.type;
	}
	public double getLongitude() {
		return location.position[0];
	}
	public double getLatitude() {
		return location.position[1];
	}
	public String toString() {
		 String s="";
		 s="EventTime: "+EventTime+" ID: "+location.id+" ,Type: "+location.getType()+" Lon: "+location.getLongitude()+" Lat: "+location.getLatitude();
		 return s;
	 }
}
