package XBee.Configurator;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;

public class XBeeDevice implements Serializable{


	private static final long serialVersionUID = 1375942286432721341L;
	private String sh;
	private String sl;
	private byte[] shByte;
	private byte[] slByte;
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
		this.type=type;
		this.ss=signal_strength;
	}
	
	public XBeeDevice(String sh, String sl, String type, String signal_strength, byte[] shByte, byte[] slByte){
		this.sh=sh;
		this.sl=sl;
		this.type=type;
		this.ss=signal_strength;
		this.shByte=shByte;
		this.slByte=slByte;
	}
	
	public void setActuator(String addr){
		myActuators.add(addr);
	}
	
	public void setSensor(String addr){
		mySensor=addr;
	}
	
	
	public String getSH(){
		return this.sh;
	}
	
	public String getSL(){
		return this.sl;
	}
	
	public byte[] getShByte(){
		return this.shByte;
	}
	
	
	public byte[] getSlByte(){
		return this.slByte;
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
