//Listener interface for atEvents
public interface myListener {

		public void atCity(AtEvent e) throws Exception;
		public void atIntersection(AtEvent e);
		public void GPS_Signal(AtEvent e) ;
}
