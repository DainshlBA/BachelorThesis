
public class QGram {
	
	String id1;
	String id2;
	
	public QGram(String i1, String i2) {
		
		id1=i1;
		id2=i2;	
	}

	
	
	public void setId1(String s) {
		id1=s;
	}

	
	public void setId2(String s) {
		id2=s;
	}
	
	
	
	public String getId1() {
		return id1;
	}

	
	public String getId2() {
		return id2;
	}
	
	
	
	public boolean isEqualTo(QGram q) {
		boolean isEqual = false;
		
		if (this.id1.equals(q.getId1()) && this.id2.equals(q.getId2())) {
			isEqual = true;
		}
		
		return isEqual;				
	}
}



