

import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
//Class that initiates Matrix-Request and saves and manages all returned duration values 
//No objects required because of static class variables
public class Distanzmatrix {
	
//VARIABLES:
	//Matrix as double array for all values without time influence
	static double[][] matrix;
	
	//Number of cities at the beginning
	static int CreatingnumOfCities;
	
	//City we start from
	static City startCity;
	
	//ArrayList containing all 24 double array matrixes with specific hour value
	static ArrayList<double[][]> allMatrix= new ArrayList<double[][]>();	

//METHODS	
	// read TSP Instance from file with readFile object, create a city object for every location
	// and add to All_Cities class
	public static void createAll_Cities (){		
			double[][] zwischenmatrix;
			String s= "C:\\Users\\BADai\\git\\BachelorThesis\\BA\\src\\testing";
			readFile rf= new readFile(s);
			rf.readingFile();
			CreatingnumOfCities=rf.getNumberofCities();		
			zwischenmatrix=rf.getAllCoordinates();
			for(int a=0;a<CreatingnumOfCities;a++) {			
				City neueStaedte = new City(Integer.toString(a),"City",zwischenmatrix[a][1],zwischenmatrix[a][0]);			//Wechsel von Position Latitude -> Longitude
				All_Cities.addCity(neueStaedte);
			}
			startCity=new City(All_Cities.getCity(0));	
	}
	
	//First table service request with Send_Request class
	//Initialize matrix with an extra row and column for upcoming "intersection" values
	//Saving values in double array matrix
	public static void createDurationMatrix() {
			matrix= new double[CreatingnumOfCities+1][CreatingnumOfCities+1]; 
			try {		
		        //matrix=Maths.getBeispielMatrix();
		       matrix=Send_Request.createsmallMatrix();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	// create all 24 matrixes by multiplying each value with its hour factor
	// add each double array matrix to ArrayList allMatrix at position of corresponding hour
	public static void createAllMatrixes(){
		for(int i=0; i<24;i++) {
			double faktor= Maths.getFaktor(i);
			double newMatrix[][] = new double[matrix.length][matrix.length];
			for(int j=0;j<matrix.length-1;j++) {				//Matrix wird mit +1 Stellen extra erzeugt für Zwischenwerte
				for(int k=0; k<matrix.length-1;k++) {
					newMatrix[j][k]=matrix[j][k]*faktor;
				}
			}
			allMatrix.add(newMatrix);
		}
		allMatrix.add(matrix);
	}
	
	// Asymmetric table service request for next Intersection in best solution/Tour
	// Occurs with atCity-Event or atIntersection-Event if their is a change in best solution/Tour 
	// Overwrite old values and add new values to matrix in extra row
	// Add hour value to each of the 24 matrixes
	public static void updateAllMatrix() throws Exception {
		double faktor;
		double[]IntersectionMatrix=Send_Request.IntersectionMatrix(EA.best.getCity(1));
//		for(int s=0; s<IntersectionMatrix.length;s++) {
//			System.out.print(" "+IntersectionMatrix[s]);
//		}
		System.out.println( );
		for(int i=0;i<matrix.length-1;i++) {
			matrix[matrix.length-1][i]=IntersectionMatrix[i];
		}
		for(int i=0; i<24;i++) {
			faktor= Maths.getFaktor(i);
			for(int j=0;j<matrix.length-1;j++) {		
					allMatrix.get(i)[matrix.length-1][j]=IntersectionMatrix[j]*faktor;
			}		
		}
	
	}

}