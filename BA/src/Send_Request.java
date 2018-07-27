

import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
//Class for managing and sending requests, managing and preparing the data for Evolutionary Algorithm
public class Send_Request {
	
																				//2-dim. array for saving table service response
																			//JSON Object for saving routing service response
	static int anfragencounter=0;																// request counter

		
	public static  StringBuffer gogo(String gesamt) throws Exception{									//sends URL and gets response in Stringbuffer
		URL obj = new URL(gesamt);
	
	    HttpURLConnection con = (HttpURLConnection) obj.openConnection();						//open a http connection
	    con.setRequestMethod("GET");															
	  
	  
	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));	//Create BufferedReader with an InputstreamReader to read InputStream of URL Connection
	    String inputLine;
	    StringBuffer response = new StringBuffer();
	    while ((inputLine = in.readLine()) != null) {											//while BufferedReader still has a line to read, append line to StringBuffer
	     	response.append(inputLine);
	    }
	    in.close();																				//Close BufferedReader
	    anfragencounter++;																		//Increase request counter
	    return response;
	}
	
	public static JSONObject createRouteRequest(Tour fittest) throws Exception{	
		JSONObject Way;		//Creates String with URL, applies gogo and saves response in an JSONObejct
		String gesamt= "https://w-bdlab-s1.hs-karlsruhe.de/osrm/route/v1/driving/";						//Fixed URL Start
		City From;
		City To;
		if(Run.runs==true) {
		From=fittest.getCity(1);	//??? Korrekt wenn Tour angepasst bei ankunft: C->C
		To=fittest.getCity(2);
		}
		else{
			From=fittest.getCity(0);
			To=fittest.getCity(1);
		}
		double x1=From.getLongitude();															//Longitude of departing city
		double y1=From.getLatitude();															//Latitude of departing city
		double x2=To.getLongitude();															//Longitude of destination city
		double y2= To.getLatitude();															//Latitude of destination city
		gesamt+=Double.toString(x1)+","+Double.toString(y1)+";"+Double.toString(x2)+","+Double.toString(y2)+"?steps=true&annotations=true"; //Add coordinates to url string
		//System.out.println(gesamt);
		StringBuffer response = gogo(gesamt);													//Open HTTP Connection and send URL
		Way= new JSONObject(response.toString());												//Save response in JSONObject
		return Way;
	}
	
	//Muss noch getestet werden
	public static ArrayList<City> getNodes(String[]nodes) throws Exception{		//Muss wahrscheinlich String übergeben werden da Zahl zu groß								//Gets geo-coordinates for all received OSM nodes
		ArrayList<City> Nodes= new ArrayList<City>();																//Contains all nodes that has to be converted
		
		
		for(int i=0;i<nodes.length;i++){												
			String url="https://w-bdlab-s1.hs-karlsruhe.de/osm/api/0.6/node/";
			url+=nodes[i];
			  try
		        {
		            // Turn the string into a URL object
		            URL urlObject = new URL(url);
		            // Open the stream (which returns an InputStream):
		            InputStream in = urlObject.openStream();

		            /** Now parse the data (the stream) that we received back ***/

		            // Create an XML reader
		            @SuppressWarnings("deprecation")
					XMLReader xr = XMLReaderFactory.createXMLReader();

		            // Tell that XML reader to use our special Google Handler
		            OSMHandler ourSpecialHandler = new OSMHandler();
		            xr.setContentHandler(ourSpecialHandler);

		            // We have an InputStream, but let's just wrap it in
		            // an InputSource (the SAX parser likes it that way)
		            InputSource inSource = new InputSource(in);

		            // And parse it!
		            xr.parse(inSource);
		            Nodes.add(ourSpecialHandler.getNode());

		        }
		        catch(IOException ioe)
		        {
		            ioe.printStackTrace();
		        }
		        catch(SAXException se)
		        {
		            se.printStackTrace();
		        }
		}
		return Nodes;
	}
	
	public static double[] IntersectionMatrix(City Intersection) throws Exception{								//Gets all distances from upcoming WP Node to all cities
		int numOfCities=All_Cities.checkForCities();
		
		double[] erg=new double[Distanzmatrix.CreatingnumOfCities];
		int numberOfCases;
		if(numOfCities%99==0){
		 numberOfCases= numOfCities/99;	
		}
		else{
			numberOfCases=(numOfCities/99)+1;
		}
		for(int asym=1;asym<=numberOfCases;asym++){
			if(numOfCities-(asym*99)<0){      //wenn splitted Asymanfrage 
				String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
				String zwischenerg="";
				double x = Intersection.getLongitude();			//Intersection an erster Stelle in URL
			 	double y = Intersection.getLatitude();
			 	zwischenerg += Double.toString(x);
				zwischenerg+=",";
				zwischenerg+=Double.toString(y);
				zwischenerg+=";";
				for(int position=((asym-1)*99);position<numOfCities;position++){
					City intermediate = All_Cities.getCity(position);				//Klappere All_Cities ab
				 	double x1 = intermediate.getLongitude();
				 	double y1=intermediate.getLatitude();
					zwischenerg += Double.toString(x1);
					zwischenerg+=",";
					zwischenerg+=Double.toString(y1);
					if(position==(numOfCities-1))    //-1
					{}
					else{
					zwischenerg+=";";
					}
				}
				 String gesamt=urlAnfang+zwischenerg+"?sources=0";
				// System.out.println(gesamt);
				 StringBuffer response = gogo(gesamt); 
			     JSONObject jobj= new JSONObject(response.toString());
			     JSONArray dura_1 = jobj.getJSONArray("durations");
			     int z=1;//Überspringe 0 wert am anfang;
			    
			     JSONArray dura_2=dura_1.getJSONArray(0);
			     for (int positionzeile=((asym-1)*99);positionzeile<numOfCities;positionzeile++){//numofCities könnte falsch sein
			    	 int toCityID=Integer.parseInt(All_Cities.getCity(positionzeile).getId());	
			    	
			    	 erg[toCityID] = dura_2.getDouble(z);
			    	 z++;				    	    	
			     }			   	    				    	
			    	z=1;
			}	
			
			if(numOfCities-(asym*99)>=0){	//wenn volle 1x99 Anfrage
				String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
				String zwischenerg="";
				double x = Intersection.getLongitude();
			 	double y = Intersection.getLatitude();
				 zwischenerg += Double.toString(x);
				 zwischenerg+=",";
				 zwischenerg+=Double.toString(y);
				 zwischenerg+=";";
				for(int position=((asym-1)*99);position<asym*99;position++){
					City intermediate = All_Cities.getCity(position);
				 	double x1 = intermediate.getLongitude();
				 	double y1=intermediate.getLatitude();
					zwischenerg += Double.toString(x1);
					zwischenerg+=",";
					zwischenerg+=Double.toString(y1);
					 if(position==((asym*99)-1))    //-1
					 {}
					 else{
						 zwischenerg+=";";
					 }
				}
				 String gesamt=urlAnfang+zwischenerg+"?sources=0";
			//	 System.out.println(gesamt);
				 StringBuffer response = gogo(gesamt); 
			     JSONObject jobj= new JSONObject(response.toString());
			     JSONArray dura_1 = jobj.getJSONArray("durations");
			     int z=1;
			     JSONArray dura_2=dura_1.getJSONArray(0);
			     for (int positionzeile=((asym-1)*99);positionzeile<asym*99;positionzeile++){
			    	 int toCityID=Integer.parseInt(All_Cities.getCity(positionzeile).getId());				   	   			    	    	
			    	 erg[toCityID] = dura_2.getDouble(z);
			    	 z++;				    	    	
			     }			   	    				    	
			    z=1;
			}	
		}
		return erg;
	}

	public static double[][]createBasicMatrix() throws Exception {
		// System.out.println("start:"+timestamp1);
		double[][] erg=new double[Distanzmatrix.CreatingnumOfCities+1][Distanzmatrix.CreatingnumOfCities+1];
		int numOfCities=All_Cities.numberOfCities();
		int numberOfCases;
		int SplittedtoAdd;
		int numberSplittedMatrix = 0;
		int numberSymmMatrix;		
		if(numOfCities%50==0){
			numberOfCases= numOfCities/50;		
		}
		else{
			numberOfCases=(numOfCities/50)+1;
		}
		if(numberOfCases%2==0){
			SplittedtoAdd=numberOfCases-2;
			numberSymmMatrix=numberOfCases/2;
		}
		else{
			SplittedtoAdd=numberOfCases-1;
			numberSymmMatrix=(numberOfCases+1)/2;
		}
		
		for(int x=SplittedtoAdd;x>0;x=x-2){
			if(numberOfCases%2==0){
			numberSplittedMatrix+=2*x;
			}
			else{
				if(x==SplittedtoAdd){
					numberSplittedMatrix+=x;
				}
				else{
					numberSplittedMatrix+=x*2;
				}
			}
		}		
		
		//Symmetrische Matrizen
		int IFzähler=0;
		for(int sym=1;sym<=numberSymmMatrix;sym++){
			if(numOfCities-(sym*100)<0){			 //letzte Symmetrische Matrix <=100
				if((numOfCities-(sym*100))==-99){
					erg[numOfCities-1][numOfCities-1]=0;
				}
				else{
					String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
					String zwischenerg="";
					for(int position=((sym-1)*100);position<numOfCities;position++){
						City intermediate = All_Cities.getCity(position);
					 	double x = intermediate.getLongitude();
					 	double y=intermediate.getLatitude();
						 zwischenerg += Double.toString(x);
						 zwischenerg+=",";
						 zwischenerg+=Double.toString(y);
						 if(position==(numOfCities-1))    //-1
						 {}
						 else{
						 zwischenerg+=";";
						 }
					}
					 String gesamt=urlAnfang+zwischenerg;
					// System.out.println(gesamt);
					 StringBuffer response = gogo(gesamt); 
				     JSONObject jobj= new JSONObject(response.toString());
				     JSONArray dura_1 = jobj.getJSONArray("durations");
				     int z=0;
				     int s=0;
				     for (int positionzeile=((sym-1)*100);positionzeile<numOfCities;positionzeile++){
				    	 JSONArray dura_2=dura_1.getJSONArray(s);	    	   
				    	 for (int positionspalte=((sym-1)*100);positionspalte<numOfCities;positionspalte++) {
					    	erg[positionzeile][positionspalte] = dura_2.getDouble(z);
					    	// System.out.print(s+" "+z+"    ");
				    	    z++;				    	    	
					    }
				   	    	//System.out.println();
				    	s++;
				    	z=0;
				     }	
			    IFzähler++;
				}
			}		
			if(numOfCities-(sym*100)>=0)  { //100x100 volle
				String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
				String zwischenerg="";
				for(int position=(sym-1)*100;position<sym*100;position++){
					City intermediate = All_Cities.getCity(position);
				 	double x = intermediate.getLongitude();
				 	double y=intermediate.getLatitude();
					zwischenerg += Double.toString(x);
					zwischenerg+=",";
					zwischenerg+=Double.toString(y);
					if(position==(sym*100-1) )  //-1
					{}
					else{
					zwischenerg+=";";
					}
				}
				String gesamt=urlAnfang+zwischenerg;
				//System.out.println(gesamt);
				 StringBuffer response = gogo(gesamt); 
			     JSONObject jobj= new JSONObject(response.toString());
			     JSONArray dura_1 = jobj.getJSONArray("durations");
			    int z=0;
			    int s=0;
			    for (int positionzeile=((sym-1)*100);positionzeile<sym*100;positionzeile++){
			    	JSONArray dura_2=dura_1.getJSONArray(s);  
			    	for (int positionspalte=((sym-1)*100);positionspalte<sym*100;positionspalte++) {
			    		//System.out.print(positionzeile+" "+positionspalte+" "+z+" "+s+"   ");
			    		erg[positionzeile][positionspalte] = dura_2.getDouble(z);
			    		z++;				    	    	
			    	}
			    	//	System.out.println();
			    	s++;
				    z=0;

			    }
			   IFzähler++;
			 //   System.out.println("IF 2: "+IFzähler);
			}
			
			// Splitted Matrizen
			
			if(sym>1){
				if(numOfCities-(sym*100)>=0){
					for(int zeile=1;zeile<=sym-1;zeile++){ //4 volle 50x50	
						for(int caseNR=1;caseNR<=4;caseNR++){	
							switch (caseNR){
								case 1: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100;a<(zeile-1)*100+50;a++){// /50 Städte der aktuellen zeile
									City intermediate = All_Cities.getCity(a);
								 	double x = intermediate.getLongitude();
								 	double y=intermediate.getLatitude();
									zwischenerg += Double.toString(x);
									zwischenerg+=",";
									zwischenerg+=Double.toString(y);
									zwischenerg+=";";
									}
									for(int b=(sym-1)*100;b<(sym-1)*100+50;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==(sym-1)*100+49)
										{}
										else{
											zwischenerg+=";";
										}						
									}
									String gesamt=urlAnfang+zwischenerg;
									 StringBuffer response = gogo(gesamt); 
								     JSONObject jobj= new JSONObject(response.toString());
								     JSONArray dura_1 = jobj.getJSONArray("durations");
								     int t=0;
								     int s=50;
								     int x=0;
								     int y=50;
								     for(int c=(zeile-1)*100;c<(zeile-1)*100+50;c++){
								    	 JSONArray dura_2=dura_1.getJSONArray(t);
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int d=(sym-1)*100;d<(sym-1)*100+50;d++){
								    		 erg[c][d] = dura_2.getDouble(s);
								    		 erg[d][c]= dura_3.getDouble(x);
									    		s++;
									    		x++;
								    	 }
								    	 s=50;
								    	 x=0;
								    	 t++;
								    	 y++;

								     }
								  //   IFzähler++;
									   // System.out.println("IF 3, Case1: "+IFzähler);
									 continue;
								}
								case 2: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100+50;a<(zeile-1)*100+100;a++){// /50 Städte der aktuellen zeile
										City intermediate = All_Cities.getCity(a);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										zwischenerg+=";";
									}
									for(int b=(sym-1)*100;b<(sym-1)*100+50;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==(sym-1)*100+49)
										{}
										else{
											zwischenerg+=";";
										}									
									}
									String gesamt=urlAnfang+zwischenerg;
									StringBuffer response = gogo(gesamt); 
								    JSONObject jobj= new JSONObject(response.toString());
								    JSONArray dura_1 = jobj.getJSONArray("durations");
								    int t=0;
								    int s=50;
								    int x=0;
								    int y=50;
								    for(int c=(zeile-1)*100+50;c<(zeile-1)*100+100;c++){
								    	 JSONArray dura_2=dura_1.getJSONArray(t);
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int d=(sym-1)*100;d<(sym-1)*100+50;d++){
								    		 erg[c][d] = dura_2.getDouble(s);
								    		 erg[d][c]= dura_3.getDouble(x);
									    		s++;
									    		x++;
								    	 }
								    	 s=50;
								    	 x=0;
								    	 t++;
								    	 y++;

								     }
								   //  IFzähler++;
									  //  System.out.println("IF 3, Case2: "+IFzähler);
									continue;
								}
								case 3: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100;a<(zeile-1)*100+50;a++){// /50 Städte der aktuellen zeile
										City intermediate = All_Cities.getCity(a);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										zwischenerg+=";";
									}
									for(int b=(sym-1)*100+50;b<(sym-1)*100+100;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==(sym-1)*100+99)
										{}
										else{
											zwischenerg+=";";
										}
										
									}
									String gesamt=urlAnfang+zwischenerg;

									 StringBuffer response = gogo(gesamt); 
								     JSONObject jobj= new JSONObject(response.toString());
								     JSONArray dura_1 = jobj.getJSONArray("durations");
								     int t=0;
								     int s=50;
								     int x=0;
								     int y=50;
								     for(int c=(zeile-1)*100;c<(zeile-1)*100+50;c++) {
								    	 JSONArray dura_2=dura_1.getJSONArray(t);
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int d=(sym-1)*100+50;d<(sym-1)*100+100;d++){
								    		 erg[c][d] = dura_2.getDouble(s);
								    		 erg[d][c]= dura_3.getDouble(x);
									    		s++;
									    		x++;
								    	 }
								    	 s=50;
								    	 x=0;
								    	 t++;
								    	 y++;

								     }
								     //IFzähler++;
									    //System.out.println("IF 3, Case3: "+IFzähler);
									    continue;
								}
								case 4: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100+50;a<(zeile-1)*100+100;a++){// /50 Städte der aktuellen zeile
										City intermediate = All_Cities.getCity(a);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										zwischenerg+=";";
									}
									for(int b=(sym-1)*100+50;b<(sym-1)*100+100;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==(sym-1)*100+99)
										{}
										else{
											zwischenerg+=";";
										}
										
									}
									String gesamt=urlAnfang+zwischenerg;
									
									 StringBuffer response = gogo(gesamt); 
								     JSONObject jobj= new JSONObject(response.toString());
								     JSONArray dura_1 = jobj.getJSONArray("durations");
								     int t=0;
								     int s=50;
								     int x=0;
								     int y=50;
								     for(int c=(zeile-1)*100+50;c<(zeile-1)*100+100;c++){
								    	 JSONArray dura_2=dura_1.getJSONArray(t);
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int d=(sym-1)*100+50;d<(sym-1)*100+100;d++){
								    		 erg[c][d] = dura_2.getDouble(s);
								    		 erg[d][c]= dura_3.getDouble(x);
									    		s++;
									    		x++;
									    		
								    	 }
								    	 s=50;
								    	 x=0;
								    	 t++;
								    	 y++;

								     }
								 //    System.out.println(gesamt);
								     	//IFzähler++;
									   // System.out.println("IF 3, Case4: "+IFzähler);
									    continue;
								}		
							}						
						}						
					}
				}
				if(numOfCities-(sym*100)<0&&numOfCities-(sym*100)>=-49) {// 2 volle 50x50 2xsplitted
					for(int zeile=1;zeile<=sym-1;zeile++){ //??? Zeilen gehen 100 schritte
						for(int caseNR=1;caseNR<=4;caseNR++){
							switch (caseNR){
								case 1: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100;a<(zeile-1)*100+50;a++){// /50 Städte der aktuellen zeile
										
										City intermediate = All_Cities.getCity(a);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										zwischenerg+=";";
									}
									for(int b=(sym-1)*100;b<(sym-1)*100+50;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==(sym-1)*100+49)
										{}
										else{
											zwischenerg+=";";
										}
									}
									String gesamt=urlAnfang+zwischenerg;
									 StringBuffer response = gogo(gesamt); 
								     JSONObject jobj= new JSONObject(response.toString());
								     JSONArray dura_1 = jobj.getJSONArray("durations");
								     int t=0;
								     int s=50;
								     int x=0;
								     int y=50;
								     for(int c=(zeile-1)*100;c<(zeile-1)*100+50;c++){
								    	 JSONArray dura_2=dura_1.getJSONArray(t);
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int d=(sym-1)*100;d<(sym-1)*100+50;d++){
								    		 erg[c][d] = dura_2.getDouble(s);
								    		 erg[d][c]= dura_3.getDouble(x);
									    		s++;
									    		x++;
								    	 }
								    	 s=50;
								    	 x=0;
								    	 t++;
								    	 y++;

								     }
								    // IFzähler++;
									 //   System.out.println("IF 4, Case1: "+IFzähler);
									    continue;
								}
								case 2: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100+50;a<(zeile-1)*100+100;a++){// /50 Städte der aktuellen zeile									
										City intermediate = All_Cities.getCity(a);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										zwischenerg+=";";
									}
									for(int b=(sym-1)*100;b<(sym-1)*100+50;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==(sym-1)*100+49)
										{}
										else{									
											zwischenerg+=";";
										}
										
									}
									String gesamt=urlAnfang+zwischenerg;								 
									 StringBuffer response = gogo(gesamt); 
								     JSONObject jobj= new JSONObject(response.toString());
								     JSONArray dura_1 = jobj.getJSONArray("durations");
								     int t=0;
								     int s=50;
								     int x=0;
								     int y=50;
								     for(int c=(zeile-1)*100+50;c<(zeile-1)*100+100;c++){
								    	 JSONArray dura_2=dura_1.getJSONArray(t);
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int d=(sym-1)*100;d<(sym-1)*100+50;d++){
								    		 erg[c][d] = dura_2.getDouble(s);
								    		 erg[d][c]= dura_3.getDouble(x);
									    		s++;
									    		x++;
								    	 }
								    	 s=50;
								    	 x=0;
								    	 t++;
								    	 y++;

								     }
								  //   IFzähler++;
									  //  System.out.println("IF 4, Case2: "+IFzähler);
									 continue;
								}
								case 3: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100;a<(zeile-1)*100+50;a++){// /50 Städte der aktuellen zeile
										City intermediate = All_Cities.getCity(a);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										zwischenerg+=";";
									}
									for(int b=(sym-1)*100+50;b<numOfCities;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==numOfCities-1)
										{}
										else{
											zwischenerg+=";";
										}
									}
									String gesamt=urlAnfang+zwischenerg;									 
									 StringBuffer response = gogo(gesamt); 
								     JSONObject jobj= new JSONObject(response.toString());
								     JSONArray dura_1 = jobj.getJSONArray("durations");
								     int t=0;
								     int s=50;
								     int x=0;
								     int y=50;
								     for(int c=(zeile-1)*100;c<(zeile-1)*100+50;c++){
								    	 JSONArray dura_2=dura_1.getJSONArray(t);
								    	 for(int d=(sym-1)*100+50;d<numOfCities;d++){
								    		 erg[c][d] = dura_2.getDouble(s);								    		 								    	
									    		s++;
								    	 }
								    	 s=50;
								    	 t++;
								     }
								     
								    for(int e=(sym-1)*100+50;e<numOfCities;e++){
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int f=(zeile-1)*100;f<(zeile-1)*100+50;f++){
								    	 	erg[e][f] = dura_3.getDouble(x);		
										    x++;											    		
								    	 }
									     x=0;
									     y++;
								    }

								  //  IFzähler++;
								//    System.out.println("IF 4, Case3: "+IFzähler);
								    continue;
								}
								case 4:{ 
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100+50;a<(zeile-1)*100+100;a++){// /50 Städte der aktuellen zeile
										City intermediate = All_Cities.getCity(a);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										zwischenerg+=";";
									}
									for(int b=(sym-1)*100+50;b<numOfCities;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==numOfCities-1)
										{}
										else{
											zwischenerg+=";";
										}
									}
									String gesamt=urlAnfang+zwischenerg;
									 StringBuffer response = gogo(gesamt); 
								     JSONObject jobj= new JSONObject(response.toString());
								     JSONArray dura_1 = jobj.getJSONArray("durations");
								     int t=0;
								     int s=50;
								     int x=0;
								     int y=50;
								     for(int c=(zeile-1)*100+50;c<(zeile-1)*100+100;c++){
								    	 JSONArray dura_2=dura_1.getJSONArray(t);
								    	 for(int d=(sym-1)*100+50;d<numOfCities;d++){
								    		 erg[c][d] = dura_2.getDouble(s);								    		 								    	
									    		s++;
								    	 }
								    	 s=50;
								    	 t++;
								     }
								     
								    for(int e=(sym-1)*100+50;e<numOfCities;e++){
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int f=(zeile-1)*100+50;f<(zeile-1)*100+100;f++){
								    	 	erg[e][f] = dura_3.getDouble(x);		
										    x++;											    		
									    }
									     x=0;
									     y++;
								    }
								//    IFzähler++;
								    //System.out.println("IF 4, Case4: "+IFzähler);
								    continue;
								}	
							}						
						}						
					}
				}
				
				if(numOfCities-(sym*100)<-49){
					for(int zeile=1;zeile<=sym-1;zeile++){ //??? 2x splitted
						for(int caseNR=1;caseNR<=2;caseNR++){
							switch (caseNR){
								case 1: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100;a<(zeile-1)*100+50;a++){// /50 Städte der aktuellen zeile
										City intermediate = All_Cities.getCity(a);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										zwischenerg+=";";
									}
									for(int b=(sym-1)*100;b<numOfCities;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==numOfCities-1)
										{}
										else{
											zwischenerg+=";";
										}
										
									}
									String gesamt=urlAnfang+zwischenerg;
									
									 StringBuffer response = gogo(gesamt); 
								     JSONObject jobj= new JSONObject(response.toString());
								     JSONArray dura_1 = jobj.getJSONArray("durations");
								     int t=0;
								     int s=50;
								     int x=0;
								     int y=50;
								     for(int c=(zeile-1)*100;c<(zeile-1)*100+50;c++){
								    	 JSONArray dura_2=dura_1.getJSONArray(t);
								    	 for(int d=(sym-1)*100;d<numOfCities;d++)
								    	 {
								    		 erg[c][d] = dura_2.getDouble(s);								    		 								    	
								    		 s++;
								    	 }
								    	 s=50;
								    	 t++;
								     }
								    for(int e=(sym-1)*100;e<numOfCities;e++){	
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int f=(zeile-1)*100;f<(zeile-1)*100+50;f++){
								    	 	erg[e][f] = dura_3.getDouble(x);		
										    x++;											    		
									    }
									     x=0;
									     y++;
								    }
								    IFzähler++;
								
								    continue;
								}
								case 2: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100+50;a<(zeile-1)*100+100;a++){// /50 Städte der aktuellen zeile
										City intermediate = All_Cities.getCity(a);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										zwischenerg+=";";
									}
									for(int b=(sym-1)*100;b<numOfCities;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==numOfCities-1)
										{}
										else{
											zwischenerg+=";";
										}
									}
									String gesamt=urlAnfang+zwischenerg;
								  
									 StringBuffer response = gogo(gesamt); 
								     JSONObject jobj= new JSONObject(response.toString());
								     JSONArray dura_1 = jobj.getJSONArray("durations");
								     int t=0;
								     int s=50;
								     int x=0;
								     int y=50;
								     for(int c=(zeile-1)*100+50;c<(zeile-1)*100+100;c++){
								    	 JSONArray dura_2=dura_1.getJSONArray(t);
								    	 for(int d=(sym-1)*100;d<numOfCities;d++){
								    		 erg[c][d] = dura_2.getDouble(s);								    		 								    	
									    		s++;
								    	 }
								    	 s=50;
								    	 t++;
								     } 
								    for(int e=(sym-1)*100;e<numOfCities;e++){
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int f=(zeile-1)*100+50;f<(zeile-1)*100+100;f++){
								    	 	erg[e][f] = dura_3.getDouble(x);		
										    x++;											    		
									    }
									     x=0;
									     y++;
								    }
								    IFzähler++;
								    
								    continue;
								}
							}						
						}						
					}
				}	
			}
		}
		return erg;
	}

	public static double[][] createsmallMatrix() throws Exception{
		double[][] erg=new double[EA.numOfCities][EA.numOfCities];
		String urlAnfang="https://api.openrouteservice.org/matrix?api_key=58d904a497c67e00015b45fce60fe6750d3e4061a1e3178c1db4f08e&profile=driving-car&locations=";
		//String urlAnfang = "http://router.project-osrm.org/table/v1/driving/";
		 String zwischenerg="";
		 for(int i=0; i<All_Cities.numberOfCities();i++){
			 City intermediate = All_Cities.getCity(i);
			 double x = intermediate.getLongitude();
		   	 double y=intermediate.getLatitude();
			 zwischenerg += Double.toString(x);
			 zwischenerg+="%2C";
			 zwischenerg+=Double.toString(y);
			 if(i==(All_Cities.numberOfCities()-1))    //-1
			 {}
			else{
				zwischenerg+="%7C";
			} 
		 }
		 String gesamt=urlAnfang+zwischenerg;
		
		 StringBuffer response = gogo(gesamt); 	 
	     JSONObject jobj= new JSONObject(response.toString());
	     JSONArray dura_1 = jobj.getJSONArray("durations");  
	     for (int t=0; t<All_Cities.numberOfCities();t++){
	    	JSONArray dura_2=dura_1.getJSONArray(t);
	    	
	        for (int i = 0; i < EA.numOfCities; i++) {
	       
	   	        erg[t][i] = dura_2.getDouble(i);	    	    	
	   	    }
	    }
	     return erg;
	}
}
	
	  



