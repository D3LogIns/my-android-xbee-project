package XBee.Configurator;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;

public class XBeeDevice implements Serializable{


	private static final long serialVersionUID = 1375942286432721341L;
	private String sh;
	private String sl;
	private String address;
	private String type;
	private String ss;
	private LinkedList<String> myActuators=new LinkedList<String>();
	private String mySensor="";

	
	
	
	public XBeeDevice(String a, String t, String s){
		this.address=a;
		
		this.type=t;
		this.ss=s;

	}
	
	public XBeeDevice(String sh, String sl, String type, String signal_strength){
		this.sh=sh;
		this.sl=sl;
		
		if(type.equals("0"))
			this.type="Coordenador";
		else if(type.equals("1"))
			this.type="Router";
		else if(type.equals("3"))
			this.type="Sensor";
		
		this.ss=signal_strength;
	}
	
	public void setActuator(String addr){
		myActuators.add(addr);
	}
	
	public void setSensor(String addr){
		mySensor=addr;
	}
	
	public String getAdress() {
		return this.sh+" "+this.sl;
	}

	public String getType() {
		return this.type;
	}

	public String getSignalStrength() {
		return this.ss;
	}
	
	public String getMySensor(){
		return mySensor;
	}
	
	
	public LinkedList<String> getMyActuators(){
		return myActuators;
	}
	
	public void removeSensor(){
		mySensor="";
	}
	
	public void removeActuator(String addr){
		ListIterator<String> it=myActuators.listIterator();
		while(it.hasNext()){
			if(it.next().equals(addr))
				myActuators.remove(addr);
		}
	}

}