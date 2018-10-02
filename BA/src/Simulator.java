

import java.util.ArrayList;

//Simulator that for event-based and time-depending simulation
//Using Gamma-function for simulating new values
//checks after every iteration of the EA if it has to fire an event
//Implements RouteServiceListener with which it receives simulation command and API data
//Creates three different types of events, based on API data -> "AtCity", "AtIntersection" & "GPS"
public class Simulator implements RouteServiceListener {
	
//VARIABLES:
	//List for upcoming Events
	public static ArrayList<AtEvent> upcomingEvents = new ArrayList<AtEvent>();
	//List for past Events
	private ArrayList<AtEvent> pastEvents = new ArrayList<AtEvent>();
	//ListenerList
	private ArrayList<myListener> listenerList= new ArrayList<myListener>();
	//Simulation parameters and data
	ArrayList<City> Nodes;
	ArrayList<City> Intersection;
	Tour best;
	double[] duration;
	double[] GammaDuration;
	static int GPS_frequency;
	double k;
	double theta;
	double shiftDistance;
	TimeElement now= new TimeElement();


//CONSTRUCTOR
	public Simulator() {
		this.k=EA.c;
		this.theta=EA.theta;
		this.shiftDistance=EA.shiftDistance;
		GPS_frequency=EA.GPS_frequency;
		
	}
	
//METHODS:
	public void addListener(myListener toAdd) {
		listenerList.add(toAdd);
	}
	
