import java.util.ArrayList;
import java.util.Random;

public class Jaccard_Test {

	Random zufall = new Random();
	
	public ArrayList<City> createTour(){
		
		ArrayList<City> tour = new ArrayList<City>();
		double[] position = {0,1};
//		position[0] = 0.0;
//		position[1] = 1.0;
		
		for(int i=0; i<10; i++) {
		
			int j = zufall.nextInt(tour.size()+1);
			
			String id = "" + i;	
			City city = new City(id, "City", position);	
			
			tour.add(j, city);
		}	
				
		return tour;
	}


	public void printTour(ArrayList<City> tour, String title){
		
		System.out.print(title + ": [");
		
		for(int i=0; i<tour.size()-1; i++) {
			System.out.print(tour.get(i).getId()+", ");
		}
		
		System.out.print(tour.get(tour.size()-1).getId() + "]\n");
	}
	
	
	public ArrayList<QGram> findQGrams(ArrayList<City> tour) {
		
		ArrayList<QGram> qGramList = new ArrayList<QGram>();
		
		for (int i=0; i<tour.size()-1; i++) {
			QGram qGram = new QGram(tour.get(i).getId(), tour.get(i+1).getId());
			qGramList.add(qGram);
		}
		
		return qGramList;
	}
	
	public void printQGramList (ArrayList<QGram> qGramList, String title) {
			
		if (qGramList.size()>0) {
			System.out.print(title + ": {");
		
			for(int i=0; i<qGramList.size()-1; i++) {
				System.out.print("[" + qGramList.get(i).getId1() +", ");
				System.out.print(qGramList.get(i).getId2() +"], ");
			}
			
			System.out.print("[" + qGramList.get(qGramList.size()-1).getId1() +", ");
			System.out.print(qGramList.get(qGramList.size()-1).getId2() +"]}\n");
		}
		
		else {
			System.out.println(title + " ist leer!");
		}
	}
	
	public ArrayList<QGram> copyQGramList(ArrayList<QGram> qGramList){
		
		ArrayList<QGram> copyList = new ArrayList<QGram>();
		
		for (int i=0; i<qGramList.size(); i++) {
			copyList.add(qGramList.get(i));
		}
		
		return copyList;
		
	}
	
	
	
	
	
	public double calculateJaccard(ArrayList<QGram> qGramList1, ArrayList<QGram> qGramList2) {
		
		double jaccard = -1;
		ArrayList<QGram> intersection = new ArrayList<QGram>();
		ArrayList<QGram> union = new ArrayList<QGram>();
		
		ArrayList<QGram> qGramList2_copy= new ArrayList<QGram>();
		qGramList2_copy = this.copyQGramList(qGramList2);

		for (int i=0; i<qGramList1.size(); i++) {
			
			union.add(qGramList1.get(i));		
			
			for (int j=0; j<qGramList2_copy.size(); j++) {
				
				if (qGramList1.get(i).getId1().equals(qGramList2_copy.get(j).getId1())
						&& qGramList1.get(i).getId2().equals(qGramList2_copy.get(j).getId2())) {
					
					intersection.add(qGramList1.get(i));
					qGramList2_copy.remove(j);
					break;
				}
			}
		}
		
		for (int i=0; i<qGramList2_copy.size(); i++) {
			union.add(qGramList2_copy.get(i));
		}
		
		
//		this.printQGramList(intersection, "Schnitt");
//		System.out.println("Schnitt: " + intersection.size());
//		
//		this.printQGramList(union, "Vereinigung");
//		System.out.println("Vereinigung: " + union.size());
		
		jaccard = 1.0*intersection.size()/union.size();
		
//		System.out.println("Jaccard: " + jaccard);
		
		return jaccard;
	}	
}