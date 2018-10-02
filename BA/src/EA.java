
import java.util.ArrayList;


//Main algorithm and optimizer based on evolutionary algorithm
//Implements Listener and reacts on time depending events created by the simulator
//Sets properties and parameters, activates the correct operators for the EA
public class EA implements myListener {

//VARIABLES:			
    
	//Properties
	static long dynamicStartinMilli;
	static boolean testinitial=false;
	static boolean readMatrix=false;
	
	//EA parameters
    static int numberofCities;
	static int popSize=20;			
	static int iterations1=100;
	static int iterations2=0;
	static long timeStop=0;
	static double mutationRate =0.7;
	static double crossoverRate =1.0;
	static double selectionPressure=1.2;
	static double generationGap=0.5;
    static int tournamentSize = 3;	
    static double reinsertionRate=0.2;
    
    //Gamma distribution
    
	static double c=1;
	static double theta=0.25;
	static double shiftDistance=0.75;
	
	//Further variables
	static int blockedCities=1;
	static Population pop;
	static Population newOffsprings;  
	static Population initialTourPop;
	static boolean OP_Stop=false;
	static boolean lastCityvisited=false;
	static boolean START=false;

	
	//Selection operators 
	static boolean RWS=true;
	static boolean TMS=false;
	
	
	//API request data
	ArrayList<City> Nodes;
	ArrayList<City> Intersections;
	static double[] durations;
	
	//Eventhandling and duration calculation
	static int GPS_frequency=5;
	static int EventCounter=0;
	static Tour best;
	static Tour lastbest;
	static City lastLocation;
	static AtEvent lastEvent;
	static double toDrivetoCity=0;
	static double toDrivetoIntersection=0;
	static double toDrivetoNode=0;
	static TimeElement lastEventTime;
	static TimeElement start;
	private ArrayList<RouteServiceListener> listenerList= new ArrayList<RouteServiceListener>();
	

	//METHODS:
	
	//Method for setting properties
	//Create city list
	//Create durationmatrix
	public void Formalitäten(){
	   
		System.out.println("Bitte warten.....");
		System.out.println();
		System.out.println();
		
		
		D_Matrix.createAll_Cities();
		numberofCities=D_Matrix.CreatingnumOfCities;
		D_Matrix.createDurationMatrix(readMatrix);
		Maths.createFaktoren();
		
		for(int a=0; a<D_Matrix.matrix.length; a++) {
			for(int b=0; b< D_Matrix.matrix.length;b++) {
				System.out.print(D_Matrix.matrix[a][b]*1000+" ");
			}
			System.out.println();
		}
		
		System.out.println("Einstellung sind getroffen worden!");
	}
	
	
	
// EVOLUTIONARY ALGORITHM AND OPERATORS
	
	
	//Evolve population by using recombination, mutation, selection and replacing operators
	//Initializes first population
	//If true: Initialize first population and generate individuals
	public void evolvePopulation(boolean initilize, boolean selfCreation) {
	    	if(testinitial==false) {  
	    		if(OP_Stop==false) {	
	    	    	if(initilize&&selfCreation==false) {
	    	    		pop = new Population(popSize, true,selfCreation);
	    	    		
	    	    	}	
	    	    	else if(selfCreation) {
	    	    		pop = new Population(1, true,selfCreation);
    	    			initialTourPop= new Population(1, false, false);
    	    			Tour ttt= new Tour(pop.getTour(0));
    	    			initialTourPop.saveTour(0, ttt);
    	    			testinitial=true;
    	    			best=pop.getTour(0);
       	    		}
	    	    	if(testinitial!=true) {
	    	    	//Create new offspring generation according to generation gap
	    	    	newOffsprings= new Population((int)(popSize*generationGap), false,false);

	    	    	// Apply Crossover Operators
	    	    	  for (int z = 0; z < newOffsprings.populationSize(); z++) {  
	    	    		  int c_op=(int)(Math.random()*2)+1;
			    	      switch(c_op) {  
			    	      case 1:{	    	    	       
			    	    	   if(Math.random()<=crossoverRate) {
			    	    		   if ((z+1)<newOffsprings.populationSize()) {							   		
									   Tour parent1 = Selection(pop);							
										Tour parent2 = Selection(pop);       					
										Tour childs[]= Ox2Crossover(parent1,parent2);					
										Tour child1=childs[0];
										Tour child2=childs[1];            
										newOffsprings.saveTour(z, child1);    							
										newOffsprings.saveTour((z+1),child2);    						
										z=z+1;						
								   }        	
									else {																	
									Tour parent1 = Selection(pop);							
									Tour parent2 = Selection(pop);           				   	
									Tour childs[]= Ox2Crossover(parent1,parent2);						
									newOffsprings.saveTour(z, childs[0]);                    				
									} 
								}
								else{
									Tour parent1 = tournamentSelection(pop);
									newOffsprings.saveTour(z, parent1); 
								}
			    	       }
			    	       case 2: {	  
			    	    	   if(Math.random()<=crossoverRate) {
			    	    		   if ((z+1)<newOffsprings.populationSize()) {							   		
									   Tour parent1 = Selection(pop);							
										Tour parent2 = Selection(pop);       					
										Tour childs[]= CycleC(parent1,parent2);					
										Tour child1=childs[0];
										Tour child2=childs[1];            
										newOffsprings.saveTour(z, child1);    							
										newOffsprings.saveTour((z+1),child2);    						
										z=z+1;						
								   }        	
									else {																	
									Tour parent1 = Selection(pop);							
									Tour parent2 = Selection(pop);           				   	
									Tour childs[]= CycleC(parent1,parent2);						
									newOffsprings.saveTour(z, childs[0]);                    				
									} 
								}
								else{
									Tour parent1 = tournamentSelection(pop);
									newOffsprings.saveTour(z, parent1); 
								}
			    	       	}
			    	      }
	    	    	  }
	    	    	  
	    	    	// Apply Mutation Operators  
	    	       for (int i = 0; i < newOffsprings.populationSize(); i++) {
	    	     	 int m_op=(int)(Math.random()*4)+1;
		    	       switch(m_op) {
			    	       case 1:{	    	    	       
			    	    	   if(Math.random()<=mutationRate) {
			    	    		   	DisplacementMutation(newOffsprings.getTour(i));
			    	    	   }
			    	       }
			    	       case 2: {	  
			    	           if(Math.random()<=mutationRate) {
			    	        	   ExchangeMutation(newOffsprings.getTour(i));
			    	           }
  
			    	       }
			    	       case 3:{			    
			    	    	   if(Math.random()<=mutationRate) {
			    	    		   InsertionMutation(newOffsprings.getTour(i));
			    	    	   }	   
			    	       }
			    	       case 4:{
			    	    	   if(Math.random()<=mutationRate) {
			    	    		   InversionMutation(newOffsprings.getTour(i));
			    	    	   }
			    	    	   
			    	       }
		    	       	}
	    	       	}
	    	      
	    	    
	    	    
	    	       //Reinsertion with reinsertion rate and generation gap
	    	       pop.rankPopulation();
	    	       newOffsprings.rankPopulation();
	    	       int toInsert=(int)(pop.populationSize()*reinsertionRate);
	    	       int inserted=0;
	    	       for(int a=newOffsprings.populationSize()-1;a>=0;a--) {
	    	    
	    	    	  if( pop.checkforDuplicates(newOffsprings.getTour(a))==false) {
	    	    		  pop.saveTour((inserted), new Tour(newOffsprings.getTour(a)));
	    	    		  inserted++;
	    	    	  }
	    	    	  
	    	    	  if(inserted==toInsert) {

	    	    		  break;
	    	    	  }
	    	    	
	    	       }	    	
	    	       //Evaluate population & Calculate duration
	    	       pop.rankPopulation();
	    	    } 
	    	    best= pop.getFittest();
	   		}
	    }
	}
	
