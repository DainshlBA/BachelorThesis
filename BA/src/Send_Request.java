import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

//Class for managing and sending requests, managing and preparing the data for Evolutionary Algorithm und Simulation
public class Send_Request {
	
//METHODS:
	
	//opens an URL connection, sends URL and gets response in Stringbuffer
	public static  StringBuffer gogo(String gesamt) throws Exception{			
		URL obj = new URL(gesamt);
	
	    HttpURLConnection con = (HttpURLConnection) obj.openConnection();						
	    con.setRequestMethod("GET");															
	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));	
	    String inputLine;
	    StringBuffer response = new StringBuffer();
	    while ((inputLine = in.readLine()) != null) {											
	     	response.append(inputLine);
	    }
	    in.close();																				
	   																		
	    return response;
	}
	
	//Creates URL for Route request
	//Calls method gogo and saves Response as JSON Object 
	public static JSONObject createRouteRequest(Tour fittest) throws Exception{	
		JSONObject Way;		//Creates String with URL, applies gogo and saves response in an JSONObejct
		String gesamt= "https://w-bdlab-s1.hs-karlsruhe.de/osrm/route/v1/driving/";						//Fixed URL Start
		
		City From;
		City To;
		if(Run.runs==true) {
		From=fittest.getCity(1);	
		To=fittest.getCity(2);
		}
		else{
			From=fittest.getCity(0);
			To=fittest.getCity(1);
		}
		double x1=From.getLongitude();															
		double y1=From.getLatitude();															
		double x2=To.getLongitude();															
		double y2= To.getLatitude();															
		gesamt+=Double.toString(x1)+","+Double.toString(y1)+";"+Double.toString(x2)+","+Double.toString(y2)+"?steps=true&annotations=true"; 
		StringBuffer response = gogo(gesamt);									
		Way= new JSONObject(response.toString());									
		return Way;
	}
	
	//Converts node IDs into coordinates by calling OSM API and parse and handles response with OSMHandler  and XMLHandler
	public static ArrayList<City> ConvertAPI(String[]nodes) throws Exception{										
		ArrayList<City> Nodes= new ArrayList<City>();																
		for(int i=0;i<nodes.length;i++){												
			String url="https://w-bdlab-s1.hs-karlsruhe.de/osm/api/0.6/node/";
			url+=nodes[i];
			  try
		        {	           
		            URL urlObject = new URL(url);		           
		            InputStream in = urlObject.openStream();
		            @SuppressWarnings("deprecation")
					XMLReader xr = XMLReaderFactory.createXMLReader();	        
		            OSMHandler ourSpecialHandler = new OSMHandler();
		            xr.setContentHandler(ourSpecialHandler);	           
		            InputSource inSource = new InputSource(in); 
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
	
	//Calculates the most efficient way for sending the request, efficient==few requests
	//Prepares URL and calls method gogo
	//Manages response and updates last row of all matrixes, no regulation in number of cities
	public static double[] IntersectionMatrix(City Intersection) throws Exception{	
		int numberofCities=All_Cities.checkForCities();
		
		double[] erg=new double[D_Matrix.CreatingnumOfCities];
		int numberOfCases;
		if(numberofCities%99==0){
		 numberOfCases= numberofCities/99;	
		}
		else{
			numberOfCases=(numberofCities/99)+1;
		}
		for(int asym=1;asym<=numberOfCases;asym++){
			if(numberofCities-(asym*99)<0){   
				String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
				String zwischenerg="";
				double x = Intersection.getLongitude();			
			 	double y = Intersection.getLatitude();
			 	zwischenerg += Double.toString(x);
				zwischenerg+=",";
				zwischenerg+=Double.toString(y);
				zwischenerg+=";";
				for(int position=((asym-1)*99);position<numberofCities;position++){
					City intermediate = All_Cities.getCity(position);				
				 	double x1 = intermediate.getLongitude();
				 	double y1=intermediate.getLatitude();
					zwischenerg += Double.toString(x1);
					zwischenerg+=",";
					zwischenerg+=Double.toString(y1);
					if(position==(numberofCities-1))    //-1
					{}
					else{
					zwischenerg+=";";
					}
				}
				 String gesamt=urlAnfang+zwischenerg+"?sources=0";
				 StringBuffer response = gogo(gesamt); 
			     JSONObject jobj= new JSONObject(response.toString());
			     JSONArray dura_1 = jobj.getJSONArray("durations");
			     int z=1;
			    
			     JSONArray dura_2=dura_1.getJSONArray(0);
			     for (int positionzeile=((asym-1)*99);positionzeile<numberofCities;positionzeile++){//numofCities könnte falsch sein
			    	 int toCityID=Integer.parseInt(All_Cities.getCity(positionzeile).getId());	
			    	
			    	 erg[toCityID] = dura_2.getDouble(z);
			    	 z++;				    	    	
			     }			   	    				    	
			    	z=1;
			}	
			if(numberofCities-(asym*99)>=0){	
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

	//Creates first duration matrix up to 100 Cities
	public static double[][] createsmallMatrix() throws Exception{
		double[][] erg=new double[EA.numberofCities+1][EA.numberofCities+1];
		String urlAnfang="https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
		 String zwischenerg="";
		 for(int i=0; i<All_Cities.numberOfCities();i++){
			 City intermediate = All_Cities.getCity(i);
			 double x = intermediate.getLongitude();
		   	 double y=intermediate.getLatitude();
			 zwischenerg += Double.toString(x);
			 zwischenerg+=",";
			 zwischenerg+=Double.toString(y);
			 if(i==(All_Cities.numberOfCities()-1))    
			 {}
			else{
				zwischenerg+=";";
			} 
		 }
		 String gesamt=urlAnfang+zwischenerg;
	System.out.println(gesamt);
		 StringBuffer response = gogo(gesamt); 	 
	     JSONObject jobj= new JSONObject(response.toString());
	     JSONArray dura_1 = jobj.getJSONArray("durations");  
	     for (int t=0; t<All_Cities.numberOfCities();t++){
	    	JSONArray dura_2=dura_1.getJSONArray(t);
	    	
	        for (int i = 0; i < EA.numberofCities; i++) {
	       
	   	        erg[t][i] = dura_2.getDouble(i);	    	    	
	   	    }
	    }
	     return erg;
	}
	
	
	
	
	
	//Creates first duration matrix
	//Calculates most efficient way to save requests
	//No regulation in number of cities
	public static double[][]createBasicMatrix() throws Exception {
		double[][] erg=new double[D_Matrix.CreatingnumOfCities+1][D_Matrix.CreatingnumOfCities+1];
		int numberofCities=All_Cities.numberOfCities();
		int numberOfCases;
		int numberSymmMatrix;		
		if(numberofCities%50==0){
			numberOfCases= numberofCities/50;		
		}
		else{
			numberOfCases=(numberofCities/50)+1;
		}
		if(numberOfCases%2==0){
			numberSymmMatrix=numberOfCases/2;
		}
		else{	
			numberSymmMatrix=(numberOfCases+1)/2;
		}

		
		for(int sym=1;sym<=numberSymmMatrix;sym++){
			if(numberofCities-(sym*100)<0){			
				if((numberofCities-(sym*100))==-99){
					erg[numberofCities-1][numberofCities-1]=0;
				}
				else{
					String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
					String zwischenerg="";
					for(int position=((sym-1)*100);position<numberofCities;position++){
						City intermediate = All_Cities.getCity(position);
					 	double x = intermediate.getLongitude();
					 	double y=intermediate.getLatitude();
						 zwischenerg += Double.toString(x);
						 zwischenerg+=",";
						 zwischenerg+=Double.toString(y);
						 if(position==(numberofCities-1))    //-1
						 {}
						 else{
						 zwischenerg+=";";
						 }
					}
					 String gesamt=urlAnfang+zwischenerg;
					
					 StringBuffer response = gogo(gesamt); 
				     JSONObject jobj= new JSONObject(response.toString());
				     JSONArray dura_1 = jobj.getJSONArray("durations");
				     int z=0;
				     int s=0;
				     for (int positionzeile=((sym-1)*100);positionzeile<numberofCities;positionzeile++){
				    	 JSONArray dura_2=dura_1.getJSONArray(s);	    	   
				    	 for (int positionspalte=((sym-1)*100);positionspalte<numberofCities;positionspalte++) {
					    	erg[positionzeile][positionspalte] = dura_2.getDouble(z);
					    	
				    	    z++;				    	    	
					    }
				   	    	
				    	s++;
				    	z=0;
				     }	
			    
				}
			}		
			if(numberofCities-(sym*100)>=0)  {
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
				 StringBuffer response = gogo(gesamt); 
			     JSONObject jobj= new JSONObject(response.toString());
			     JSONArray dura_1 = jobj.getJSONArray("durations");
			    int z=0;
			    int s=0;
			    for (int positionzeile=((sym-1)*100);positionzeile<sym*100;positionzeile++){
			    	JSONArray dura_2=dura_1.getJSONArray(s);  
			    	for (int positionspalte=((sym-1)*100);positionspalte<sym*100;positionspalte++) {
			    		erg[positionzeile][positionspalte] = dura_2.getDouble(z);
			    		z++;				    	    	
			    	}
			    	s++;
				    z=0;

			    }
			   
			}
			
			
			
			if(sym>1){
				if(numberofCities-(sym*100)>=0){
					for(int zeile=1;zeile<=sym-1;zeile++){
						for(int caseNR=1;caseNR<=4;caseNR++){	
							switch (caseNR){
								case 1: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100;a<(zeile-1)*100+50;a++){
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
								   
									 continue;
								}
								case 2: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100+50;a<(zeile-1)*100+100;a++){
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
									continue;
								}
								case 3: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100;a<(zeile-1)*100+50;a++) {
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
									    continue;
								}
								case 4: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100+50;a<(zeile-1)*100+100;a++) {
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
								     continue;
								}		
							}						
						}						
					}
				}
				if(numberofCities-(sym*100)<0&&numberofCities-(sym*100)>=-49) {
					for(int zeile=1;zeile<=sym-1;zeile++){ 
						for(int caseNR=1;caseNR<=4;caseNR++){
							switch (caseNR){
								case 1: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100;a<(zeile-1)*100+50;a++) {
										
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
								  
									 continue;
								}
								case 3: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100;a<(zeile-1)*100+50;a++){
										City intermediate = All_Cities.getCity(a);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										zwischenerg+=";";
									}
									for(int b=(sym-1)*100+50;b<numberofCities;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==numberofCities-1)
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
								    	 for(int d=(sym-1)*100+50;d<numberofCities;d++){
								    		 erg[c][d] = dura_2.getDouble(s);								    		 								    	
									    		s++;
								    	 }
								    	 s=50;
								    	 t++;
								     }
								     
								    for(int e=(sym-1)*100+50;e<numberofCities;e++){
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int f=(zeile-1)*100;f<(zeile-1)*100+50;f++){
								    	 	erg[e][f] = dura_3.getDouble(x);		
										    x++;											    		
								    	 }
									     x=0;
									     y++;
								    }
								    continue;
								}
								case 4:{ 
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100+50;a<(zeile-1)*100+100;a++){
										City intermediate = All_Cities.getCity(a);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										zwischenerg+=";";
									}
									for(int b=(sym-1)*100+50;b<numberofCities;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==numberofCities-1)
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
								    	 for(int d=(sym-1)*100+50;d<numberofCities;d++){
								    		 erg[c][d] = dura_2.getDouble(s);								    		 								    	
									    		s++;
								    	 }
								    	 s=50;
								    	 t++;
								     }
								     
								    for(int e=(sym-1)*100+50;e<numberofCities;e++){
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int f=(zeile-1)*100+50;f<(zeile-1)*100+100;f++){
								    	 	erg[e][f] = dura_3.getDouble(x);		
										    x++;											    		
									    }
									     x=0;
									     y++;
								    }
								    continue;
								}	
							}						
						}						
					}
				}
				
				if(numberofCities-(sym*100)<-49){
					for(int zeile=1;zeile<=sym-1;zeile++) {
						for(int caseNR=1;caseNR<=2;caseNR++){
							switch (caseNR){
								case 1: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100;a<(zeile-1)*100+50;a++){
										City intermediate = All_Cities.getCity(a);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										zwischenerg+=";";
									}
									for(int b=(sym-1)*100;b<numberofCities;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==numberofCities-1)
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
								    	 for(int d=(sym-1)*100;d<numberofCities;d++)
								    	 {
								    		 erg[c][d] = dura_2.getDouble(s);								    		 								    	
								    		 s++;
								    	 }
								    	 s=50;
								    	 t++;
								     }
								    for(int e=(sym-1)*100;e<numberofCities;e++){	
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int f=(zeile-1)*100;f<(zeile-1)*100+50;f++){
								    	 	erg[e][f] = dura_3.getDouble(x);		
										    x++;											    		
									    }
									     x=0;
									     y++;
								    }
								    
								
								    continue;
								}
								case 2: {
									String urlAnfang = "https://w-bdlab-s1.hs-karlsruhe.de/osrm/table/v1/driving/";
									String zwischenerg="";
									for(int a=(zeile-1)*100+50;a<(zeile-1)*100+100;a++){
										City intermediate = All_Cities.getCity(a);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										zwischenerg+=";";
									}
									for(int b=(sym-1)*100;b<numberofCities;b++){
										City intermediate = All_Cities.getCity(b);
									 	double x = intermediate.getLongitude();
									 	double y=intermediate.getLatitude();
										zwischenerg += Double.toString(x);
										zwischenerg+=",";
										zwischenerg+=Double.toString(y);
										if(b==numberofCities-1)
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
								    	 for(int d=(sym-1)*100;d<numberofCities;d++){
								    		 erg[c][d] = dura_2.getDouble(s);								    		 								    	
									    		s++;
								    	 }
								    	 s=50;
								    	 t++;
								     } 
								    for(int e=(sym-1)*100;e<numberofCities;e++){
								    	 JSONArray dura_3=dura_1.getJSONArray(y);
								    	 for(int f=(zeile-1)*100+50;f<(zeile-1)*100+100;f++){
								    	 	erg[e][f] = dura_3.getDouble(x);		
										    x++;											    		
									    }
									     x=0;
									     y++;
								    }  
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
}
	
	  



