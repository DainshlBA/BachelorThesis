

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
	
//MEHTODS:
	//Returns number of lines of the tsp file which contain values
	public int getNumberofCities(){
		return countlines;
	}
	
	//returns double array with coordiantes
	public double[][] getAllCoordinates(){
		return coordinates;
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