	// Method for choosing correct selection operator
	public Tour Selection(Population popul){
		Tour parent;
		if(TMS==true){
			parent=tournamentSelection(popul);
		}
		else {
			parent=RWS(popul);
		}
		return parent;
	} 


	
    //Recombination operators
	
	public static Tour[] CycleC(Tour parent1, Tour parent2) {
		
		Tour child1=new Tour();
	 	Tour child2=new Tour();
	 	City city1;
	 	City city2;
	 	Tour[] kids= new Tour[2];											
	 	int cyclecounter=0;													
	 	int position=blockedCities;		
	
	 	int start=blockedCities;													
	 	ArrayList<City> notvisited= new ArrayList<City>();				
	 	for(int bl=0;bl<blockedCities;bl++) {
           	child1.setCity(bl, parent1.getCity(bl));
           	child2.setCity(bl,parent2.getCity(bl));
        }
	 	for(int a=blockedCities; a<parent1.tourSize();a++) {		 					
			 City ci= parent1.getCity(a);
			 notvisited.add(ci);											
		}	
	 	while(notvisited.isEmpty()==false) { 								
	 		if(cyclecounter%2==1) {											
	 			for(int a=blockedCities; a<parent1.tourSize();a++) {		
					if(containsCity(notvisited,parent1.getCity(a))) {			
						start=a;												
						position=start;										
						break;
					}
				
				}
	 			do {	 					 				
	 				city1=parent1.getCity(position);
	 				child2.setCity(position, city1);						
	 				city2=parent2.getCity(position);
	 				child1.setCity(position, city2);						
	 				position=parent1.positionofCity(city2);	 				
	 				 if(containsCity(notvisited,city2)) {	 				  	
	 					int p= positionofCity(notvisited, city2);
	 					notvisited.remove(p);
	 				 }
	 				 if(containsCity(notvisited,city1)) { 						
	 					int p= positionofCity(notvisited, city1);
	 					notvisited.remove(p);
	 				 }
	 			}
	 			while(parent1.getCity(start).getId()!=city2.getId());						
	 			cyclecounter++;												
	 			continue;
			}
	 		
	 		if(cyclecounter==0) {									
	 			do {	
	 			
	 				city1=parent1.getCity(position);				
	 				child1.setCity(position, city1);
	 				city2=parent2.getCity(position);				
	 				child2.setCity(position, city2);
	 				position=parent1.positionofCity(city2);	 			
	 				if(containsCity(notvisited,city2)) {  				
	 					int p= positionofCity(notvisited, city2);
	 					notvisited.remove(p);						
	 				}
	 				if(containsCity(notvisited,city1)) {
	 					int p= positionofCity(notvisited, city1);
	 					notvisited.remove(p);
	 				 }
	 			}
	 			while(parent1.getCity(start).getId()!=city2.getId());						
	 			cyclecounter++;												
	 			continue;			
	 		}	
	 		if(cyclecounter%2==0&&cyclecounter!=0) {						
	 			for(int a=0; a<parent1.tourSize();a++) { 					
	 				if(containsCity(notvisited,parent1.getCity(a))) {			
	 					start=a;											
	 					position=start;			
	 					break;
	 				}
	 			
	 			}
	 			do {	 			
	 				city1=parent1.getCity(position);						
	 				child1.setCity(position, city1);
	 				 city2=parent2.getCity(position);						
	 				 child2.setCity(position, city2);
	 				 position=parent1.positionofCity(city2);				
	 				 if(containsCity(notvisited,city2)) {		  				
	 					int p= positionofCity(notvisited, city2);
	 					notvisited.remove(p);						
	 				 }
	 				 if(containsCity(notvisited,city1)){						
	 					int p= positionofCity(notvisited, city1);
	 					notvisited.remove(p);
	 				 }	
	 			}
	 			while(parent1.getCity(start).getId()!=city2.getId());						
	 			cyclecounter++;											
	 			
	 			continue;
	 		}	 		
	 	}
	 	kids[0]=child1;														
	 	kids[1]=child2;
		return kids;
	 }

