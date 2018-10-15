import java.util.ArrayList;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<City>ar=new ArrayList<City>();
		Send_Request sr= new Send_Request();
		String[] s = {"25182297","1234333","25182297"};
	try {
		ar=	Send_Request.ConvertAPI(s);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	for(int a=0;a<ar.size();a++) {
		System.out.println(ar.get(a));
	}
	}
}
