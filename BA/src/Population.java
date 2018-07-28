
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
	 
	 //save a whole population individually
	 public void saveAll(Population p) {
		for(int index=0;index<p.populationSize();index++) {
			 tours[index]=p.getTour(index);
		}
		
	 }
	
	 //Sort by totalduration
	 public void rankPopulation() {
		 Tour temp = null;
	     for (int i = 0; i <tours.length - 1; i++) {
	         for (int j = i + 1; j < tours.length; j++) {
	             if (tours[i].getDuration() < tours[j].getDuration()) {
	                 temp = new Tour(tours[j]);
	                 tours[j] = new Tour(tours[i]);
	                 tours[i] = new Tour(temp);
	             }
	         }
	     }
	     for(int a=0;a<tours.length;a++) {
	    	 tours[a].rank=(a+1);
	    	
	     }
	 }
	 //Average duration of population
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
	//Get number of empty tours in population
	 public int checkforNull() {
		 int notNull=0;
		 for(int a=0; a<populationSize();a++) {
			 if(getTour(a)!=null) {
				 notNull++;
			 }
		 }
		 return notNull;
	 }
	 //Check for duplicate Tours in population
	 public boolean checkforDuplicates(Tour tocheck) {
		 boolean duplicate=false;
		for(int t=0; t<tours.length-1;t++) {
			if(tocheck.checkforOrderDiffrence(tours[t])==false) {
				duplicate=true;
				break;
			}
		}
		return duplicate;
	}		
}
