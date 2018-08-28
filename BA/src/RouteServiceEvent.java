
import java.util.ArrayList;
import java.util.EventObject;
// Event class for activating the Simulation class "Simulator" after a RouteRequest
public class RouteServiceEvent extends EventObject{

//VARIABLES:
	private static final long serialVersionUID = 1L;
	double[] durations;
	Tour best;
	ArrayList<City> Nodes;
	ArrayList<City> Intersection;
	TimeElement eTime;

//CONSTRUCTOR:
	public RouteServiceEvent(EA Opti, ArrayList<City> nodes, ArrayList<City> intersections, double[] durations, Tour best,TimeElement te) {
		super(Opti);
		this.Nodes=nodes;
		this.Intersection=intersections;
		this.durations=durations;
		this.best=best;
		this.eTime=te;
	}
	
}
