


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
//CONSTRUCTOR:
    //Constructs blank tour
    public Tour(){
        for (int i = 0; i < All_Cities.numberOfCities(); i++) {
            tour.add(null);
        }
    }
    //Constructs Tour with City objects of ArrayList
    public Tour(ArrayList<City> tour){
        this.tour = tour;
    }
    //Constructs a copy a tour
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
	
    	//Calculation of duration in dynamic environment
    	if(EA.START==true) {
    		if(totalduration==0) {
			//actual hour
    		int hour= EA.lastEventTime.getHour();
			//Time in Millis at next full hour
        	long nextStep=EA.lastEventTime.getMilliatNextStep();
        	int step=EA.lastEventTime.getStep();
        	
			//Sum for comparing if there is an houroverlaps
        	long sumMilli=EA.lastEventTime.startInMilli;
			//value of next full hour
        
			//References the start in the tour for summation of Matrix values
    		int index=1;
			
			//Add toDriveto Value from EA to totalduration, first part of calculation
	    	totalduration =EA.toDrivetoCity+EA.toDrivetoIntersection;
	    
	    	//ABfrage......... fehlt
	    	
	    	if(sumMilli+totalduration*1000>nextStep) { 
	    		step++;
	    		long ttnh=nextStep-sumMilli;
	    		long x =ttnh;
	    		sumMilli=nextStep;
	    		nextStep+=Maths.intervall;
	    		boolean finish=false;
				
				if(step>Faktor.steps) {
					hour++;
					step=1;
				}
				if(hour==24) {
					hour=0;
				}
	    		do {
    				
    				long y=(long)(totalduration*1000)-x;
    				if((int)(y/Maths.intervall)==0) {
    					sumMilli+=y;//+Maths.get
    					finish=true;
    				}
    				else {
    					x+=Maths.intervall;	
    					sumMilli=nextStep;
    					nextStep+=Maths.intervall;	
    	    			
    	    			step++;
    					
    					if(step>Faktor.steps) {
    						step=1;
    						hour++;
    					}
    					if(hour==24) {
    						hour=0;
    					}
    				}
    			}
    			while(finish==false);   	
	    	}	
	    	else {
	    		sumMilli+=totalduration*1000;
	    	}
	  
	    	
	    	// If situation requires a Intersection-value of the "Intersection" matrix range 
	    	//Second part of calculation
	    	if(EA.OP_Stop==false&&this.getCity(1).getType()=="Intersection") {
	    		//Get ID for selecting correct value in intersection matrix range
	    		int a=Integer.parseInt(this.getCity(2).getId());
	    		//General calculation process
				 if(sumMilli+D_Matrix.matrix[D_Matrix.CreatingnumOfCities][a]*Maths.getFaktor(hour,step)*1000>nextStep) {
						long ttnh=nextStep-sumMilli;
		    			totalduration+=ttnh/1000;
		    			long x =(long)(ttnh/Maths.getFaktor(hour,step));
		    			sumMilli=nextStep;
		    			nextStep+=Maths.intervall;	
		    			
		    			step++;
						
						if(step>Faktor.steps) {
    						step=1;
    						hour++;
    					}
						if(hour==24) {
							hour=0;
						}
		    			boolean finish=false;
		    			do {
		    				
		    				long y=(long)(D_Matrix.matrix[D_Matrix.CreatingnumOfCities][a]*1000)-x;
		    				if((int)(y*Maths.getFaktor(hour,step)/Maths.intervall)==0) {
		    					sumMilli+=y*Maths.getFaktor(hour,step);
		    					totalduration+=(y/1000)*Maths.getFaktor(hour,step);
		    					finish=true;
		    				}
		    				else {
		    					x+=(long)(Maths.intervall/Maths.getFaktor(hour,step));
		    					totalduration+=3600;
		    					sumMilli=nextStep;
		    					nextStep+=Maths.intervall;	
		    	    			
		    	    			step++;
		    					
		    					if(step>Faktor.steps) {
		    						hour++;
		    						step=1;
		    					}
		    					if(hour==24) {
		    						hour=0;
		    					}
		    				}
		    			}
		    			while(finish==false);   
					
					}
					else {
						
						totalduration+=D_Matrix.matrix[D_Matrix.CreatingnumOfCities][a]+Maths.getFaktor(hour,step);	
						sumMilli+=D_Matrix.matrix[D_Matrix.CreatingnumOfCities][a]*1000*Maths.getFaktor(hour,step);	
						
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
	   
	    	//Calculation duration of remaining cities and back to city we've started from,
	    	// analogue calculation process
			//third part of calculation
				for (int cityIndex=index; cityIndex < tourSize(); cityIndex++) {
					City fromCity = getCity(cityIndex);
					City destinationCity;	
					if(cityIndex+1 < tourSize()){   
						destinationCity = getCity(cityIndex+1);
					} 
					else{    	 
						destinationCity = D_Matrix.startCity;
					}
					int a = Integer.parseInt(fromCity.getId());
					int b = Integer.parseInt(destinationCity.getId());
				
					if(sumMilli+D_Matrix.matrix[a][b]*Maths.getFaktor(hour,step)*1000>nextStep) {
						long ttnh=nextStep-sumMilli;
						
		    			totalduration+=ttnh/1000;
		    			long x =(long)(ttnh/Maths.getFaktor(hour,step));
		    			sumMilli=nextStep;
		    			nextStep+=Maths.intervall;	
		    			
		    			step++;
						if(step>Faktor.steps) {
							hour++;
							step=1;
    					}
						if(hour==24) {
							hour=0;
						}
						boolean finish=false;
		    			do {
		    				
		    				long y=(long)(D_Matrix.matrix[a][b]*1000)-x;
		    				if((int)(y*Maths.getFaktor(hour,step)/Maths.intervall)==0) {
		    					sumMilli+=y*Maths.getFaktor(hour,step);
		    					totalduration+=(y/1000)*Maths.getFaktor(hour,step);
		    					finish=true;
		    				
		    				}
		    				else {
		    					x+=(long)(Maths.intervall/Maths.getFaktor(hour,step));
		    					totalduration+=3600;
		    					sumMilli=nextStep;
		    					nextStep+=Maths.intervall;	
		    	    			
		    	    			step++;
		    					if(step>Faktor.steps) {
		    						hour++;
		    						step=1;
		    					}
		    					if(hour==24) {
		    						hour=0;
		    					}
		    				
		    				}
		    			}
		    			while(finish==false);   
						
					}
					else {
						totalduration+=D_Matrix.matrix[a][b]*Maths.getFaktor(hour,step);	
						sumMilli+=D_Matrix.matrix[a][b]*1000*Maths.getFaktor(hour,step);	
					}
				}
	    	}
	    	
	    	totalduration=Maths.round(totalduration, 3);
	    	return totalduration;
	    	
    		}
    		else {
    			return totalduration;
    		}
    	
    	}
    	
    	//Calculation of duration in static environment, just city objects of type "City"
		//No toDriveto or Intersection values
    	else {
    		if(totalduration==0) {
    		TimeElement now = Run.start;
    		int hour= now.getHour();
    		int step=now.getStep();
        	long nextStep=now.getMilliatNextStep();
        	long sumMilli=now.startInMilli;
       
   		 	for (int cityIndex=0; cityIndex < tourSize(); cityIndex++) { 		
   		 		City fromCity = getCity(cityIndex);
   		 		City destinationCity;	  	
   		 		if(cityIndex+1 < tourSize()){
   		 			destinationCity = getCity(cityIndex+1);
   		 		}
                else{    	 
                    destinationCity = D_Matrix.startCity;
                } 
   		 		int a = Integer.parseInt(fromCity.getId());
   		 		int b = Integer.parseInt(destinationCity.getId());
   		 		
   		 		if(sumMilli+D_Matrix.matrix[a][b]*Maths.getFaktor(hour,step)*1000>nextStep) {
   		 			long ttnh=nextStep-sumMilli;
	    			totalduration+=ttnh/1000;
	    			long x =(long)(ttnh/Maths.getFaktor(hour,step));
	    			sumMilli=nextStep;
	    			nextStep+=Maths.intervall;	
	    			
	    			step++;
					
					if(step>Faktor.steps) {
						hour++;
						step=1;
					}
					if(hour==24) {
						hour=0;
					}
	    			boolean finish=false;
	    			do {
	    				
	    				long y=(long)(D_Matrix.matrix[a][b]*1000)-x;
	    				if((int)(y*Maths.getFaktor(hour,step)/Maths.intervall)==0) {
	    					sumMilli+=y*Maths.getFaktor(hour,step);
	    					totalduration+=(y/1000)*Maths.getFaktor(hour,step);
	    					finish=true;
	    				}
	    				else {
	    					x+=(long)(Maths.intervall/Maths.getFaktor(hour,step));
	    					totalduration+=3600;
	    					sumMilli=nextStep;
	    					nextStep+=Maths.intervall;	
	    	    			
	    	    			step++;
	    					
	    					if(step>Faktor.steps) {
	    						step=1;
	    						hour++;
	    					}
	    					if(hour==24) {
	    						hour=0;
	    					}
	    				}
	    			}
	    			while(finish==false);   
   		 		}
   		 		else {
					totalduration+=D_Matrix.matrix[a][b]*Maths.getFaktor(hour,step);	
					sumMilli+=D_Matrix.matrix[a][b]*1000*Maths.getFaktor(hour,step);	
   		 		}
   		 	}
   		 	totalduration= Maths.round(totalduration,3);
   		 	return totalduration;  	
    	}
    		else {
    			return totalduration;
    		}
    	}
    }
    
    public double getstaticduration() {
    	totalduration=0;
    	//Values just needed for logger results evaluation
    	if(totalduration==0) {
    		int hour= EA.lastEventTime.getHour();
			//Time in Millis at next full hour
        	long nextStep=EA.lastEventTime.getMilliatNextStep();
        	int step=EA.lastEventTime.getStep();
        	long sumMilli=EA.lastEventTime.startInMilli;
        
   		 	for (int cityIndex=0; cityIndex < tourSize(); cityIndex++) { 		
   		 		City fromCity = getCity(cityIndex);
   		 		City destinationCity;	  	
   		 		if(cityIndex+1 < tourSize()){
   		 			destinationCity = getCity(cityIndex+1);
   		 		}
                else{    	 
                    destinationCity = D_Matrix.startCity;
                } 
   		 		int a = Integer.parseInt(fromCity.getId());
   		 		int b = Integer.parseInt(destinationCity.getId());
   		 		if(sumMilli+D_Matrix.matrix[a][b]*Maths.getFaktor(hour,step)*1000>nextStep) {
   		 			long ttnh=nextStep-sumMilli;
	    			totalduration+=ttnh/1000;
	    			long x =(long)(ttnh/Maths.getFaktor(hour,step));
	    			sumMilli=nextStep;
	    			nextStep+=Maths.intervall;	
	    			
	    			step++;
					
					if(step>Faktor.steps) {
						step=1;
						hour++;
					}
					if(hour==24) {
						hour=0;
					}
	    			boolean finish=false;
	    			do {
	    				
	    				long y=(long)(D_Matrix.matrix[a][b]*1000)-x;
	    				if((int)(y*Maths.getFaktor(hour,step)/Maths.intervall)==0) {
	    					sumMilli+=y*Maths.getFaktor(hour,step);
	    					totalduration+=(y/1000)*Maths.getFaktor(hour,step);
	    					finish=true;
	    				}
	    				else {
	    					x+=(long)(Maths.intervall/Maths.getFaktor(hour,step));
	    					totalduration+=3600;
	    					sumMilli=nextStep;
	    					nextStep+=Maths.intervall;	
	    	    			
	    	    			step++;
	    					if(step>Faktor.steps) {
	    						step=1;
	    						hour++;
	    					}
	    					if(hour==24) {
	    						hour=0;
	    					}
	    				}
	    			}
	    			while(finish==false);   
   		 		}
   		 		else {
					totalduration+=D_Matrix.matrix[a][b]*Maths.getFaktor(hour,step);	
					sumMilli+=D_Matrix.matrix[a][b]*1000*Maths.getFaktor(hour,step);	
   		 		}
   		 	}
   		 	totalduration= Maths.round(totalduration,3);
   		 	return totalduration;  	
    	}
    		else {
    			return totalduration;
    		}
    	
    	
    }
    public double getRelativeDuration() {
    	
    	double durationdiff= ((EA.lastEventTime.startInMilli-Run.start.startInMilli)/1000)+getDuration();
    	return durationdiff;
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
    public String toString2() {
        String geneString = "|";
        for (int i = 0; i < tourSize(); i++) {
        	if(getCity(i)!=null) {
            geneString += getCity(i).getId()+" "+getCity(i).getLatitude()+" "+getCity(i).getLongitude()+" ";
        	}
        	else {
        		geneString+="  Null  ";
        	}
        }
        return geneString;
    }
   
}