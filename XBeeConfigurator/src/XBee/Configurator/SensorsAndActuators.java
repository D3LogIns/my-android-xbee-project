package XBee.Configurator;

import java.io.Serializable;
import java.util.LinkedList;

public class SensorsAndActuators implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3608403942121079485L;
	private byte sensorByte[]=null;
	private String sensorString="";
	private LinkedList<byte[]> actuatorsByte=new LinkedList<byte[]>();
	private LinkedList<String> actuatorsString=new LinkedList<String>();

	/*
	 * Constructors
	 */
	
	public SensorsAndActuators(){
		
	}
	
	public SensorsAndActuators(byte address){
		
	}
	
	

	/*
	 * Method for defining a sensor and a list of actuators
	 */
	public void setSensorsAndActuators(byte[] sensor, LinkedList<byte[]> actuators){
		this.sensorByte=sensor;
		this.actuatorsByte=actuators;
	}
	
	/*
	 * Method for defining a sensor, although it receives
	 * a byte this method converts the byte address into
	 * String
	 */
	public void setSensorByte(byte[] sensor){
		this.sensorByte=sensor;
//		this.sensorString=this.convertByteToString(sensor);
		this.sensorString=new AuxiliarMethods().convertByteToString(sensor);
	}
	
	
	/*
	 * Methods for defining a list of actuators
	 */
	
	public void setActuatorsByte(LinkedList<byte[]> actuators){
		this.actuatorsByte=actuators;
		for(int i=0; i<actuatorsByte.size(); i++){
			this.actuatorsString.add(new AuxiliarMethods().convertByteToString(this.actuatorsByte.get(i)));
		}
	}
	
	public void setActuatorsString(LinkedList<String> actuators){
		this.actuatorsString=actuators;
	}
	
	/*
	 * Method for adding an actuator
	 */
	public void addActuatorByte(byte[] address){
		this.actuatorsByte.add(address);
//		this.actuatorsString.add(this.convertByteToString(address));
		this.actuatorsString.add(new AuxiliarMethods().convertByteToString(address));
	}
	
	/*
	 * Method for returning the sensor address
	 */
	public byte[] getSensorByte(){
		return this.sensorByte;
	}
	
	public String getSensorString(){
		return this.sensorString;
	}
	
	/*
	 * Methods for returning the actuator address from the a position of the list
	 */
	public byte[] getActuatorByte(int position){
		return this.actuatorsByte.get(position);
	}
	
	public String getActuatorString(int position){
		return this.actuatorsString.get(position);
	}
	
	/*
	 * Method for returning the actuators list
	 */
	public LinkedList<byte[]> getActuatorsByte() {
		return this.actuatorsByte;
	}
	
	public LinkedList<String> getActuatorsString(){
		return this.actuatorsString;
	}
	
	/*
	 * Method for returning the actuators list size
	 */
	public int listSize(){
		return this.actuatorsByte.size();
	}
	
	/*
	 * Method for clearing the data
	 */
	public void clearTable(){
		this.sensorByte=null;
		this.sensorString="";
		this.actuatorsByte.clear();
		this.actuatorsString.clear();
	}
	
//	private String convertByteToString(byte[] b){
//		StringBuilder textBuilder = new StringBuilder();
//		for(int i=0; i<b.length; i++){
//			textBuilder.append(Integer.toHexString(b[i] & 0xFF));
//			if(i==7)
//				textBuilder.append(" ");
//		}
//		return textBuilder.toString();
//	}
}
