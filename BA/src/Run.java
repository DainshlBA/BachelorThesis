

import com.opencsv.CSVWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.*;



// Main class with main method and main loop
//Initializes EA object "Optimierer" and Simulator object "Salesman" and starts the dynamic algorithm process and simulation
public class Run {

//VARIABLES:
	static int count=0;
	static int hourstart=8;
	static boolean eventcheck=false;
	//status of dynamic process
	static boolean runs=false;
	static Tour lastbest;
	static Tour best;
	static String mutation;
	static String crossover;
	static String cRate;
	static String mRate;
	static String selection;
	static String TMsize;
	static String gen;
	static String insR;
	static String genGap;
	static String selPre;
	static String GPSf;
	 ArrayList<String[]> CSVdata= new ArrayList<String[]>();
static TimeElement start;

	public static void getParamter() {
		if(EA.ox2C==true) {crossover="Crossover: ox2";}
		else if(EA.cycC==true) {crossover="Crossover: Cycle";}
		if(EA.disM==true) {mutation="Mutation: Dsplacement";}
		else if(EA.insM==true) {mutation="Mutation: Insertion";}
		else if(EA.invM==true) {mutation="Mutation: Inversion";}
		else if(EA.excM==true) {mutation="Mutation: Exchange";}
		if(EA.TMS==true) {selection="Selection: Tournament"; TMsize="T-Size: "+String.valueOf(EA.tournamentSize);}
		else if(EA.RWS==true) {selection="RouletteWheel-S.";TMsize="T-Size: ---";}
		cRate="C-Rate: "+String.valueOf(EA.crossoverRate);
		mRate="M-Rate: "+String.valueOf(EA.mutationRate);
		gen="Generationen: "+String.valueOf(EA.iterations1);
		insR="Reinsertion-Rate: "+String.valueOf(EA.reinsertionRate);
		genGap="Generation-Gap: "+String.valueOf(EA.generationGap);
		selPre="Selection-Pressure: "+String.valueOf(EA.selectionPressure);
		GPSf="GPS Frequenz: "+String.valueOf(Simulator.GPS_frequency);
	}
//MAIN METHOD:
	public static void main(String[] args) throws Exception{
		 start= new TimeElement();
		 long realstart= System.currentTimeMillis();
		 start.setStartTimetoHour(hourstart);
		 System.out.println(start);
		String nameCSV="./"+"88"+".csv";
		
		//Create new EA class object and create new Simulator class Object Salesman
		//start the preperation process: Matrix request, set Selection, Recombination and Mutation Operators
		//add MyListener to object "Optimierer" , add RouteServiceListener to object "Salesman"
			// PrintWriter pw = new PrintWriter(new File(nameCSV));
			 //pw.close();
			
			EA Optimierer= new EA();	
			Simulator Salesman= new Simulator();
			Optimierer.Formalitäten();
			getParamter();
			 CSVWriter csvWriter = new CSVWriter(new FileWriter(nameCSV,true)); 
		   
		Salesman.addListener(Optimierer);
		Optimierer.addRouteServiceListener(Salesman);
		
		//Initialize population, do first iteration and save initial duration
		Optimierer.evolvePopulation(true);
		EA.pop.rankPopulation();
	
		best=EA.pop.getFittest();
		double d=best.getDuration();

		
		//Set up Logger for LogFiles
		//Calculate the first solution by number of iteration (iterations1) or by number of iterations without improved solution (iterations2)
	
     
		long now1 = System.currentTimeMillis();
		String[]simval= new String[Maths.Faktoren.length];
		for(int xxx=0; xxx<Maths.Faktoren.length;xxx++) {
			simval[xxx]=String.valueOf(Maths.SimulationsFaktoren[xxx]);
		}
		csvWriter.writeNext(simval);
		String[]parameter= new String[] {gen,crossover,cRate,mutation,mRate,selection, TMsize,insR,genGap,selPre,GPSf};
		csvWriter.writeNext(parameter);
		String[] header= new String[] {"Iteration","total duration","relative totalduration","avg. duration","standard deviation","avg.DiffrentCitiesofBest","avg.DiffrentCitiesofAll","calc. time","time"};
		csvWriter.writeNext(header);
		csvWriter.close(); 
		if(EA.iterations1!=0) {
     		for (int z = 0; z < EA.iterations1; z++) {
     			CSVWriter csvWriter2 = new CSVWriter(new FileWriter(nameCSV,true)); 
          		if(z>1) {
          			lastbest= new Tour(best);
          		}
          		if(z%10000==0) {
          			System.out.println(z);
          		}
     			Optimierer.evolvePopulation(false);
     			best=EA.best;
     			
     			long last=now1;
     			now1 = System.currentTimeMillis();   
     			long dif= now1-realstart;
     			TimeElement action= new TimeElement(start.startInMilli+dif);
				if(z==EA.iterations1-1||(z>1&&best.getDuration()<lastbest.getDuration())) {
	    			String[] dataset= new String[] {String.valueOf(z),String.valueOf((int)EA.best.getDuration()),String.valueOf((int)EA.best.getDuration()),String.valueOf((int)EA.pop.getAverageDuration()),String.valueOf((int)EA.pop.getStandardDeviation()),String.valueOf((int)EA.pop.getAvergeDiffrentCitiesofBest()),String.valueOf((int)EA.pop.getAvergeDiffrentCities()),String.valueOf(now1-last), action.toString2()};

    			csvWriter2.writeNext(dataset);
    			csvWriter2.close();
				}
				else if(z%2500==0) {
	    			String[] dataset= new String[] {String.valueOf(z),String.valueOf((int)EA.best.getDuration()),String.valueOf((int)EA.best.getDuration()),String.valueOf((int)EA.pop.getAverageDuration()),String.valueOf((int)EA.pop.getStandardDeviation()),String.valueOf((int)EA.pop.getAvergeDiffrentCitiesofBest()),String.valueOf((int)EA.pop.getAvergeDiffrentCities()),String.valueOf(now1-last), action.toString2()};
					csvWriter2.writeNext(dataset);
	    			csvWriter2.close();
				}
     		}
     	}
		else if(EA.timeStop!=0) {
			TimeElement now= new TimeElement();
			long stop = now.startInMilli+EA.timeStop;
			do {
				Optimierer.evolvePopulation(false);
				best=EA.pop.getFittest();  
			}
    		while(System.currentTimeMillis()<=stop);
		}
    
		else {
			int counter =0;
			int rundenzähler=0;
			do {
				lastbest= new Tour (best);
				Optimierer.evolvePopulation(false);
				best=EA.best;  
				rundenzähler++;
				if(rundenzähler >2) {
					if(best.getDuration()<lastbest.getDuration()) {
						counter=0;	
						long last=now1;
						now1 = System.currentTimeMillis();
						String[] dataset= new String[] {String.valueOf(counter),String.valueOf(Maths.round(EA.best.getDuration(),0)),String.valueOf(Maths.round(EA.best.getRelativeDuration(), 0)),String.valueOf(Maths.round(EA.pop.getAverageDuration(),0)),String.valueOf(Maths.round(EA.pop.getStandardDeviation(),0)),String.valueOf(now1-last),EA.best.toString()};
						csvWriter.writeNext(dataset);
					}		 
					else{
        	   		counter++;  
					}
				} 
			}    	  
			while (counter<EA.iterations2);       
		}
      
      
/*
      
      //Start dynamic algorithm process
 	  Optimierer.start();
 	  
 	  String[] dataset= new String[] {"Start",String.valueOf((int)EA.best.getDuration()),String.valueOf((int)EA.pop.getAverageDuration()),String.valueOf((int)EA.pop.getStandardDeviation()),String.valueOf((int)EA.pop.getAvergeDiffrentCitiesofBest()),String.valueOf((int)EA.pop.getAvergeDiffrentCities()),"-",EA.lastEventTime.toString2()};
 	  csvWriter.writeNext(dataset);
 	  //Let Algorithm and Simulation run while runs==true
 	  do {
 		  Optimierer.evolvePopulation(false);
 		  Salesman.checkForEvents();
          if (eventcheck==true) {
        	  CSVWriter csvWriter3 = new CSVWriter(new FileWriter(nameCSV,true)); 
        	  long last=now1;
   			now1 = System.currentTimeMillis(); 
        	  String[] dataset2= new String[] {(EA.lastEvent.getEventType()+"-"+EA.lastEvent.location.getId()),String.valueOf((int)EA.best.getDuration()),String.valueOf((int)EA.pop.getAverageDuration()),String.valueOf((int)EA.pop.getStandardDeviation()),String.valueOf((int)EA.pop.getAvergeDiffrentCitiesofBest()),String.valueOf((int)EA.pop.getAvergeDiffrentCities()),String.valueOf((int)EA.pop.getAvergeDiffrentCities()),String.valueOf(now1-last),EA.lastEventTime.toString2()};
        	  csvWriter3.writeNext(dataset2);
        	  eventcheck=false;
        	  csvWriter3.close();
           }  
		}
    	
       while(runs==true);
   */
// 	 CSVWriter csvWriter4 = new CSVWriter(new FileWriter(nameCSV,true)); 
 	  
 	  
        // Print final results
	 	
	 	  System.out.println("FINISH!!!");
	      System.out.println(("Initial duration : "+d));   
	      System.out.println();
	      System.out.println("Solution:");
	      System.out.println(EA.pop.getFittest()); 
	      System.out.println(EA.pop.getFittest().getDuration());
	      TimeElement ende = new TimeElement();
	      System.out.print(ende);
//	      String[] SE= new String[]{"START: "+start.toString(),"ENDE: "+ende.toString()};
//	      csvWriter4.writeNext(SE);
//	      csvWriter4.close();
	  
   }    
}






		