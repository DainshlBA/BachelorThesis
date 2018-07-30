

import java.util.ArrayList;
import java.util.Collections;

// Class representing an individual or solution
//Holds City objects in ArrayList
//Calculates its own fitness and total duration
public class Tour {

//VARIABLES:
    // Holds our tour of cities
    ArrayList<City> tour= new ArrayList<City>();
    double fitness = 0;
    double totalduration = 0;
    int rank=0;
    static int highestRank=EA.popSize;
	double IntersectionValue=0;
	 double allsymmValue=0;
//CONSTRUCTOR:
    //Constructs blank tour
    public Tour(){
        for (int i = 0; i < All_Cities.numberOfCities(); i++) {
            tour.add(null);
        }
    }
    //Constructs Tour with City objects of argument ArrayList
    public Tour(ArrayList<City> tour){
        this.tour = tour;
    }
    
    public Tour(Tour t) {
    	for( int tt=0; tt<t.tourSize();tt++) {
    		City cn= new City(t.getCity(tt).getId(), t.getCity(tt).getType(),t.getCity(tt).getPosition());
    		tour.add(tt, cn);
    	}
        
    }

//METHODS:
    // Creates a random individual
    public void generateIndividual() {
        // Loop through all our destination cities and add them to our tour
        for (int cityIndex = 0; cityIndex < All_Cities.numberOfCities(); cityIndex++) {
          setCity(cityIndex, All_Cities.getCity(cityIndex));     

        }
        // Randomly reorder the tour
        Collections.shuffle(tour);
        int startcitypos= tour.indexOf(All_Cities.getCity(0));
        tour.set(startcitypos, tour.get(0));
        tour.set(0, All_Cities.getCity(0));
       
    }
    
    //Get size of the tour
    public int tourSize() {
        return tour.size();
    }
   
    //Gets the City at a position
    public City getCity(int tourPosition) {
        return (City)tour.get(tourPosition);
    }
    
    //Returns true if the tour contains a certain city
    public boolean containsCity(City city){
      boolean contains=false;
    	for( int c=0;c<tourSize();c++) {
    		if(getCity(c)!=null) {
    			if(city.getId()==getCity(c).getId()) {
    				contains=true;
    				break;
    			}
    		}
        }
    	return contains;
    }
    
    //Get the position of a certain
    public int positionofCity(City city) {    
    	int pos=-1;
    	for( int c=0;c<tourSize();c++) {
    	   	if(city.getId().equals(getCity(c).getId())) {
    	      	pos=c;
    	      	break;
    	     	}
    	}
    	return pos;
    }
    
    //Delete the city at certain position
    public void deleteCity(int pos) {
    	tour.remove(pos);
    }
   
    // Add a city at a certain position within the tour
    public void addatPosition(int pos, City city) {
    	tour.add(pos,city );
    }
    
    // Sets a city in a certain position within a tour
    public void setCity(int tourPosition, City city) {
        tour.set(tourPosition, city);
       
    }

    //Check if two tours got the same order by comparing IDs
    public boolean checkforOrderDiffrence(Tour last) {
    	boolean change=false;
    	for(int a=0;a<this.tourSize();a++) {		
    		if(!this.getCity(a).getId().equals(last.getCity(a).getId())) {
    			change=true;
    			break;
    		}
    	}
    	return change;
    }
 
  
    // Gets the tours rank based fitness according to selection pressure
    public double getFitness() {
        if (fitness == 0) {    	
        	fitness=(2-EA.selectionPressure)+2*(EA.selectionPressure-1)*((double)(rank-1)/(double)(highestRank-1));
        }
        return fitness;
    }
     
