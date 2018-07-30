

import java.util.Calendar;
import java.util.Random;

//Class for mathematical methods and all hour factors
public class Maths {
	
//VARIABLES:
	//All 24 factors representing traffic at specific hour
	static double []Faktoren= {(1/1.2),(1/1.3),(1/1.2),(1/1.1),(1/1),(1/0.9),(1/0.8),(1/0.7),(1/0.8),(1/0.85),(1/0.9),(1/0.95),(1/1),(1/0.9),(1/0.8),(1/0.7),(1/0.75),(1/0.8),(1/0.9),(1/0.95),(1/1),(1/1.05),(1/1.1),(1/1.15)};
	static double [] SimulationsFaktoren= new double[Faktoren.length];
	static double [] GammaFaktoren={(1/1.23),(1/1.34),(1/1.21),(1/1.13),(1/1.4),(1/0.93),(1/0.83),(1/0.71),(1/0.82),(1/0.851),(1/0.92),(1/0.953),(1/1.14),(1/0.92),(1/0.9),(1/0.72),(1/0.735),(1/0.78),(1/0.89),(1/0.975),(1/1.1),(1/1.15),(1/1.21),(1/1.115)};
//METHODS:
	public static double getFaktor(int hour) {
		return Faktoren[hour];
	}
	public static double getGammaFaktor(int hour) {
		return SimulationsFaktoren[hour];
	}
	// Method for rounding a double value to specific number of decimal places 
	public  static double round(double value, double decimal){
		double erg=Math.round(value*Math.pow(10,decimal))/Math.pow(10, decimal);
		return erg;
	}
	
	//Randomly calculates new time factors for calculation
	public static void calcSimFaktoren() {
		for(int f=0; f<Faktoren.length;f++) {
			SimulationsFaktoren[f]=Maths.round(Faktoren[f]*((Math.random()*0.2)+0.9),3);
		}
	}
	
	public static void calcGamFaktoren() {
		for(int f=0; f<Faktoren.length;f++) {
			SimulationsFaktoren[f]=Maths.round(Faktoren[f]*((Math.random()*0.2)+0.9),3);
		}
	}
	
	
	//EAmmaFunction for simulating duration values
	public static void goGamma(double k, double theta, double shiftDistance) {
		SimulationsFaktoren= new double[Faktoren.length];
		for(int a=0; a<Faktoren.length;a++) {
			
			boolean accept = false;
			Random rng = new Random(Calendar.getInstance().getTimeInMillis() + Thread.currentThread().getId());
		    if (k < 1) {
		    	// Weibull algorithm
		    	double c = (1 / k);
		    	double d = ((1 - k) * Math.pow(k, (k / (1 - k))));
		    	double u, v, z, e, x;
		    	
		    	do {
		    		u = rng.nextDouble();
		    		v = rng.nextDouble();
		    		z = -Math.log(u);
		    		e = -Math.log(v);
		    		x = Math.pow(z, c);
		    		
		    		if ((z + e) >= (d + x)) {
		    			accept = true;
		    			}
		    		} while (!accept);
		    	double GammaValue =  ((x * theta) + shiftDistance);
		    
		    	SimulationsFaktoren[a] = Faktoren[a] * GammaValue;
		    	
		    } 
		    else {	
			 // Cheng's algorithm   	
			 double b = (k - Math.log(4));
			 double c = (k + Math.sqrt(2 * k - 1));
			 double lam = Math.sqrt(2 * k - 1);
			 double cheng = (1 + Math.log(4.5));
			 double u, v, x, y, z, r;
			 
			 do {
				 u = rng.nextDouble();
				 v = rng.nextDouble();
				 y = ((1 / lam) * Math.log(v / (1 - v)));
				 x = (k * Math.exp(y));
				 z = (u * v * v);
				 r = (b + (c * y) - x);
				 
				 if ((r >= ((4.5 * z) - cheng)) || (r >= Math.log(z))) {
					 accept = true;
					 }
				 
				 } while (!accept);
			 
			 double GammaValue = ((x * theta) + shiftDistance);
			 SimulationsFaktoren[a] = Faktoren[a] * GammaValue;
		    }  
		}
	}
}
