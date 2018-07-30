

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
//Class that initiates Route-Request
// Saves, prepares and manages all returned duration values, Node Ids and Intersection coordinates
public class Route {
	
//VARIABLES:
	double[]Duration;
	String[]Nodes;
	ArrayList<City> Nodes_as_City= new ArrayList<City>();
	ArrayList<City> intersections= new ArrayList<City>();
	JSONObject Way;

//METHODS:
	// Initiates Route Request with Send_Request class
	// reads returned JSON Object and saves all information in Duration, Node and Intersections
	// Converts Nodes and Intersections in City Objects
	public void WayFromTo(Tour best) throws Exception {
		//Route Request
		try {
			Way=Send_Request.createRouteRequest(best);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//find JSON Arrays
		JSONArray routes = Way.getJSONArray("routes");			
		JSONObject r1 = routes.getJSONObject(0);
		JSONArray legs = r1.getJSONArray("legs");
		JSONObject l1= legs.getJSONObject(0);
		JSONObject annotation = l1.getJSONObject("annotation");
		JSONArray nodes=annotation.getJSONArray("nodes");
		JSONArray duration=annotation.getJSONArray("duration");
		JSONArray steps= l1.getJSONArray("steps");
		
		Nodes= new String[nodes.length()];
		Duration= new double[duration.length()];
		
		//Save values of JSON Arrays in Node and Duration
		for(int a=0;a<nodes.length();a++){
			if(a==nodes.length()-1){
			Nodes[a]=String.valueOf(nodes.getLong(a));
			}
			else{
			Nodes[a]=String.valueOf(nodes.getLong(a));
			Duration[a]=duration.getDouble(a);
			
			}
		}
	
		//Convert Node Ids in City Objects
		Nodes_as_City=Send_Request.getNodes(Nodes);
		

		//Create City objects of type "Interesection" from JSON Array "Intersections" in JSONObject "steps"
		//Receive longitude and latitude from location in JSONArray
		//First and last "intersection" JSON Object are type =="City" but keep their coordinates from responses 
		//-> Corresponding "Cities" in All_Cities will be updated in EA
		//ID of first and last intersections equals ID of corresponding city object of type "City"
		//ID of all other "Intersections" equal ID of corresponding city object of type "Node" from Nodes_as_City
		String id="";
		for( int step=0;step<steps.length();step++) {
			JSONObject s1= steps.getJSONObject(step);
			JSONArray intersects= s1.getJSONArray("intersections");
																	
			for(int inter=0; inter<intersects.length();inter++) {
				JSONArray location = intersects.getJSONObject(inter).getJSONArray("location");
				double [] position= new double[2];
				position[0]=location.getDouble(0);
				position[1]=location.getDouble(1);
				
				// first City Object "Intersection"
				if(inter==0&&step==0) {
					if(Run.runs==true) {
						id=best.getCity(1).getId();
					}
					else {
						id=best.getCity(0).getId();
					}
					City newCity= new City(id,"City",position);
					intersections.add(newCity);	
				}
				
				//last City Object "Intersection"
				else if(step==steps.length()-1 && inter==intersects.length()-1) {
					City newCity;
					if(All_Cities.checkForCities()==1) {
						id="0";
						newCity= new City(id,"City",position);
					}
					else {
						if(Run.runs==true) {
						id=best.getCity(2).getId();
						}
						else {
							id=best.getCity(1).getId();
						}
						newCity= new City(id,"City",position);
					}													
					intersections.add(newCity);	
				}
				//all other intersections
				else {
					//													
					for(int node=1; node<Nodes_as_City.size();node++) {
					if(Maths.round(position[1],6)==Maths.round(Nodes_as_City.get(node).getLatitude(),6)&&Maths.round(position[0],6)==Maths.round(Nodes_as_City.get(node).getLongitude(),6)) {
						 id=Nodes_as_City.get(node).getId();	
						}
					}														
					City newCity= new City(id,"Intersection",position);																		
					intersections.add(newCity);  
				}
			}
		}
	}

}
