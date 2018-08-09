

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
	public double[][] readingMatrix(String m){
		countlines=0;
		double[][] erg=new double[Distanzmatrix.CreatingnumOfCities][Distanzmatrix.CreatingnumOfCities];
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
		System.out.println(countlines);
		for(int j=0;j<countlines-1;j++){
			for(int k=0;k<countlines-1;k++) {
		String b= s.next();
		System.out.println(b);
		erg[j][k]=Double.parseDouble(b);
	
		}
		}
		return erg;
	}
	
	
	//reads TSP instance and saves coordinates in double array/
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
		String b= s.next();
		String c=s.next();
		coordinates[j][0]=Double.parseDouble(b);
		coordinates[j][1]=Double.parseDouble(c);
		}
		
		s.close();
	}
}
/*
 * 
 */
