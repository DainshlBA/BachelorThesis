import java.util.ArrayList;

/**
 * 
 */

/**
 * @author sanderer
 *
 */
public class Tester {

	public static void main(String[] args) {

		Jaccard_Test Jt = new Jaccard_Test();
		
		ArrayList<City> tour1 = Jt.createTour();
		ArrayList<City> tour2 = Jt.createTour();
		
		Jt.printTour(tour1, "Tour1");
		Jt.printTour(tour2, "Tour2");
		
		ArrayList<QGram> qGramList1 = Jt.findQGrams(tour1);
		ArrayList<QGram> qGramList2 = Jt.findQGrams(tour2);
		
		Jt.printQGramList(qGramList1, "QL1");
		Jt.printQGramList(qGramList2, "QL2");
		
		
		Jt.calculateJaccard(qGramList1, qGramList2);	
		

		
	}	
}