    public static Tour[] Ox2Crossover(Tour parent1, Tour parent2) {	
    	
    	Tour child1=new Tour();
    	Tour child2=new Tour();
    	Tour[] kids= new Tour[2];
    	
    	int number1 = (int) (Math.random() *( parent1.tourSize()-blockedCities));	
    	int number2 = (int) (Math.random() * ( parent1.tourSize()-blockedCities));	
    	while(number1==number2)	{											
    		number1 = (int) (Math.random() * (parent1.tourSize()-blockedCities));
    		number2 = (int) (Math.random() * (parent1.tourSize()-blockedCities)); 
    		continue;
    	}
    	int startPos= Math.min(number1, number2)+blockedCities;						
    	int endPos= Math.max(number1, number2)+blockedCities;
    	
    	for(int bl=0;bl<blockedCities;bl++) {
           	child1.setCity(bl, parent1.getCity(bl));
           	child2.setCity(bl,parent2.getCity(bl));
           	
        }
    
    	for(int j=blockedCities;j<parent1.tourSize();j++) {   			
    		if(j >= startPos && j <= endPos) {							
    			City cityP1=parent1.getCity(j);							
    			City cityP2=parent2.getCity(j);
    			child1.setCity(j, cityP2);								
    			child2.setCity(j, cityP1);	
    		
    		
    			}
    	}   
    	
    	for(int k=blockedCities;k<parent1.tourSize();k++) {    
    		if (!child1.containsCity(parent1.getCity(k))) {   		
    			for (int ii = blockedCities; ii < child1.tourSize(); ii++)  {       
                    if (child1.getCity(ii) == null)	{		 			                
                    	City city1 = parent1.getCity(k);
                        child1.setCity(ii, city1);						
                        break;
                    }
                }
            }
    	
    		if (!child2.containsCity(parent2.getCity(k))) {			
                for (int ii = 0; ii < child2.tourSize(); ii++) {                          
                    if (child2.getCity(ii) == null) {      			
                    	City city2 = parent2.getCity(k);
                        child2.setCity(ii, city2);					
                        break;
                    }
                }
    		}
    
    	}
    	kids[0]=child1;											
    	kids[1]=child2;
    	return kids;
    }
    
    
	//Mutation operators
    
    public static void ExchangeMutation(Tour tour) {   	
    	Tour child = new Tour();  
   
    	for(int bl=0;bl<blockedCities;bl++) {
    		child.setCity(bl, tour.getCity(bl));
    	}	
	    int tourPos1 = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities; 			
	    int tourPos2 = (int) ((tour.tourSize() -blockedCities)* Math.random())+blockedCities;
	    while(tourPos1==tourPos2){					
	         tourPos1 = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities;			
	         tourPos2 = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities;    
	    }       
	    City city1 = tour.getCity(tourPos1);								
	    City city2 = tour.getCity(tourPos2);
	    child.setCity(tourPos2, city1);
	    child.setCity(tourPos1, city2); 
	    for(int j=blockedCities;j<tour.tourSize();j++) {   	
	   		if (!child.containsCity(tour.getCity(j))) {						
	              for (int ii = 0; ii < child.tourSize(); ii++) {
	                   if (child.getCity(ii) == null) {						
	                    	City city3 = tour.getCity(j);
	                        child.setCity(ii, city3);							
	                        break;
	                    }
	                }
	            }
	    	}
	    	tour=child;
    	
    }
    

    private static void InversionMutation(Tour tour) {  
    
    		Tour child= new Tour();
    		for(int bl=0;bl<blockedCities;bl++) {
	    		child.setCity(bl, tour.getCity(bl));
   	
	    	}
	    	
	    	int number1 = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities;				//Create two random positions that should be swapped
			int number2 = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities;
	    	while(number1==number2) { 		  							
	    		number1 = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities;
	    		number2 = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities; 
			}   	
	    	int startPos= Math.min(number1, number2);							
			int endPos= Math.max(number1, number2);								
	    	int b= endPos;													
	    	int d= startPos;												
	    	for(int a=startPos; a<b;a++) {									
	    		City city1= tour.getCity(b);
	    		child.setCity(a,city1);											
	    		b=b-1;															
	    	}
	    	
	    	for(int c=endPos;c>d;c--) {											
	    		City city1=tour.getCity(d);										
	    		child.setCity(c,city1);											
	    		d=d+1;															
	    	}		
	    	
	    	for (int i = blockedCities; i < tour.tourSize(); i++) {				
	    		if (!child.containsCity(tour.getCity(i))) {	 				    			 
	    			City city1 =tour.getCity(i);
	    			 child.setCity(i, city1);									 
	    		 }
	    	}  	
	    	tour=child; 


    	
    }
    
    private static void DisplacementMutation(Tour tour) {    
    
    	
	    	Tour child = new Tour();  
	    	
	    	for(int bl=0;bl<blockedCities;bl++) {
	    		child.setCity(bl, tour.getCity(bl));
   	
	    	}
	    	int number1 = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities;	
	    	int number2 = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities;	
	    	while(number1==number2) { 		  								
	    		number1 = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities;
	    		number2 = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities;  
	    	}
	    	int startPos= Math.min(number1, number2);						
			int endPos= Math.max(number1, number2);			
			
			
	    	int insertPos=(int) (Math.random() * (tour.tourSize()-(endPos-startPos)-blockedCities))+blockedCities; 	//create random position for insertion
	    	int zaehler=0;				
	  
	    	for(int i=blockedCities; i<tour.tourSize();i++) { 							
	    		if(i >= startPos && i <= endPos) { 								 
	    			City city1= tour.getCity(i);
	    			
	    			child.setCity(insertPos+zaehler, city1);	
	    			
	    			zaehler+=1;													
	    		}
	    	}
	    	zaehler=0;															
	    	for(int j=blockedCities;j<tour.tourSize();j++) {   	
	    		
	    		if (!child.containsCity(tour.getCity(j))) {						
	                for (int ii = 0; ii < child.tourSize(); ii++) {
	                    if (child.getCity(ii) == null) {						
	                    	City city1 = tour.getCity(j);
	                        child.setCity(ii, city1);							
	                        break;
	                    }
	                }
	            }
	    	}
	    	
	    	tour=child;  
	    
    	
    }
    
    private static void InsertionMutation(Tour tour)    {  	  	
    	Tour child = new Tour();  
    	
    	for(int bl=0;bl<blockedCities;bl++) {
    		child.setCity(bl, tour.getCity(bl));
	
    	}
        
    	int oldPos = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities;	
    	int newPos = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities;  									
    	while(oldPos==newPos) {		
    		oldPos = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities;		   
    		newPos = (int) ((tour.tourSize()-blockedCities) * Math.random())+blockedCities;
        }
    	City citytaken=tour.getCity(oldPos);	
    	child.setCity(newPos, citytaken);
    	for(int j=blockedCities;j<tour.tourSize();j++) {   	
    		
    		if (!child.containsCity(tour.getCity(j))) {						
                for (int ii = 0; ii < child.tourSize(); ii++) {
                    if (child.getCity(ii) == null) {						
                    	City city1 = tour.getCity(j);
                        child.setCity(ii, city1);							
                        break;
                    }
                }
            }
    	}
    	tour=child;	
    
	
}

    
    //Selection operators
    
