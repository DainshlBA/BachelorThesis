
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
//class that holds all of out individuals/tours
public class Population {

	//VARIABLES:
		//Array that holds all of our tours
	 Tour[] tours;														
	 
	//CONSTRUCTOR:
	 // Initializes Tour Array
	 // Initialize==true : Initialize a new population and randomly generate individuals
	 public Population(int populationSize, boolean initialize) {
	     tours = new Tour[populationSize];         
	     if (initialize) {											
	     	for (int i = 0; i < populationSize; i++) {				  
	             Tour newTour = new Tour();
	             newTour.generateIndividual();
	             saveTour(i, newTour);									     
	         }
	     }
	 }
	 
	//METHODS:
	 //Save a tour at certain position in tours
	 public void saveTour(int index, Tour tour) {
	     tours[index] = tour;
	 }
	 
	 public void saveAll(Population p) {
		for(int index=0;index<p.populationSize();index++) {
			 tours[index]=p.getTour(index);
		}
		
	 }
	
	 public double getAverageDuration(){
		 double sum=0;
		 for(int a=0; a<populationSize();a++) {
			 sum+=getTour(a).getDuration();
		 }
		 sum=sum/populationSize();
		 return sum;
	 }
	 //Standard deviation of average duration
	 public double getStandardDeviation() {
		double standDev=0;
		double avgD=getAverageDuration();
		double zaehler=0;
		for(int a=0;a<populationSize();a++) {
			zaehler+=Math.pow((avgD-getTour(a).getDuration()), 2);
		}
		standDev=Math.pow((zaehler/populationSize()), 0.5);
		return standDev;
		
		 
		 
		 
	 }
	 
	 public void deleteTour(Tour t) {
		 int i=Arrays.asList(tours).indexOf(t);
		
		 tours[i]=null;
		 }
	 public Tour getTour(int index) {
	     return tours[index];
	 }
	 
	 //Returns the fittest and best individual 
	 public Tour getFittest() {
	     Tour fittest=null;
		 for(int nn=0; nn<tours.length;nn++) {
			 if(tours[nn]!=null) {
			 fittest = tours[nn];
			 break;
			 }
		 }
	     for (int i = 1; i < populationSize(); i++) {
	    	 if(getTour(i)!=null) {
	    		 if (fittest.getFitness() <= getTour(i).getFitness()) {
	             fittest = getTour(i);
	    		 }
	         }
	     }
	     return fittest;
	 }
	
	 public int populationSize() {
	 	return tours.length;
	 }
	
	 public int checkforNull() {
		 int notNull=0;
		 for(int a=0; a<populationSize();a++) {
			 if(getTour(a)!=null) {
				 notNull++;
			 }
		 }
		 return notNull;
	 }
	 public void eliminateDuplicates() {
		for(int t=0; t<tours.length-1;t++) {
			
			Tour check= tours[t];
			if(tours[t]!=null) {
				
				for(int tt=t+1; tt<tours.length;tt++) {
					
					if(tours[tt]!=null) {
						if(check.checkforOrderDiffrence(tours[tt])==false) {
							
							deleteTour(tours[tt]);
							
						}
					}
				}
			}	
		}
	 }
}
