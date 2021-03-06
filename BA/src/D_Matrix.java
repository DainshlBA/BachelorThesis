import java.util.ArrayList;
//Class that initiates Matrix-Request and saves and manages all returned duration values 
//No objects required because of static class variables
public class D_Matrix {
	
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
			String s= "C:\\Users\\BADai\\git\\BachelorThesis\\BA\\src\\TTTEST";
			readFile rf= new readFile(s);
			rf.readingFile();
			CreatingnumOfCities=rf.getNumberofCities();		
			zwischenmatrix=rf.getAllCoordinates();
			if(Run.initialtest==false) {
			for(int a=0;a<CreatingnumOfCities;a++) {			
				City neueStaedte = new City(Integer.toString(a),"City",zwischenmatrix[a][1],zwischenmatrix[a][0]);			//Wechsel von Position Latitude -> Longitude
				All_Cities.addCity(neueStaedte);
			}
			startCity=new City(All_Cities.getCity(0));	
			}
	}
	//Creates matrix by reading text file or OSRM Table Service Request
	//Initialize matrix with an extra row and column for upcoming "intersection" values
	//Saving values in double array matrix
	public static void createDurationMatrix(boolean read) {
			matrix= new double[CreatingnumOfCities+1][CreatingnumOfCities+1]; 
			readFile rf= new readFile();
			try {		
				if(read) {
					matrix=rf.readingMatrix("C:\\Users\\BADai\\git\\BachelorThesis\\BA\\src\\Matrix_klein.txt");
				}
				else {
					matrix=Send_Request.createBasicMatrix();
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}

		}

	
	// Table service request (1 x n) for next Intersection in best solution tour
	// Occurs with atCity-Event or atIntersection-Event if their is a change in best solution/Tour 
	// Overwrite old values and add new values to matrix in extra row
	// Add hour value to each of the 24 matrixes
	public static void updateAllMatrix(City Kreuzung) throws Exception {
		double[]IntersectionMatrix=Send_Request.IntersectionMatrix(Kreuzung);
		System.out.println("Intersectionwerte");
		for(int i=0;i<matrix.length-1;i++) {
			matrix[matrix.length-1][i]=IntersectionMatrix[i];
			System.out.print(matrix[matrix.length-1][i]+" ");
		}
		System.out.println();
	
	}
}
