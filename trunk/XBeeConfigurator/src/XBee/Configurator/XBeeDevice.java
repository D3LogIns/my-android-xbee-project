package XBee.Configurator;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;

import android.util.Log;

public class XBeeDevice implements Serializable {

	private static final long serialVersionUID = 1375942286432721341L;
	private String sh;
	private String sl;
	private byte[] shByte;
	private byte[] slByte;
	private byte[] addressByte;
	private String address;
	private String type;
	private String ss;
	private LinkedList<String> myActuators = new LinkedList<String>();
	private LinkedList<byte[]> myActuatorsByte = new LinkedList<byte[]>();
	private LinkedList<String> myActuatorsType = new LinkedList<String>();
	
	private LinkedList<String> mySensor = new LinkedList<String>();
	private LinkedList<byte[]> mySensorByte = new LinkedList<byte[]>();
	//private String mySensor = "";
	
	//private byte[] mySensorByte;

	public XBeeDevice(String a, String t, String s) {
		this.address = a;
		this.type = t;
		this.ss = s;

	}

	public XBeeDevice(String sh, String sl, String type, String signal_strength) {
		this.sh = sh;
		this.sl = sl;
		this.type = type;
		this.ss = signal_strength;
		this.address = this.sh + " " + this.sl;
	}

	public XBeeDevice(String sh, String sl, String type,
			String signal_strength, byte[] shByte, byte[] slByte) {
		this.sh = sh;
		this.sl = sl;
		this.type = type;
		this.ss = signal_strength;
		this.shByte = shByte;
		this.slByte = slByte;
		
		if(this.sh.length()==6){
			this.sh="00"+this.sh;
		}else if(this.sh.length()==7){
			this.sh="0"+this.sh;
		}
		if(this.sl.length()==6){
			this.sl="00"+this.sl;
		}else if(this.sl.length()==7){
			this.sl="0"+this.sl;
		}
		
		this.address = this.sh + " " + this.sl;

		
		this.addressByte = new byte[shByte.length + slByte.length];
		for (int i = 0; i < (addressByte.length / 2); i++) {
			addressByte[i] = shByte[i];
			addressByte[4 + i] = slByte[i];
		}
	}

	public void setActuator(String addr) {
		myActuators.add(addr);
	}

	public void setActuator(byte[] addr) {
		myActuatorsByte.add(addr);
	}
	
	public void setActuatorType(String type){
		myActuatorsType.add(type);
	}

	public void setActuators(LinkedList<String> actuators) {
		myActuators = actuators;
	}

//	public void setSensor(String addr) {
//		//mySensor = addr;
//		mySensor.add(addr);
//	}
//
//	public void setSensorByte(byte[] addr) {
//		//mySensorByte = addr;
//		mySensorByte.add(addr);
//	}

	public String getSH() {
		return this.sh;
	}

	public String getSL() {
		return this.sl;
	}

	public byte[] getShByte() {
		return this.shByte;
	}

	public byte[] getSlByte() {
		return this.slByte;
	}

	public String getAddress() {
		return this.address;
	}

	public byte[] getAddressByte() {
		return this.addressByte;
	}

	public String getType() {
		return this.type;
	}

	public String getSignalStrength() {
		return this.ss;
	}

//	public String getMySensor() {
//		return mySensor;
//	}
//
//	public byte[] getMySensorByte() {
//		return mySensorByte;
//	}
	
	public LinkedList<String> getMySensor() {
		return mySensor;
	}

	public LinkedList<byte[]> getMySensorByte() {
		return mySensorByte;
	}
	

	public LinkedList<String> getMyActuators() {
		return myActuators;
	}

	public LinkedList<byte[]> getMyActuatorsByte() {
		return myActuatorsByte;
	}
	
	public LinkedList<String> getMyActuatorsType(){
		return myActuatorsType;
	}

	public void removeSensor() {
//		mySensor = "";
//		mySensorByte = null;
		mySensor.clear();
		mySensorByte.clear();
		
	}

	public void removeActuator(String addr) {
		ListIterator<String> it = myActuators.listIterator();
		while (it.hasNext()) {
			if (it.next().equals(addr))
				myActuators.remove(addr);
		}
	}

	public void removeActuator(byte[] addrByte, String addrString) {
		// ListIterator<String> it=myActuators.listIterator();
		// while(it.hasNext()){
		// if(it.next().equals(addrString))
		// myActuators.remove(addrString);
		// }
		
		
		

		try {
			int pos = this.myActuators.indexOf(addrString);
			this.myActuators.remove(addrString);
			//this.myActuatorsByte.remove(addrByte);
			this.myActuatorsByte.remove(pos);
			this.myActuatorsType.remove(pos);
		} catch (Exception e) {

		}

	}

}
