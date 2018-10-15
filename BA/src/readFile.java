

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
//class that reads our TSP-Instance file
public class readFile {

//VARIABLES:
	File file;
	BufferedReader br;
	Scanner s;
	String path;
	double[][] coordinates;
	int countlines=0;
	
//CONSTRUCTOR:
	public readFile(String f){
		this.path=f;
		this.file= new File(f);
	}
	public readFile(){
		
	}
//MEHTODS:
	//Returns number of lines of the tsp file which contain values
	public int getNumberofCities(){
		return countlines;
	}
	
	//returns double array with coordiantes
	public double[][] getAllCoordinates(){
		return coordinates;
	}
	//Reads a matrix text file and returns it as 2-dimensional array
	public double[][] readingMatrix(String m){
		countlines=0;
		double[][] erg=new double[D_Matrix.CreatingnumOfCities+1][D_Matrix.CreatingnumOfCities+1];
		try{
			File ff= new File(m);
			br= new BufferedReader(new FileReader(ff));
			s = new Scanner(ff);
		}
		catch(FileNotFoundException e){
			System.out.print("File not found");
		}
	
		String line="";

		try{
			while((line=br.readLine())!=null){
				countlines+=1;
			}
		}
		
		catch(IOException ioex){
			System.out.print("Error reading file");
		}
	
		for(int j=0;j<D_Matrix.CreatingnumOfCities;j++){
			for(int k=0;k<D_Matrix.CreatingnumOfCities;k++) {
				System.out.println(k);
				String b= s.next();
//				if(k==D_Matrix.CreatingnumOfCities-1) {
//					erg[j][k]=0;
//				}
//				else if(j==D_Matrix.CreatingnumOfCities-1) {
//					erg[j][k]=0;
//				}
//				else {
					erg[j][k]=Double.parseDouble(b);
//				}
			}
		}

		s.close();
		return erg;
	}
	
	//Reads the text file with the static initial tour
	public ArrayList<City> readTour(String p) {
		ArrayList<City> tour= new ArrayList<City>();
		try{
			File ff= new File(p);
			br= new BufferedReader(new FileReader(ff));
			s = new Scanner(ff);
			
		}
		catch(FileNotFoundException e){
			System.out.print("File not found");
		}
		String b;
		String c;
		String a;
		while(s.hasNext()) {
			a=s.next();
			b=s.next();
			c=s.next();
			City c1= new City(a,"City",Double.parseDouble(c),Double.parseDouble(b));
			tour.add(c1);
		}

		s.close();
		return tour;
	}
	
	//Reads the simulation factors of the real tour simulation to use them for simulating the static initial tour
//	public Faktor[] readGammaFaktoren(String p) {
//		Faktor []fak ;
//		try{
//			File ff= new File(p);
//			br= new BufferedReader(new FileReader(ff));
//			s = new Scanner(ff);
//			
//		}
//		catch(FileNotFoundException e){
//			System.out.print("File not found");
//		}
//		String line="";
//
//		try{
//			while((line=br.readLine())!=null){
//				countlines+=1;
//			}
//		}
//		catch(IOException ioex){
//			System.out.print("Error reading file");
//		}
//		String b;
//		for(int a=0;a<countlines;a++) {
//			b=s.next();
//		
//		}
//
//		s.close();
//		return fak;
//	}
	
	public Faktor[] readFaktoren(String p) {
	
		try{
			File ff= new File(p);
			br= new BufferedReader(new FileReader(ff));
			s = new Scanner(ff);
			
		}
		catch(FileNotFoundException e){
			System.out.print("File not found");
		}
		String line="";

		try{
			while((line=br.readLine())!=null){
				countlines+=1;
			}
		}
		catch(IOException ioex){
			System.out.print("Error reading file");
		}
		int abc=countlines%24;
		int numberOffactor=countlines-abc;
		int steps=numberOffactor/24;
		Faktor.setSteps(steps);
		String b;
		Faktor []fak= new Faktor[numberOffactor];
		double uhrzeit=0.00;
		for(int a=0;a<numberOffactor;a++) {
			uhrzeit+=1/steps;
			b=s.next();
			Faktor f= new Faktor(uhrzeit,Double.parseDouble(b));
			fak[a]=f;
		}

		s.close();
		return fak;
	}
	
	//reads TSP instance and saves coordinates in double array
	public void readingFile(){
		try{
			br= new BufferedReader(new FileReader(file));
			s = new Scanner(file);
		}
		catch(FileNotFoundException e){
			System.out.print("File not found");
		}
	
		String line="";

		try{
			while((line=br.readLine())!=null){
				countlines+=1;
			}
		}
		catch(IOException ioex){
			System.out.print("Error reading file");
		}
		
		coordinates= new double[countlines][2];
		
		for(int j=0;j<countlines;j++){
			System.out.println("countlines: "+j);
		String b= s.next();
		String c=s.next();
		coordinates[j][0]=Double.parseDouble(b);
		System.out.print(" "+coordinates[j][0]);
		coordinates[j][1]=Double.parseDouble(c);
		System.out.print(" "+coordinates[j][1]);
		}
		
		s.close();
	}
}
/*
 * 
 */
