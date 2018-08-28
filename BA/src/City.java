//Class that represents one element of an individual/Tour
// Can be of type "City", "Intersection", "GPS" or "Node"
//"City" and "GPS" have chronological IDs,
//"Nodes" receive ID through RouteRequest, "Intersection" has ID of corresponding "Node"/"City"
public class City {
//VARIABLES:
	//Array that holds longitude [0] and latitude [1]
	double[] position;								
	public String type;
	public String id;
	
//CONSTRUCTOR
	public City(String id,String type, double...position) {
		super();
		this.id = id;
		this.type=type;
		this.position = position;
	}
	public City ( City c) {
		this.position=c.position;
		this.type=c.type;
		this.id=c.id;
	}
	
//METHODS:
	public String getId() {
			return id;
		}
	 
	public void setID(String s) {
		id=s;
	}
	
	public double[] getPosition() {
		return position;
	}
	    
	public double getLongitude(){
	        return position[0];
	}
	    
	public double getLatitude(){
	        return position[1];
	}
	 
	public void setCoordinates(double lon, double lat) {
		 position[0]=lon;
		 position[1]=lat;
	 }
	
	public String getType() {
		 return type;
	 }
	
	public void setType(String type) {
			this.type=type;
		}
		
	public String toString() {
		  String str;
		  str="ID:"+this.id+" ( "+this.getLongitude()+" "+this.getLatitude()+" )";
		  return str;
	  }

}