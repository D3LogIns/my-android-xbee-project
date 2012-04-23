package XBee.Configurator;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;

public class FakeXBee implements Serializable{


	private static final long serialVersionUID = 1375942286432721341L;
	private String address;
	private String type;
	private String ss;
	private LinkedList<String> myActuators=new LinkedList<String>();

	
	
	
	public FakeXBee(String a, String t, String s){
		this.address=a;
		this.type=t;
		this.ss=s;
		

		
	}
	
	public void setActuator(String addr){
		myActuators.add(addr);
	}
	
	public String getAdress() {
		return this.address;
	}

	public String getType() {

			return this.type;

	}

	public String getSignalStrength() {
		return this.ss;
	}
	
	
	public LinkedList<String> getActuators(){
		return myActuators;
	}
	
	public void removeActuator(String addr){
		ListIterator<String> it=myActuators.listIterator();
		while(it.hasNext()){
			if(it.next().equals(addr))
				myActuators.remove(addr);
		}
	}

}
