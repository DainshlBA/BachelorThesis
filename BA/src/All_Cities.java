import java.util.ArrayList;

//class that saves and manages all City objects that are relevant for the "Optimierer"
public class All_Cities {
//VARIABLES:
	// ArrayList that holds our current cities, intersections and GPS locations
	static ArrayList<City> destinationCities = new ArrayList<City>();
	
	//Saves city we start from
	static City startCity=Distanzmatrix.startCity;

//METHODS:	
	// Adds a City object of type "City", "Intersection" or "GPS"
	public static void addCity(City city) {
	        destinationCities.add(city);
	}
	// Deletes specific City object
	public static void deleteCity(City city)
	{
		destinationCities.remove(city);
	}
	
	//returns true if destinationCities contains a specific City object
	public static boolean containsCity(City city){
	    return destinationCities.contains(city);
	}
	// Returns City object at specific position in destinationCities
	public static City getCity(int index){
	        return destinationCities.get(index);
	}
	    
	// Get the number of City objects in destinationCities
	public static int numberOfCities(){
	        return destinationCities.size();
	}
	//Check how many City objects of Type="City" destinationCities contains
	public static int checkForCities() {
		int checkcounter=0;
		for(int check=0; check<All_Cities.numberOfCities();check++) {
			if(All_Cities.getCity(check).type=="City") {
				checkcounter++;
			}		
		}
		return checkcounter;
	}
    public static int PositionofCity(City city){
        int pos=-1;
      	for( int c=0;c<numberOfCities();c++) {
      			if(city.getId()==getCity(c).getId()) {
      				pos=c;
      				break;
      			
      		}
          }
      	return pos;
      }
	//Check how many City objects of Type="Intersection" destinationCities contains
	public static int checkForIntersection() {
		int checkcounter=0;
		for(int check=All_Cities.numberOfCities()-2; check<All_Cities.numberOfCities();check++) {
			if(All_Cities.getCity(check).type=="Intersection") {
				checkcounter++;
			}		
		}
		return checkcounter;
	}
}

