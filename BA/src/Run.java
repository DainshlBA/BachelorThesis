

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
	 ArrayList<String[]> CSVdata= new ArrayList<String[]>();


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
	}
//MAIN METHOD:
	public static void main(String[] args) throws Exception{
	TimeElement el= new TimeElement();
	String nameCSV="./"+el.toString2()+".csv";
	ArrayList<String[]> CSVdata= new ArrayList<String[]>();
	
	//Create new EA class object and create new Simulator class Object Salesman
	//start the preperation process: Matrix request, set Selection, Recombination and Mutation Operators
	//add MyListener to object "Optimierer" , add RouteServiceListener to object "Salesman"
		 PrintWriter pw = new PrintWriter(new File(nameCSV));
		 pw.close();
		
		EA Optimierer= new EA();	
		Simulator Salesman= new Simulator();
		//Optimierer.gui_start();
		Optimierer.Formalitäten();
		
		Salesman.addListener(Optimierer);
		Optimierer.addRouteServiceListener(Salesman);
		
		//Initialize population, do first iteration and save initial duration
		Optimierer.evolvePopulation(true);
		best=EA.pop.getFittest();
		
		double d=best.getDuration();
	
		
		
		//Set up Logger for LogFiles
		//Calculate the first solution by number of iteration (iterations1) or by number of iterations without improved solution (iterations2)
	
     
//		log.writeInfo("time factor with gamma function: "+Maths.GammaFaktoren[0]+" "+Maths.GammaFaktoren[1]+" "+Maths.GammaFaktoren[2]+" "+)......;
		 long now1 = System.currentTimeMillis();
		getParamter();
		String[]parameter= new String[] {gen,crossover, cRate,mutation,mRate,selection, TMsize};
		CSVdata.add(parameter);
		String[] header= new String[] {"ID of Location","total duration","avg. duration","standard deviation","calc. time","best Tour"};
		CSVdata.add(header);
      if(EA.iterations1!=0) {
     		for (int z = 0; z < EA.iterations1; z++) {
          		if(z>1) {
     			lastbest= new Tour(best);
          		}
          		/*for(int a=0; a<EA.pop.populationSize();a++) {
    	    	//	log.writeInfo("Nummer: "+String.valueOf(a)+ " "+EA.pop.getTour(a).toString());
    	    	}
          		*/
     			Optimierer.evolvePopulation(false);
     			best=EA.best;
//     			long last=now1;
//     			now1 = System.currentTimeMillis();
//     			
//     			String[] dataset= new String[] {String.valueOf(z),String.valueOf(Maths.round(EA.best.getDuration(),0)),String.valueOf(Maths.round(EA.pop.getAverageDuration(),0)),String.valueOf(Maths.round(EA.pop.getStandardDeviation(),0)),String.valueOf(now1-last),EA.best.toString()};
//     			writeData(dataset);
     			
     		/*	for(int a=0; a<EA.pop.populationSize();a++) {
    	    	//	log.writeInfo("Nummer: "+String.valueOf(a)+ " "+EA.pop.getTour(a).toString());
    	    	}*/
     			//best=new Tour(EA.pop.getFittest());
          		/*if(z>1) {
     			//log.writeInfo("Iteration: "+String.valueOf(z)+" Best duration: "+String.valueOf(best.getDuration())+"  Lastbest duration: "+String.valueOf(lastbest.getDuration()));
          		//log.writeFinest("Lastbest: "+lastbest.toString());
          		//log.writeFinest("Best: "+best.toString());
          		}*/
     	
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
       		;	
          	 
        	   if(best.getFitness()>lastbest.getFitness()) {
       			   counter=0;	
       			   System.out.println(best.getDuration()+" "+best);
       			   
//       			log.writeInfo("VERBESSERT!! : Iteration: "+String.valueOf(rundenzähler)+" Best duration: "+String.valueOf(best.getDuration())+"  Lastbest duration: "+String.valueOf(lastbest.getDuration()));
//    			 log.writeFinest("Lastbest: "+lastbest.toString());
//           	log.writeFinest("Best: "+best.toString());
             		 
         			 
         		   }
         		   else if (best.getFitness()<lastbest.getFitness()) {////////WAS IST MIT DIESEM VERGLEICH
//         			  log.writeWarning("MISTAKE?!?!?: Iteration: "+String.valueOf(rundenzähler)+" Best duration: "+String.valueOf(best.getDuration())+"  Lastbest duration: "+String.valueOf(lastbest.getDuration()));
//                 		log.writeFinest("Lastbest: "+lastbest.toString());
//                 		log.writeFinest("Best: "+best.toString());
         		   }
         	   else{
         		  counter++;  
         		  if(counter%1000==0) {
         			System.out.println(counter);  
         		  }
         	   }
         	//	 log.writeInfo("GLEICH: Iteration: "+String.valueOf(rundenzähler)+" Best duration: "+String.valueOf(best.getDuration())+"  Lastbest duration: "+String.valueOf(lastbest.getDuration()));
           		//log.writeFinest("Lastbest: "+lastbest.toString());
           		//log.writeFinest("Best: "+best.toString());
           	  if(best.checkforOrderDiffrence(lastbest)) {
//     			  log.writeWarning("change in Order obwohl gleiche duration");
    		   }
         		   
         	   }
         	  
            }
         	  
         	
         	  
           
            while (counter<EA.iterations2); 
         
             
     	}

 	  Optimierer.start();
         
       
        //Start dynamic algorithm process
      
     	
     	//Let Algorithm and Simulation run while runs==true
    	do {
            Optimierer.evolvePopulation(false);
           
            Salesman.checkForEvents();
           if (eventcheck==true) {
      		 String[] dataset= new String[] {"Event",String.valueOf(Maths.round(EA.best.getDuration(),0)),String.valueOf(Maths.round(EA.pop.getAverageDuration(),0)),String.valueOf(Maths.round(EA.pop.getStandardDeviation(),0)),EA.lastEventTime.toString(),EA.best.toString()};
      		 CSVdata.add(dataset)	;
      		 eventcheck=false;
           }
          
		}
    	
       while(runs==true);
   	System.out.println(CSVdata.get(5)[0]);
        // Print final results
    	 try (
          		Writer writer = Files.newBufferedWriter(Paths.get(nameCSV));
          	     CSVWriter csvWriter = new CSVWriter(writer,
          	                    CSVWriter.DEFAULT_SEPARATOR,
          	                    CSVWriter.NO_QUOTE_CHARACTER,
          	                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
          	                    CSVWriter.DEFAULT_LINE_END);
          	        ) 
          	{
          	          for(String[] s: CSVdata) {

          	            csvWriter.writeNext(s);
          	            
          	       
          	        }
          	}
 	
 
        System.out.println(("Initial duration : "+d));
     
     
    
        System.out.println("Solution:");
        System.out.println(EA.pop.getFittest()); 
        System.out.println(EA.pop.getFittest().getDuration());
        TimeElement ende = new TimeElement();
        System.out.print(ende);
       
    }    
}






		