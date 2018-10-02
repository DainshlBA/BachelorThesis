

import java.util.Calendar;
// Class for for saving and managing time 
public class TimeElement{

//VARIABLES:
	Calendar start;
	long startInMilli;

	
//CONSTRUCTOR:
	public TimeElement(){
		 this.start=Calendar.getInstance();
		this.startInMilli= start.getTimeInMillis();
		
	}
	
	public TimeElement(long timeinMilli) {
		 Calendar cal =Calendar.getInstance();
		 cal.setTimeInMillis(timeinMilli);
		 
		 this.start=cal;
		this.startInMilli=start.getTimeInMillis();
		
	}
	
//METHODS:
	//Gets hour of initialized time
	@SuppressWarnings("deprecation")
	public int getHour(){
		return start.getTime().getHours();
		
	}
	
	public int getStep() {
		int step=1;
		int minutes =start.getTime().getMinutes();
		int intervall=60/Faktor.steps;
		for(int i=1;i<=Faktor.steps;i++) {
			if(intervall*(i)>=minutes) {
				step=i;
				break;
			}
		}
		return step;
	}
	//Gets the milli seconds value at next hour
	public long getMilliatNextHour() { 	
		int year=start.getTime().getYear()+1900;
		int month=start.getTime().getMonth();
		int day= start.getTime().getDate();
		int hour=start.getTime().getHours()+1;
		int minute=0;
		int second=0;
		long timeAtNextHour;
		Calendar nextHour= Calendar.getInstance();
		nextHour.set(year,month,day,hour,minute,second);	
		timeAtNextHour= nextHour.getTimeInMillis();  
		return timeAtNextHour;
		
								
	}
	public long getMilliatNextStep() { 	
		int year=start.getTime().getYear()+1900;
		int month=start.getTime().getMonth();
		int day= start.getTime().getDate();
		int hour=start.getTime().getHours();
		int minute=start.getTime().getMinutes();
		int second=0;
		int intervall=60/Faktor.steps;
		for(int i=1;i<=Faktor.steps;i++) {
			if(intervall*(i)>=minute) {
				minute=i*intervall;
				break;
			}
		}
		long timeAtNextStep;
		Calendar nextStep= Calendar.getInstance();
		nextStep.set(year,month,day,hour,minute,second);	
		timeAtNextStep= nextStep.getTimeInMillis();  
		return timeAtNextStep;
								
	}
	//Get seconds to fill up time of "start" to match the next full hour
	public double getTimeToNextHour() { 	
		int year=start.getTime().getYear()+1900;
		int month=start.getTime().getMonth();
		int day= start.getTime().getDate();
		int hour=start.getTime().getHours()+1;
		int minute=0;
		int second=0;
		Calendar nextHour= Calendar.getInstance();
		nextHour.set(year,month,day,hour,minute,second);			
		double timeToNextHour= Maths.round((nextHour.getTimeInMillis()-startInMilli)/1000,3);   //In Seconds
		return timeToNextHour;						
	}
	
	//set time of a TimeElement to a full hour at this day
	public void setStartTimetoHour(int hour2) {
		int year=start.getTime().getYear()+1900;
		int month=start.getTime().getMonth();
		int day= start.getTime().getDate();
		int minute=0;
		int second=0;
	
		start.set(year,month,day,hour2,minute,second);
		startInMilli=start.getTimeInMillis();
	
	}
	
	//Diffrent toString() methods
	@SuppressWarnings("deprecation")
	public String toString() {
		 String s="";
		 s=start.getTime().toString();
		 return s;
	 }
	public String toString2() {
		 String s="";
		 s=String.valueOf(start.getTime().getHours())+":"+String.valueOf(start.getTime().getMinutes()+":"+String.valueOf(start.getTime().getSeconds()));
		 return s;
	 }
	public String toString3() {
		 String s="";
		 s=String.valueOf(start.getTime().getHours())+""+String.valueOf(start.getTime().getMinutes()+""+String.valueOf(start.getTime().getSeconds()));
		 return s;
	 }
	
}