    private static Tour tournamentSelection(Population pop) {				    
        Population tournament = new Population(tournamentSize, false,false);		 
        for (int i = 0; i < tournamentSize; i++) { 							
            int randomId = (int) (Math.random() * pop.populationSize());	
            tournament.saveTour(i, pop.getTour(randomId));
        }
        Tour fittest = tournament.getFittest();								
        return fittest;
    }
	
    private static Tour RWS(Population pop)								
    {	Tour chosen=new Tour();
    	Tour inter=new Tour();
    	double Totalslotsize=0;
    	double slotsize2=0;
    	for(int i=0;i<pop.populationSize();i++) {							
    		inter=pop.getTour(i);
    		Totalslotsize+=inter.getFitness();								
    	} 	
    	double select=Math.random()*Totalslotsize;							
    	for(int j=0; j<pop.populationSize();j++) {  						
    		inter=pop.getTour(j);
    		slotsize2+=inter.getFitness();									
    		if(select<=slotsize2) {											
    			chosen=inter;
    			break;
    		}
    	}
    	return chosen;
    }
    
//EVENT-HANDLING
    
    //Starts the simulation
    //First Route Request, Tour repair, to-driveto-calculation
    //Sets simulation process to run=true
    //Start dynamic algorithm and process
    public void start() throws Exception {
    	START=true;
    	Route route= new Route();
		lastEventTime= Run.start; 
		blockedCities=2;
		route.WayFromTo(best);
		durations=route.Duration;
		EventCounter++;
		Nodes=route.Nodes_as_City;
		Intersections=route.intersections;
		
		//Adaped first and last duration value with duration approximation from first intersection to second node
		//and penultimate node to last intersection 
		//replace first and last node with first and last city object in "Intersection"
		//Set coordinates of cities of the route by using coordinates of first and last "intersection" 

		double lat_ratio_start=(Nodes.get(1).getLatitude()-Intersections.get(0).getLatitude())/(Nodes.get(1).getLatitude()-Nodes.get(0).getLatitude());	
		double lon_ratio_start=(Nodes.get(1).getLongitude()-Intersections.get(0).getLongitude())/(Nodes.get(1).getLongitude()-Nodes.get(0).getLongitude());
		double avg_ratio_start= (lat_ratio_start+lon_ratio_start)/2; 
		
		if(avg_ratio_start<0) {
			avg_ratio_start=avg_ratio_start*(-1);
		}
		durations[0]=durations[0]*avg_ratio_start;
		
		All_Cities.getCity(0).setCoordinates(Intersections.get(0).getLongitude(), Intersections.get(0).getLatitude());
		D_Matrix.startCity.setCoordinates(Intersections.get(0).getLongitude(), Intersections.get(0).getLatitude());
		Nodes.set(0, Intersections.get(0)); 
		
		double lat_ratio_end=(Nodes.get(Nodes.size()-1).getLatitude()-Intersections.get(Intersections.size()-1).getLatitude())/(Nodes.get(Nodes.size()-1).getLatitude()-Nodes.get(Nodes.size()-2).getLatitude());	
		double lon_ratio_end=(Nodes.get(Nodes.size()-1).getLongitude()-Intersections.get(Intersections.size()-1).getLongitude())/(Nodes.get(Nodes.size()-1).getLongitude()-Nodes.get(Nodes.size()-2).getLongitude());
		double avg_ratio_end= (lat_ratio_end+lon_ratio_end)/2;
		if(avg_ratio_end<0) {
			avg_ratio_end=avg_ratio_end*(-1);
		}
		durations[durations.length-1]=durations[durations.length-1]*avg_ratio_end;
		int pos2 = All_Cities.PositionofCity(best.getCity(1)); 
		
		for(int ff=0; ff<All_Cities.numberOfCities();ff++) {
		
		}
		All_Cities.getCity(pos2).setCoordinates(Intersections.get(Intersections.size()-1).getLongitude(), Intersections.get(Intersections.size()-1).getLatitude());
		Nodes.set(Nodes.size()-1, Intersections.get(Intersections.size()-1));
	
		//Inform simulator
		RouteServiceEvent event= new RouteServiceEvent(this, Nodes,Intersections, durations,best,lastEventTime);
		fireEvent(event);
		
		
		//Repair all tours in actual population and fittest tour "best"
		for ( int t =0; t<pop.populationSize();t++) {
			if(Intersections.get(1).getType()=="Intersection"){
				pop.getTour(t).addatPosition(1,Intersections.get(1));
			}
			else {
				int delete=pop.getTour(t).positionofCity(best.getCity(2));
				pop.getTour(t).deleteCity(delete);
				pop.getTour(t).addatPosition(1, best.getCity(2));
			}
		}
		
		//If the next location will be an intersection, add to All_Cities
		if(Intersections.get(1).getType()=="Intersection"){
			All_Cities.addCity(Intersections.get(1));
			D_Matrix.updateAllMatrix();
		}
		
		//If there is no intersection, calculate duration to next city
		if(Intersections.get(1).getType()=="City") {  
			toDriveto("City",0,Nodes.size()-1,1);
		}
		
		//If there is an intersection, calculate duration to next intersection
		else {
			int PosinNode=0;
			for(int a=0; a<Nodes.size();a++) {
				if(Nodes.get(a).getId()==Intersections.get(1).getId()) {
					break;
				}
				PosinNode++;				
			}
			toDriveto("Intersection",0,PosinNode,1);
		}
		
		//Save actual position and best tour for comparison reasons at the next event
		lastLocation=All_Cities.getCity(0);
		lastbest=new Tour(best);
		
		//Start dynamic algorithm and simulation
		Run.runs=true;
		start= new TimeElement();
		dynamicStartinMilli=System.currentTimeMillis();
		System.out.println();
		System.out.println("Start dynamic simulation");
		System.out.println();
		System.out.println("At Start: Best duration: "+best.getDuration()+" Best: "+best.toString());	
    }
   
  
    
