

import com.opencsv.CSVWriter;
import java.util.ArrayList;
import java.io.*;



// Main class with main method and main loop
//Initializes EA object "Optimierer" and Simulator object "Salesman" and starts the dynamic algorithm process and simulation
public class Run {

//VARIABLES:
	static int count=0;
	static int hourstart=8;
	
	//parameter of dynamic process for csv header
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
	
	//Boolean to enable or disable simulation of initial solution
	static boolean initialtest=false;
	
	
	 ArrayList<String[]> CSVdata= new ArrayList<String[]>();
static TimeElement start;

	//Method for setting headers of .csv files
	public static void getParamter() {
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
		//Timeelement that represents the starting hour of the simulation
		start= new TimeElement();
		start.setStartTimetoHour(hourstart);
		
		//Set file name of all .csv files
		String staticpreRun="./"+"staticpreRun"+".csv";
		String dynamic_RealTour="./"+"dynamic_RealTour"+".csv";
		String dynamic_InitialTour="./"+"dynamic_InitialTour"+".csv";
		String initialDataofRealTourSimulation="./"+"initialDataofDynamic"+".csv";
		
		
		//!!!!
		//BE AWARE: Results only can only be printed if CSVWriter has used .close()
		//In case of interrupting the programm, intermediate results will not be printed
		//!!!!
		
		
		//Create new EA class object and create new Simulator class Object Salesman
		//start the preperation process: Matrix request, set Selection, Recombination and Mutation Operators
		//add MyListener to object "Optimierer" , add RouteServiceListener to object "Salesman"
			
		EA Optimierer= new EA();	
		Simulator Salesman= new Simulator();
		Optimierer.Formalitäten();
		getParamter();
		CSVWriter csvWriter = new CSVWriter(new FileWriter(staticpreRun,true));  
		CSVWriter csvWriter2 = new CSVWriter(new FileWriter(initialDataofRealTourSimulation,true)); 
		Salesman.addListener(Optimierer);
		Optimierer.addRouteServiceListener(Salesman);
		
		//Initialize population, do first iteration and save initial duration
		Optimierer.evolvePopulation(true,initialtest);
		EA.pop.rankPopulation();
		best=EA.pop.getFittest();
	
		
		
		
		//Do static pre-run in case this is not a dynamic initial tour simulation
		if(initialtest==false) {
		
			
		//Write csv files with parameter data and header	
		String[] header1= new String[] {"Iteration/Time","total duration","relative totalduration","avg. duration","average standard deviation"};
		csvWriter.writeNext(header1);
		
		String[] Gfacs;
		String sss="";
		for(int a=0;a<Maths.SimulationsFaktoren.length;a++) {
			sss+=String.valueOf(Maths.SimulationsFaktoren[a])+" ";
		}
		Gfacs= new String[] {sss};
		csvWriter2.writeNext(Gfacs);
		String[]parameter= new String[] {gen,crossover,cRate,mutation,mRate,selection, TMsize,insR,genGap,selPre,GPSf};
		csvWriter2.writeNext(parameter);
		
		System.out.println();
		
		//Solve static TSP and terminate calculation by number of iterations
		if(EA.iterations1!=0) {
			System.out.print("Start static process");
     		for (int z = 0; z < EA.iterations1; z++) {
          		if(z>1) {
          			lastbest= new Tour(EA.pop.getFittest());
          		}
          
     			Optimierer.evolvePopulation(false,false);
     			best=EA.pop.getFittest();
     		
     		
				if(z==EA.iterations1-1) {
	    			String[] dataset= new String[] {String.valueOf(z),String.valueOf((int)EA.best.getDuration()),"---",String.valueOf((int)EA.pop.getAverageDuration()),String.valueOf((int) EA.pop.getStandardDeviation()) };
	    			csvWriter.writeNext(dataset);
				}
				else if(z%100==0) {
	    			String[] dataset= new String[] {String.valueOf(z),String.valueOf((int)EA.best.getDuration()),"---",String.valueOf((int)EA.pop.getAverageDuration()),String.valueOf((int) EA.pop.getStandardDeviation()) };
	    			csvWriter.writeNext(dataset);
	    			
     			}
     		}
		}
		
		
		
		//Solve static TSP and terminate calculation by time limit
		else if(EA.timeStop!=0) {
			System.out.print("Start static process");
			TimeElement now= new TimeElement();
			long millistowait= 10000;
			int factor=1;
			long stop = now.startInMilli+EA.timeStop;
			do {
				Optimierer.evolvePopulation(false,false);
				best=EA.pop.getFittest();  
				if(System.currentTimeMillis()>now.startInMilli+(factor*millistowait)) {
					TimeElement action= new TimeElement(System.currentTimeMillis());
					String[] dataset= new String[] {action.toString2(),String.valueOf((int)EA.best.getDuration()),"---",String.valueOf((int)EA.pop.getAverageDuration()),String.valueOf((int) EA.pop.getStandardDeviation()) };
	    			csvWriter.writeNext(dataset);
	    			factor++;
				}
			}
    		while(System.currentTimeMillis()<=stop);
		}
	
		
		
		//Solve static TSO and terminate calculation if no improvement could be reached within a number of iterations
		else {
			System.out.print("Start static process");
			int counter =0;
			int rundenzähler=0;
			do {
				lastbest= new Tour (best);
				Optimierer.evolvePopulation(false,false);
				best=EA.best;  
				rundenzähler++;
				if(rundenzähler >2) {
					if(best.getDuration()<lastbest.getDuration()) {
						counter=0;	
						String[] dataset= new String[] {String.valueOf(rundenzähler),String.valueOf((int)EA.best.getDuration()),"---",String.valueOf((int)EA.pop.getAverageDuration()),String.valueOf((int) EA.pop.getStandardDeviation()) };
		    			csvWriter.writeNext(dataset);
					}		 
					else{
        	   		counter++;  
					}
				} 
			}    	  
			while (counter<EA.iterations2);       
		}
		
		
			//Write csv file with initial solution
			String[] tour= new String[] {EA.best.toString2()};
			csvWriter2.writeNext(tour);
     
		}
     
		
		csvWriter.close(); 
   		csvWriter2.close();
		//Write header in next file
		String[] header2= new String[] {"Iteration/Time","total duration","relative totalduration","avg. duration","average standard deviation","status"};
		CSVWriter csvWriter3;
		
		if(initialtest==false) {
			csvWriter3 = new CSVWriter(new FileWriter(dynamic_RealTour,true)); 
			csvWriter3.writeNext(header2);
		}
		else {
			csvWriter3 = new CSVWriter(new FileWriter(dynamic_InitialTour,true)); 
			csvWriter3.writeNext(header2);
		}
		
		
		
		
      //Start dynamic simulation
 	  Optimierer.start();
 	  
 	  long checktime=EA.dynamicStartinMilli;
 	  long MilliToWait=300000;
 	  
 	  String[] dataset= new String[] {EA.lastEventTime.toString2(),String.valueOf((int)EA.best.getDuration()),String.valueOf((int)EA.best.getRelativeDuration()),String.valueOf((int)EA.pop.getAverageDuration()),"START"};
 	  csvWriter3.writeNext(dataset);
 	 
 	  
 	
 	  //Let Algorithm and Simulation run while depot has not been reached
 	  do {
 		  Optimierer.evolvePopulation(false,false);
 		  Salesman.checkForEvents();
 		  long now= System.currentTimeMillis();
 		  
 		  
 		  //Simulation of real tour
 		  if(initialtest==false) {
	 		  if(System.currentTimeMillis()>=checktime+MilliToWait) {
	 			  long fakeNow=now-EA.dynamicStartinMilli+Run.start.startInMilli;  
	 			  TimeElement action = new TimeElement(fakeNow);
	 			  checktime=System.currentTimeMillis();
	 			  String[] dataset2= new String[] {"dynamic",String.valueOf((int)EA.best.getDuration()),String.valueOf((int)EA.best.getRelativeDuration()),String.valueOf((int)EA.pop.getAverageDuration()),action.toString2()};
	 			  csvWriter3.writeNext(dataset2);
	 		  }
 		  }
 		  
 		  //Simulation of initial solution
 		  else {
 			 if(System.currentTimeMillis()>=checktime+MilliToWait) {
 				long fakeNow=now-EA.dynamicStartinMilli+Run.start.startInMilli;  
 	        	TimeElement action = new TimeElement(fakeNow);
 	 			checktime=System.currentTimeMillis();
 	 			String[] dataset2= new String[] {"dynamic initial tour",String.valueOf((int)EA.best.getDuration()),String.valueOf((int)EA.best.getRelativeDuration()),String.valueOf((int)EA.pop.getAverageDuration()),action.toString2()};
 	       	  	csvWriter3.writeNext(dataset2);
 			 }
 		  }
		}
    	
       while(runs==true);

 	  
 	
 	 	// Simulation process is completed, inform user
	 
	 	  System.out.println("FINISH!!!");
	      System.out.println();
	      TimeElement ende = new TimeElement();    
	      String[] SE= new String[]{"START: "+start.toString(),"ENDE: "+ende.toString()};
	      csvWriter3.writeNext(SE);;
	   	
	   		csvWriter3.close();
	  
   }    
}






		