	//Methods that compares actual time with event time of all events
	//Fires event in case of time overlaps and moves specific event in the event lists
	public void checkForEvents(){
		AtEvent currentEvent = null;
		boolean timeOverlaps=false;
		long now= System.currentTimeMillis();
		long MillisSinceStart= now-EA.dynamicStartinMilli;
		
		for(int i=0; i<upcomingEvents.size();i++){
			if((Run.start.startInMilli+MillisSinceStart)>( upcomingEvents.get(i)).getEventTime()){
				pastEvents.add(upcomingEvents.get(i));
				currentEvent=upcomingEvents.get(i);
				timeOverlaps=true;
				break;
			}
			
		}
		if(timeOverlaps==true) {
			upcomingEvents.remove(currentEvent);
			try {
				fireAtEvent(currentEvent);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}

	//Fires event and activates the correct event handling mehtods in EA
	public void fireAtEvent(AtEvent e) throws Exception{
		
		if(e.getEventType().equals("City")){
			listenerList.get(0).atCity(e);
		}
		
		if(e.getEventType()=="Intersection"){
			listenerList.get(0).atIntersection(e);
			
		}	
		if(e.getEventType()=="GPS"){
			listenerList.get(0).GPS_Signal(e);
		}
	}
	
	//Event handling methods for RouteServicEvent
	//Starts creation of AtEvents
	public void EAdidRequest(RouteServiceEvent e) {
		Nodes=e.Nodes;
		Intersection= e.Intersection;
		duration=e.durations;
		best=e.best;
		upcomingEvents = new ArrayList<AtEvent>();
		pastEvents = new ArrayList<AtEvent>();
		now=e.eTime;
		
		createEvents();
	}
	
	//Creates all atEvents of the current route, "GPS", "AtIntersection" & "atCity
	public void createEvents() {
																											
		//Calculation of duration values for simulation
		//Multiply each value of duration array with simulation time factors
		//Compare actual sum of values to time and consider hour overlap
		
		GammaDuration= new double[duration.length];
		int h_next=0;			
		int hour= now.getHour();	
		long nextStep=now.getMilliatNextStep();
    	int step=now.getStep();
	
    	long sumMilli=now.startInMilli;
											
		for(int j=0; j<duration.length;j++) {														
				if(sumMilli+duration[j]*Maths.getGammaFaktor(hour,step)*1000>nextStep) {			
					long ttns=nextStep-sumMilli;
					GammaDuration[j]=ttns/1000;
					long x =(long)(ttns/Maths.getGammaFaktor(hour,step));
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
	    				
	    				long y=(long)(duration[j]*1000)-x;
	    				if((int)(y*Maths.getGammaFaktor(hour,step)/Maths.intervall)==0) {
	    					sumMilli+=y*Maths.getGammaFaktor(hour,step);
	    					GammaDuration[j]+=y/1000*Maths.getFaktor(hour, step);
	    					
	    					finish=true;
	    				
	    				}
	    				else {
	    					x+=(long)(Maths.intervall/Maths.getGammaFaktor(hour,step));
	    					GammaDuration[j]+=3600;
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
					
					GammaDuration[j]=duration[j]*Maths.getGammaFaktor(hour,step);
							
				}
				
			}
		double durationSumZFEA=0;
		for(int a=0 ;a<GammaDuration.length;a++) {
			durationSumZFEA+=GammaDuration[a];
		}
		System.out.println(durationSumZFEA);
		//Create GPS Event every 5 Seconds
		//Get coordinates through approximation: Localize the two nodes you're in between through comparison of sum of event time with sum of duration values
	
		int numberofGPSEvents= (int)((durationSumZFEA)/GPS_frequency)+1;
		double eventTimeSum=0;
		for(int events=0; events<numberofGPSEvents-1;events++) { //ACHTUNG, -1, keine Begründung bis jetzt
			eventTimeSum+=GPS_frequency;
			double diffrence=0;
			double sum_d=0;
			double ratio=0;
			int positionInDurArray=0;
			for(int findNode=1;findNode<GammaDuration.length;findNode++) {
				sum_d+=GammaDuration[findNode-1]; 
				if(sum_d>eventTimeSum) {
					positionInDurArray=findNode-1;
					diffrence=sum_d-eventTimeSum;
					ratio= 1-(diffrence/GammaDuration[findNode-1]);
					
					break;
				}
			}
			
			//Calculate coordinates with ratio factor, create the events and add to upcoming event list
			double lat1= Nodes.get(positionInDurArray).getLatitude();
			double lon1=Nodes.get(positionInDurArray).getLongitude();
			double lat2=Nodes.get(positionInDurArray+1).getLatitude();
			double lon2=Nodes.get(positionInDurArray+1).getLongitude();
			
			double newlat= Maths.round((lat2-lat1)*ratio+lat1, 7);
			double newlon=Maths.round((lon2-lon1)*ratio+lon1, 7);
			City GPS = new City("G"+Integer.toString(events),"GPS",newlon,newlat);
			AtEvent ev= new AtEvent(this,GPS,now.startInMilli+(long)(eventTimeSum*1000));
			upcomingEvents.add(ev);
			
		}
		//Create an atCity event for last city object in ArrayList Intersections
		//Create an atIntersection event for each city object in ArrayList Intersections with the type="Intersection" 
		//Event time is the sum of duration values to the corresponding node
		for(int inters=1; inters<Intersection.size();inters++) { 
			double sumIntD=0;
			if(inters==Intersection.size()-1) {
				for(int dur=0;dur<GammaDuration.length;dur++) {
					sumIntD+=GammaDuration[dur];
				}
				AtEvent ev= new AtEvent(this,Intersection.get(inters) ,(long)(now.startInMilli+(sumIntD*1000)));
				//add status if we reached the start city
				if(All_Cities.checkForCities()==1) {
					ev.status="Erste Stadt wieder erreicht";
				}
				//add status if we reached last city
				else if(All_Cities.checkForCities()==2) {
					ev.status="Letzte Stadt erreicht";
				}
			
				upcomingEvents.add(ev);
				break;
			}
			//Create Intersection events
			else {
				for(int node=1;node<Nodes.size();node++) {
					sumIntD+=GammaDuration[node-1];   
					
					if(Intersection.get(inters).getId()==Nodes.get(node).getId()) { 
						
							AtEvent ev= new AtEvent(this,Intersection.get(inters),(long)(now.startInMilli+(sumIntD*1000)));
							//add status if we reached last Intersection of route to penultimate city
							
							if(All_Cities.checkForCities()==3 &&Intersection.get(inters+1).getType()=="City") {
								ev.status="Operatoren-Stop";
							}
						
							addEventinList(ev);
							break;	
					}	
				}
			}	
		}
	}
	
	//add Events chronologically to upcoming event list
	public void addEventinList(AtEvent e) {
		if(upcomingEvents.isEmpty()) {
			upcomingEvents.add(e);
		}
		else {
			for (int i = 0; i < upcomingEvents.size(); i++){
				if(upcomingEvents.get(i).getEventTime() > e.getEventTime()){
					upcomingEvents.add(i,e);
					break;
				}
				else if(i==upcomingEvents.size()-1) {
					upcomingEvents.add(e);
					break;
				}

			}
		}
	}
}
	