    //Calculates the total duration of the tour depending on the status of the algorithm and procoess 
    public double getDuration() {
    	
    	totalduration=0;
    	//Values just needed for logger results evaluation
    	IntersectionValue=0;
    	allsymmValue=0;
	
    	//Calculation of duration in dynamic environment
    	if(EA.START==true) {
			//actual hour
    		int hour= EA.lastEventTime.getHour();
			//Time in Millis at next full hour
        	long nexthour=EA.lastEventTime.timeAtNextHour;
			//Sum for comparing if there is an houroverlaps
        	long sumMilli=EA.lastEventTime.startInMilli;
			//value of next full hour
        	int h_next;
			//References the start in the tour for summation of Matrix values
    		int index=1;
			
			//Add toDriveto Value from EA to totalduration, first part of calculation
	    	totalduration =EA.toDrivetoCity+EA.toDrivetoIntersection;

	    	//Check for special cases of hour variable
	    	sumMilli+=totalduration*1000;
	    	if(sumMilli>nexthour) { //Annahme nich größer als 60 min;
	    		hour++;
	    		nexthour+=3600000;	
	    	}	
	    	if(hour==24) {
				hour=0;
			}
	    	if(hour==23) {
				h_next=0;
			}
			else {
				h_next=hour+1;
			}
	    	
	    	// If situation requires a value of the "Intersection" matrix range
	    	if(EA.OP_Stop==false&&this.getCity(1).getType()=="Intersection") {
	    		//Get ID for selecting correct value in intersection matrix range
	    		int a=Integer.parseInt(this.getCity(2).getId());
	    		//General calculation process
				//check for hour overlaps and calculate duration by ratios
				 if(sumMilli+Distanzmatrix.allMatrix.get(hour)[Distanzmatrix.CreatingnumOfCities][a]*1000>nexthour) {
 						long houroverlaps=(long)(sumMilli+Distanzmatrix.allMatrix.get(hour)[Distanzmatrix.CreatingnumOfCities][a]*1000-nexthour);						//	System.out.print(" tohour: "+tohour);
 						double houroverlapsratio= Maths.round(houroverlaps/(Distanzmatrix.allMatrix.get(hour)[Distanzmatrix.CreatingnumOfCities][a]*1000),5);									// Calculate ratio of driven way in this sectio						//System.out.print(" hourratio: "+hourratio);
    					totalduration+=(1-houroverlapsratio)*Distanzmatrix.allMatrix.get(hour)[Distanzmatrix.CreatingnumOfCities][a]+(houroverlapsratio)*Distanzmatrix.allMatrix.get(h_next)[Distanzmatrix.CreatingnumOfCities][a];		//multiply ratio with value*factor of past hour and the reverse ratio with the value*factor of upcoming hour
    					IntersectionValue+=(1-houroverlapsratio)*Distanzmatrix.allMatrix.get(hour)[Distanzmatrix.CreatingnumOfCities][a]+(houroverlapsratio)*Distanzmatrix.allMatrix.get(h_next)[Distanzmatrix.CreatingnumOfCities][a];
    					sumMilli+=(1-houroverlapsratio)*Distanzmatrix.allMatrix.get(hour)[Distanzmatrix.CreatingnumOfCities][a]*1000+(houroverlapsratio)*Distanzmatrix.allMatrix.get(h_next)[Distanzmatrix.CreatingnumOfCities][a];
    					nexthour+=3600000;
						hour+=1;
						if(hour==24) {
							hour=0;
						}
						if(hour==23) {
							h_next=0;
						}
						else {
							h_next=hour+1;
						}
					
					}
					else {
						
						totalduration+=Distanzmatrix.allMatrix.get(hour)[Distanzmatrix.CreatingnumOfCities][a];	
						IntersectionValue+=Distanzmatrix.allMatrix.get(hour)[Distanzmatrix.CreatingnumOfCities][a];	
						sumMilli+=Distanzmatrix.allMatrix.get(hour)[Distanzmatrix.CreatingnumOfCities][a]*1000;	
						
					}
	    	}
			//Set index for summation of durations of remaining cities
	    	if(EA.lastCityvisited==false) {
				if(this.getCity(1).getType()=="Intersection") {
	    		index=2;
				}
				else if(this.getCity(1).getType()=="City") {
	    		index=1;
				}
	   
	    	//Calculation hour depending duration of remaining cities and back to city we've started from,
	    	//  analogue calculation process
				for (int cityIndex=index; cityIndex < tourSize(); cityIndex++) {
					City fromCity = getCity(cityIndex);
					City destinationCity;	
					if(cityIndex+1 < tourSize()){   
						destinationCity = getCity(cityIndex+1);
					} 
					else{    	 
						destinationCity = Distanzmatrix.startCity;
					}
					int a = Integer.parseInt(fromCity.getId());
					int b = Integer.parseInt(destinationCity.getId());
				
					if(sumMilli+Distanzmatrix.allMatrix.get(hour)[a][b]*1000>nexthour) {
						long houroverlaps=(long)(sumMilli+Distanzmatrix.allMatrix.get(hour)[a][b]*1000-nexthour);						
						double houroverlapsratio= Maths.round(houroverlaps/(Distanzmatrix.allMatrix.get(hour)[a][b]*1000),5);									
						allsymmValue+=(1-houroverlapsratio)*Distanzmatrix.allMatrix.get(hour)[a][b]+(houroverlapsratio)*Distanzmatrix.allMatrix.get(h_next)[a][b];
						totalduration+=(1-houroverlapsratio)*Distanzmatrix.allMatrix.get(hour)[a][b]+(houroverlapsratio)*Distanzmatrix.allMatrix.get(h_next)[a][b];	
						sumMilli+=(1-houroverlapsratio)*Distanzmatrix.allMatrix.get(hour)[a][b]*1000+(houroverlapsratio)*Distanzmatrix.allMatrix.get(h_next)[a][b];
						nexthour+=3600000;
						hour+=1;
						if(hour==24) {
							hour=0;
						}
						if(hour==23) {
							h_next=0;
						}
						else {
							h_next=hour+1;
						}
					}
					else {
						allsymmValue+=Distanzmatrix.allMatrix.get(hour)[a][b];
						totalduration+=Distanzmatrix.allMatrix.get(hour)[a][b];	
						sumMilli+=Distanzmatrix.allMatrix.get(hour)[a][b]*1000;	
					}
				}
	    	}
	    	totalduration=Maths.round(totalduration, 3);
	    	return totalduration;
    	}
    	
    	//Calculation of duration in static environment, just city objects of type "City"
		//No toDriveto or Intersection values
    	else {
    		TimeElement now = new TimeElement();
    		int hour= now.getHour();
        	long nexthour=now.timeAtNextHour;
        	long sumMilli=now.startInMilli;
        	int h_next;
    		if(hour==23) {
				h_next=0;
			}
			else {
				h_next=hour+1;
			}
   		 	for (int cityIndex=0; cityIndex < tourSize(); cityIndex++) { 		
   		 		City fromCity = getCity(cityIndex);
   		 		City destinationCity;	  	
   		 		if(cityIndex+1 < tourSize()){
   		 			destinationCity = getCity(cityIndex+1);
   		 		}
                else{    	 
                    destinationCity = Distanzmatrix.startCity;
                } 
   		 		int a = Integer.parseInt(fromCity.getId());
   		 		int b = Integer.parseInt(destinationCity.getId());
   		 		if(sumMilli+Distanzmatrix.allMatrix.get(hour)[a][b]*1000>nexthour) {
					long houroverlaps=(long)(sumMilli+Distanzmatrix.allMatrix.get(hour)[a][b]*1000-nexthour);					
					double houroverlapsratio= Maths.round(houroverlaps/(Distanzmatrix.allMatrix.get(hour)[a][b]*1000),5);									
					allsymmValue+=(1-houroverlapsratio)*Distanzmatrix.allMatrix.get(hour)[a][b]+(houroverlapsratio)*Distanzmatrix.allMatrix.get(h_next)[a][b];
					totalduration+=(1-houroverlapsratio)*Distanzmatrix.allMatrix.get(hour)[a][b]+(houroverlapsratio)*Distanzmatrix.allMatrix.get(h_next)[a][b];
					sumMilli+=(1-houroverlapsratio)*Distanzmatrix.allMatrix.get(hour)[a][b]*1000+(houroverlapsratio)*Distanzmatrix.allMatrix.get(h_next)[a][b];
					nexthour+=3600000;
					hour+=1;
					if(hour==24) {
						hour=0;
					}
					if(hour==23) {
						h_next=0;
					}
					else {
						h_next=hour+1;
					}
   		 		}
   		 		else {
					allsymmValue+=Distanzmatrix.allMatrix.get(hour)[a][b];
					totalduration+=Distanzmatrix.allMatrix.get(hour)[a][b];	
					sumMilli+=Distanzmatrix.allMatrix.get(hour)[a][b]*1000;	
   		 		}
   		 	}
   		 	totalduration= Maths.round(totalduration,3);
   		 	return totalduration;  	
    	}
    }

    //ToString method
    @Override
    public String toString() {
        String geneString = "|";
        for (int i = 0; i < tourSize(); i++) {
        	if(getCity(i)!=null) {
            geneString += "ID:"+getCity(i).getId()+" "+getCity(i).getLatitude()+" "+getCity(i).getLongitude()+"|";
        	}
        	else {
        		geneString+="  Null  ";
        	}
        }
        return geneString;
    }
   
}