    //Event-handling method for arriving at a "City"
    // Do route request, adapt data, inform simulator, adapt tours and All_Cities
    //do matrix update, do toDriveto calculation
    //Event-handling and event-related methods
    @Override
	public void atCity(AtEvent e){ 
    	
    	toDrivetoNode=0;
		toDrivetoIntersection=0;
		toDrivetoCity=0;
		EventCounter++;
		Route route= new Route();
		lastEvent=e;
		lastEventTime= new TimeElement(e.getEventTime());	
		System.out.println("Arrived at City: "+String.valueOf(e.location.getId()));

		Tour lastRequest=null;
		
		//Turn of dynamic algorithm when we are back at our starting city
		if(e.status=="Erste Stadt wieder erreicht") {
			Run.runs=false;
		}
		if(e.status=="Letzte Stadt erreicht") {
			lastCityvisited=true;
			System.out.println("Letzte Stadt erreicht");
		}
		if(Run.runs==true) {
		//Do the route request and save data
		
			//if we arrive at the last "City"
			if(lastCityvisited) {
				ArrayList <City> abc= new ArrayList<City>();
				//adding best(0) just for referencing purpose in Wayfromto
				abc.add(best.getCity(0));			
				abc.add(best.getCity(1));
				abc.add(D_Matrix.startCity);
				lastRequest= new Tour (abc);
				
				try {
					route.WayFromTo(lastRequest);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			else {
				try {
					route.WayFromTo(best);
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			durations=route.Duration;
			Nodes=route.Nodes_as_City;
			Intersections=route.intersections;
			
			
			//Adaped first and last duration value with duration approximation from first intersection to second node
			//and penultimate node to last intersection 
			//replace first and last node with first and last city object in "Intersection"
			//Set coordinates of destination city of the route by using coordinates of last "intersection" 
			double lat_ratio_start=(Nodes.get(1).getLatitude()-Intersections.get(0).getLatitude())/(Nodes.get(1).getLatitude()-Nodes.get(0).getLatitude());	
			double lon_ratio_start=(Nodes.get(1).getLongitude()-Intersections.get(0).getLongitude())/(Nodes.get(1).getLongitude()-Nodes.get(0).getLongitude());
			double avg_ratio_start= (lat_ratio_start+lon_ratio_start)/2;  
			durations[0]=durations[0]*avg_ratio_start;
			
			double lat_ratio_end=(Nodes.get(Nodes.size()-1).getLatitude()-Intersections.get(Intersections.size()-1).getLatitude())/(Nodes.get(Nodes.size()-1).getLatitude()-Nodes.get(Nodes.size()-2).getLatitude());	
			double lon_ratio_end=(Nodes.get(Nodes.size()-1).getLongitude()-Intersections.get(Intersections.size()-1).getLongitude())/(Nodes.get(Nodes.size()-1).getLongitude()-Nodes.get(Nodes.size()-2).getLongitude());
			double avg_ratio_end= (lat_ratio_end+lon_ratio_end)/2;
			durations[durations.length-1]=durations[durations.length-1]*avg_ratio_end;			
			if(All_Cities.checkForCities()>1){
				int pos = All_Cities.PositionofCity(best.getCity(2));
				All_Cities.getCity(pos).setCoordinates(Intersections.get(Intersections.size()-1).getLongitude(), Intersections.get(Intersections.size()-1).getLatitude());		
			}
			Nodes.set(0, Intersections.get(0)); 
			Nodes.set(Nodes.size()-1, Intersections.get(Intersections.size()-1));


			//Inform simulator
			RouteServiceEvent event= new RouteServiceEvent(this, Nodes,Intersections, durations,best,lastEventTime);
			fireEvent(event);		
			
			
			//Repair all tours in actual population
			for ( int t =0; t<pop.populationSize();t++) {
				//Delete last location
				pop.getTour(t).deleteCity(0);
				//add city object "intersection" if available
				if(Intersections.get(1).getType()=="Intersection"){
					pop.getTour(t).addatPosition(1,Intersections.get(1));
				}
				//if no "intersection" available set next city in best as next destination in all tours
				else {
					if(All_Cities.checkForCities()>1&&lastCityvisited==false) {
					int delete=pop.getTour(t).positionofCity(best.getCity(2));
					pop.getTour(t).deleteCity(delete);
					pop.getTour(t).addatPosition(1, best.getCity(2));
					}
				}
			}

			//delete last location in All_Cities
			All_Cities.deleteCity(lastLocation);
			//Insert next "intersection" if available and do a matrix update for next intersection
			if(Intersections.get(1).getType()=="Intersection"){
				All_Cities.addCity(Intersections.get(1));
				if(e.status==null||All_Cities.checkForCities()>2) {
					try {
						D_Matrix.updateAllMatrix();
					} 
					catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}	
	
			// toDriveto values calculation
			
			//If there are no intersections left on the route or tour cannot change anymore
			if(OP_Stop==true||Intersections.get(1).getType()=="City") { 		
				toDriveto("City",0,Nodes.size()-1,1);
			}	
			
			//If there are intersections left on the route
			else { 
				int PosinNode=0;
				for(int a=0; a<Nodes.size();a++) {
					if(Nodes.get(a).getId()==Intersections.get(1).getId()) {
						break;
					}
					PosinNode++;
				}		
				toDriveto("Intersection",0,PosinNode,1);	
			}
		}
		else {
			for ( int t =0; t<pop.populationSize();t++) {
				//Delete last location
				pop.getTour(t).deleteCity(0);
				pop.getTour(t).addatPosition(0, D_Matrix.startCity);
				}
			All_Cities.deleteCity(lastLocation);
			All_Cities.addCity(D_Matrix.startCity);
			}
			
		
		//Save actual position and best tour for comparison reasons at the next event
		for(int check=0; check <All_Cities.numberOfCities();check++) {
			if(e.location.getId()==All_Cities.getCity(check).getId()){
				lastLocation=All_Cities.getCity(check);
				break;
			}
		} 
		lastbest=new Tour(best);
		System.out.println("Best duration: "+best.getDuration()+" Best: "+best.toString());
		System.out.println();
    }
    
    
    
    
    //Event-handling method for arriving at a "City"
    //Distinguish change or no change in solution since last intersection/city
    //If there is a change: Do route request, adapt data, inform simulator,
    //adapt tours and All_Cities,do matrix request, do toDriveto calculation
    //If there is no change : /adapt tours and All_Cities,do matrix request, do toDriveto calculation
	@Override
	public void atIntersection(AtEvent e) { 
		
		EventCounter++;
		toDrivetoIntersection=0;
		toDrivetoCity=0;
		toDrivetoNode=0;
		lastEvent=e;
		lastEventTime= new TimeElement(e.getEventTime());	
		System.out.println("Arrived at Intersection: "+String.valueOf(e.location.getId()));
		
		//if there is a change in solution since the last event location
		if(All_Cities.checkForCities()>1 && !(lastbest.getCity(2).getId().equals(best.getCity(2).getId()))) { 
		
			Route route= new Route();
			try {
				route.WayFromTo(best);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			durations=route.Duration;
			Nodes=route.Nodes_as_City;
			Intersections=route.intersections;
			
		
			//Adaped first and last duration value with duration approximation from first intersection to second node
			//and penultimate node to last intersection 
			//replace first and last node with first and last city object in "Intersection"
			//Set coordinates of destination city of the route by using coordinates of last "intersection" 
			double lat_ratio_start=(Nodes.get(1).getLatitude()-Intersections.get(0).getLatitude())/(Nodes.get(1).getLatitude()-Nodes.get(0).getLatitude());	
			double lon_ratio_start=(Nodes.get(1).getLongitude()-Intersections.get(0).getLongitude())/(Nodes.get(1).getLongitude()-Nodes.get(0).getLongitude());
			double avg_ratio_start= (lat_ratio_start+lon_ratio_start)/2;  
			durations[0]=durations[0]*avg_ratio_start;
			
			double lat_ratio_end=(Nodes.get(Nodes.size()-1).getLatitude()-Intersections.get(Intersections.size()-1).getLatitude())/(Nodes.get(Nodes.size()-1).getLatitude()-Nodes.get(Nodes.size()-2).getLatitude());	
			double lon_ratio_end=(Nodes.get(Nodes.size()-1).getLongitude()-Intersections.get(Intersections.size()-1).getLongitude())/(Nodes.get(Nodes.size()-1).getLongitude()-Nodes.get(Nodes.size()-2).getLongitude());
			double avg_ratio_end= (lat_ratio_end+lon_ratio_end)/2;
			durations[durations.length-1]=durations[durations.length-1]*avg_ratio_end;
			
			int pos = All_Cities.PositionofCity(best.getCity(2));
			All_Cities.getCity(pos).setCoordinates(Intersections.get(Intersections.size()-1).getLongitude(), Intersections.get(Intersections.size()-1).getLatitude());
			
			Nodes.set(0, Intersections.get(0)); 
			Nodes.set(Nodes.size()-1, Intersections.get(Intersections.size()-1));
			
			//Inform simulator
			RouteServiceEvent event= new RouteServiceEvent(this, Nodes,Intersections, durations,best,lastEventTime);
			fireEvent(event);

			//Adapt all tours in actual population
			for ( int t =0; t<pop.populationSize();t++) {
				//Delete last location
				pop.getTour(t).deleteCity(0);
				if(Intersections.get(1).getType()=="Intersection"){
					//add city object "intersection" if available
					pop.getTour(t).addatPosition(1,Intersections.get(1));
				}
				//add city object "intersection" if available
				else {
					int delete=pop.getTour(t).positionofCity(best.getCity(2));
					pop.getTour(t).deleteCity(delete);
					pop.getTour(t).addatPosition(1, best.getCity(2));
				}
			}
			//Adapt All_Cities
			if(lastLocation.getType()=="GPS"||lastLocation.getType()=="Intersection") {
				All_Cities.deleteCity(lastLocation);
			}
			else {
				for(int a=0;a<All_Cities.numberOfCities();a++) {
					if(All_Cities.getCity(a).getId()==lastLocation.getId()) {
						All_Cities.deleteCity(All_Cities.getCity(a));
						break;
					}
				}
			}
			//Insert next "intersection" if available and do a matrix update for next intersection
			if(best.getCity(1).getType()=="Intersection"){
				All_Cities.addCity(Intersections.get(1));
				try {
					D_Matrix.updateAllMatrix();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
			// toDriveto values calculation
			
			//If there are no intersections left on the route or tour cannot change anymore
			if(Intersections.get(1).getType()=="City") { 
				toDriveto("City",0,Nodes.size()-1,1); 
			}
			//If there are intersections left on the route
			else {
				int PosinNode=0;
				for(int a=0; a<Nodes.size();a++) {
					if(Nodes.get(a).getId()==Intersections.get(1).getId()) {
						break;
					}
					PosinNode++;
				}	
				toDriveto("Intersection",0,PosinNode,1);	
			}

		}
		
		
		//No channge in best solution since last intersection/city
		else{			
			
			//Stop evolve Population because there is no other solution possible anymore
			if(e.status=="Operatoren-Stop") {
				OP_Stop=true;
			}
			//adapt all tours of population
			int PosinInter=-1;
			for(int srch=0; srch<Intersections.size();srch++) {
				if(e.location.id==Intersections.get(srch).getId()) {
					PosinInter=srch;
					break;
				}
			}
	
			for ( int t =0; t<pop.populationSize();t++) {
				//delete last location
				pop.getTour(t).deleteCity(0);
				//add city object "intersection" if available
				if(Intersections.get(PosinInter+1).getType()=="Intersection"){
					pop.getTour(t).addatPosition(1,Intersections.get(PosinInter+1));	
				}
				//add city object "intersection" if available
				else {		
					if(pop.getTour(t)!=pop.getFittest()&&lastCityvisited==false) {
					int delete=pop.getTour(t).positionofCity(best.getCity(2));		
					pop.getTour(t).deleteCity(delete);
					pop.getTour(t).addatPosition(1, best.getCity(2));			
					}
				}		
			}

			//adapt All_Cities
			if(lastLocation.getType()=="GPS"||lastLocation.getType()=="Intersection") {
				All_Cities.deleteCity(lastLocation);
			}
			else {
				for(int a=0;a<All_Cities.numberOfCities();a++) {
					if(All_Cities.getCity(a).getId()==lastLocation.getId()) {
						All_Cities.deleteCity(All_Cities.getCity(a));
						break;
					}
				}
			}
	
			
			//Insert next "intersection" if available and do a matrix update for next intersection
			if(Intersections.get(PosinInter+1).getType()=="Intersection"){
				All_Cities.addCity(Intersections.get(PosinInter+1));
				if(All_Cities.checkForCities()>=2 &&e.location!=Intersections.get(Intersections.size()-2)) {		
					try {
						D_Matrix.updateAllMatrix();
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}	
		
	
			//toDriveto calculation
			
			//If there are no intersections left on the route or tour cannot change anymore
			if(All_Cities.checkForCities()<2||Intersections.get(PosinInter+1).getType()=="City") { 
				int PosinNode=0;
				for(int a=0; a<Nodes.size();a++) {
					if(Nodes.get(a).getId()==e.location.getId()) {
						break;
					}
					PosinNode++;
				}
					toDriveto("City",PosinNode,Nodes.size()-1,1);
				
			}
			//If there are intersections left on the route
			else {
				int PosinNode1=0;
				for(int a=0; a<Nodes.size();a++) {
					if(Nodes.get(a).getId()==e.location.getId()) {
						break;
					}
					PosinNode1++;
				}
				int PosinNode2=0;
				for(int n=0; n<Nodes.size()-1;n++) {
					if(Nodes.get(n).getId()==Intersections.get(PosinInter+1).getId()) {	
						break;
					}
					PosinNode2++;
				}
				toDriveto("Intersection",PosinNode1,PosinNode2,1);
					
				}
			}		
		lastbest=new Tour (best);
		lastLocation=e.location;
		System.out.println("Best duration: "+best.getDuration()+" Best: "+best.toString());
		System.out.println();
	
	}
	
	
	
	//Event-handling method for GPS events
	//Localizes position and allocates the 2 nodes we are in between
	//adapts tours in population and All_Cities
	//calculates duration to next node and then to next intersection/city
	@Override
	public void GPS_Signal(AtEvent e)  {
		System.out.println("Arrived at GPS: "+String.valueOf(e.location.getId()));
		int GPSinNode=0;
		EventCounter++;
		toDrivetoIntersection=0;
		toDrivetoCity=0;
		toDrivetoNode=0;
		lastEvent=e;
		lastEventTime= new TimeElement(e.getEventTime());

		
		//Allocate the nodes we are in between through spanning a rectangle with coordinates and 
		//analyze if we are located in this rectangle
		
		for( int i=0; i<Nodes.size()-1;i++) {	
			double maxLat=0;
			double minLat=0;
			double maxLon=0;
			double minLon=0;
		
			 maxLat= Math.max(Nodes.get(i).getLatitude(),Nodes.get(i+1).getLatitude());
			 minLat= Math.min(Nodes.get(i).getLatitude(),Nodes.get(i+1).getLatitude());
			 maxLon= Math.max(Nodes.get(i).getLongitude(),Nodes.get(i+1).getLongitude());
			 minLon= Math.min(Nodes.get(i).getLongitude(),Nodes.get(i+1).getLongitude());
			
			if(e.getLatitude()<=maxLat&&e.getLatitude()>=minLat&&e.getLongitude()<=maxLon&&e.getLongitude()>=minLon) {
				break;
			}
			GPSinNode++;  
		}
		
		//Adapt tours, delete last location and add actual GPS position
		for ( int t =0; t<pop.populationSize();t++) {
			pop.getTour(t).deleteCity(0);
			pop.getTour(t).addatPosition(0,e.location);
		}
	

		//Delete last location and add actual GPS position in All_Cities
		if(lastLocation.getType()=="GPS"||lastLocation.getType()=="Intersection") {
			All_Cities.deleteCity(lastLocation);
		}
		else {
			for(int a=0;a<All_Cities.numberOfCities();a++) {
				if(All_Cities.getCity(a).getId()==lastLocation.getId()) {
						All_Cities.deleteCity(All_Cities.getCity(a));
						break;
				}
			}
		} 
		All_Cities.addCity(e.location);		
	
		 //Calculate duration to next Node (position in ArrayList=GPSinNode+1)
		double latratio= (Nodes.get(GPSinNode+1).getLatitude()-e.getLatitude())/(Nodes.get(GPSinNode+1).getLatitude()-Nodes.get(GPSinNode).getLatitude());
		double lonratio=(Nodes.get(GPSinNode+1).getLongitude()-e.getLongitude())/(Nodes.get(GPSinNode+1).getLongitude()-Nodes.get(GPSinNode).getLongitude());
		double ratio= (latratio+lonratio)/2;
		toDriveto("Node",GPSinNode,0,ratio);	
		
		//toDriveto calculation
		//case 1
		GPSinNode++;
		if(OP_Stop==false&&All_Cities.checkForIntersection()>0)	{
			int nextIntersection=0;
			for(int l=1; l<Intersections.size();l++) {
				for(int k=GPSinNode;k<Nodes.size();k++) {
					if(Nodes.get(k).getId()==Intersections.get(l).getId()) { 
						nextIntersection=k;  
						break;	
					}			
				}
				if(nextIntersection!=0) {
					break;
				}	
			}
			toDrivetoIntersection+=toDrivetoNode;
			toDriveto("Intersection",GPSinNode,nextIntersection,1);
			
		}
		// case 2,3,4	
		else {	
			toDrivetoCity+=toDrivetoNode;
			toDriveto("City",GPSinNode+1,durations.length,1);					
		}
		
		lastLocation=e.location;
		System.out.println("Best duration: "+best.getDuration()+" Best: "+best.toString());
		System.out.println();

	}
	
    
	//Method to calculate the duration from the acutal position to the next destination
	//next destination could be an intersection or city
	//Considers daytime through hour depending factor multiplication
	//toDriveto is always involved to allocate total distance with Tour.getDuration()
	public static void toDriveto(String Location,int start, int end, double ratio) { //Wenn kein Node, ratio das übergeben wird ist irrelevant
    	
		if(Location=="City") {
    		
    		//Get actual hour, time in Millis at next full hour, Time in Millis right know for summation and add toDrivetoNode value
    		int hour= lastEventTime.getHour();
    		//summation variable for comparison with nextStep to detect an overlapos 
    		long sumDurTF=lastEventTime.startInMilli+(long)toDrivetoNode*1000;
	    	//Time in Millis at next full hour

        	long nextStep=EA.lastEventTime.getMilliatNextStep();
        	int step=EA.lastEventTime.getStep();
	    	
	    	//Check if sum overlapsed an hour and other special cases
    		if(sumDurTF>nextStep) {
    			hour++;
    			nextStep+=Maths.intervall;			
    		}
    		if(hour==24) {
				hour=0;
			}
			
			//Loop from start to the end of durations[] and add all values to toDrivetoCity with correct time factor, If hour is overlapsed, calculate ratio of time in each hour and assign the to the values
			//add 1 hour (Maths.intervall Millis) to nextStep, incrase hour, check if hour matches special cases
	    	for(int a=start; a<end;a++) {
	    		if(sumDurTF+durations[a]*1000*Maths.getFaktor(hour,step)>nextStep) {
	    			long ttnh=nextStep-sumDurTF;
	    			toDrivetoCity+=Maths.round(ttnh/1000,3);
	    			long x =(long)Maths.round((ttnh/Maths.getFaktor(hour,step)),0);
	    			sumDurTF=nextStep;
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
	    				
	    				long y=(long)(durations[a]*1000)-x;
	    				if((int)(y*Maths.getFaktor(hour,step)/Maths.intervall)==0) {
	    					sumDurTF+=y*Maths.getFaktor(hour,step);
	    					toDrivetoCity+=(y/1000)*Maths.getFaktor(hour,step);
	    					finish=true;
	    				}
	    				else {
	    					x+=(long)Maths.round(Maths.intervall/Maths.getFaktor(hour,step), 0);
	    					toDrivetoCity+=3600;
	    					sumDurTF=nextStep;
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
	    		//add full duration hour depending value
	    		else {
	    			
	    			toDrivetoCity+=durations[a]*Maths.getFaktor(hour,step);
	    			sumDurTF+=durations[a]*Maths.getFaktor(hour,step)*1000;
	    			
	    		}

	    	}
	    	toDrivetoCity= Maths.round(toDrivetoCity, 2);
    	}
	
    	//Analog procedure
    	else if(Location=="Intersection") {
    		//Get actual hour, time in Millis at next full hour, Time in Millis right know for summation and add toDrivetoNode value
    		int hour= lastEventTime.getHour();
    		//summation variable for comparison with nextStep to detect an overlapos 
    		long sumDurTF=lastEventTime.startInMilli+(long)toDrivetoNode*1000;
	    	//Time in Millis at next full hour
    		long nextStep=lastEventTime.getMilliatNextHour();
        	int step=EA.lastEventTime.getStep();

	    	
	    	//Check if sum overlapsed an hour and other special cases
    		if(sumDurTF>nextStep) {
    			hour++;
    			nextStep+=Maths.intervall;			
    		}
    		if(hour==24) {
				hour=0;
			}
			
			//Loop from start to the end of durations[] and add all values to toDrivetoCity with correct time factor, If hour is overlapsed, calculate ratio of time in each hour and assign the to the values
			//add 1 hour (Maths.intervall Millis) to nextStep, incrase hour, check if hour matches special cases
	    	for(int a=start; a<end;a++) {
	    		if(sumDurTF+durations[a]*1000*Maths.getFaktor(hour,step)>nextStep) {
	    			long ttnh=nextStep-sumDurTF;
	    			toDrivetoIntersection+=Maths.round(ttnh/1000,3);
	    			long x =(long)Maths.round((ttnh/Maths.getFaktor(hour,step)),0);
	    			sumDurTF=nextStep;
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
	    				
	    				long y=(long)(durations[a]*1000)-x;
	    				if((int)(y*Maths.getFaktor(hour,step)/Maths.intervall)==0) {
	    					sumDurTF+=y*Maths.getFaktor(hour,step);
	    					toDrivetoIntersection+=(y/1000)*Maths.getFaktor(hour,step);
	    					finish=true;
	    				}
	    				else {
	    					x+=(long)Maths.round(Maths.intervall/Maths.getFaktor(hour,step), 0);
	    					toDrivetoIntersection+=3600;
	    					sumDurTF=nextStep;
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
	    		//add full duration hour depending value
	    		else {
	    			
	    			toDrivetoIntersection+=durations[a]*Maths.getFaktor(hour,step);
	    			sumDurTF+=durations[a]*Maths.getFaktor(hour,step)*1000;
	    			
	    		}

	    	}
	    	toDrivetoIntersection= Maths.round(toDrivetoIntersection, 2);
    	}
    	//Analog procedure

     	else if(Location=="Node") {
     		int hour= lastEventTime.getHour();
	    	long nextStep=lastEventTime.getMilliatNextHour();
	    	long sumDurTF=lastEventTime.startInMilli;   	
	    	int step=EA.lastEventTime.getStep();			
			if(sumDurTF+durations[start]*ratio*1000*Maths.getFaktor(hour,step)>nextStep) {
				long ttnh=nextStep-sumDurTF;
    			toDrivetoNode+=Maths.round(ttnh/1000,3);
    			long x =(long)Maths.round((ttnh/Maths.getFaktor(hour,step)),0);
    			sumDurTF=nextStep;
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
    				
    				long y=(long)(durations[start]*ratio*1000)-x;
    				if((int)(y*Maths.getFaktor(hour,step)/Maths.intervall)==0) {
    					sumDurTF+=y*Maths.getFaktor(hour,step);
    					toDrivetoNode+=(y/1000)*Maths.getFaktor(hour,step);
    					finish=true;
    				}
    				else {
    					x+=(long)Maths.round(Maths.intervall/Maths.getFaktor(hour,step), 0);
    					toDrivetoNode+=3600;
    					sumDurTF=nextStep;
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
    			toDrivetoNode+=durations[start]*ratio*Maths.getFaktor(hour,step);	
    		}
			toDrivetoNode=Maths.round(toDrivetoNode, 2);	    	
     	}  	
    }
	//adds listener to listener list
	public void addRouteServiceListener(RouteServiceListener toAdd) {
		listenerList.add(toAdd);
	}
	//activates listener method in simulator class
	public void fireEvent(RouteServiceEvent e)
	{
		listenerList.get(0).EAdidRequest(e);
	}
	
	//Further supporting methods
    public static boolean containsCity(ArrayList<City> list,City city){
        boolean contains=false;
      	for( int c=0;c<list.size();c++) {
      		if(list.get(c)!=null) {
      			if(city.getId()==list.get(c).getId()) {
      				contains=true;
      				break;
      			}
      		}
          }
      	return contains;
      }
    public static int positionofCity(ArrayList<City> list,City city) {    
        int pos=-1;
      	for( int c=0;c<list.size();c++) {
      			if(city.getId()==list.get(c).getId()) {
      				pos=c;
      				break;
      			
      		}
          }
    return pos;
}

}

