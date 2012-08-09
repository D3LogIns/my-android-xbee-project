package XBee.Configurator;

import java.io.Serializable;
import java.util.LinkedList;

public class AuxiliarXBee implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6963040436481928378L;
	
	LinkedList<XBeeDevice> xbee= new LinkedList<XBeeDevice>();
	
	
//	Constructors
	public AuxiliarXBee(LinkedList<XBeeDevice> xbee){
		this.xbee=xbee;
	}
	
	
	public AuxiliarXBee(){
		
	}
	
//	Methods
	public void setList(LinkedList<XBeeDevice> xbee){
		this.xbee=xbee;
	}
	
	public void clearList(){	
		xbee.clear();
	}
	
	public int getListSize(){
		return xbee.size();
	}
	
	public String getAddress(int pos){
		return xbee.get(pos).getAdress();
	}
	
	public String getType(int pos){
		return xbee.get(pos).getType();
	}
	
	public String getSignalStrength(int pos){
		return xbee.get(pos).getSignalStrength();
	}
	
	public LinkedList<String> getActuators(int position){
			return xbee.get(position).getMyActuators();
	}
	
	public LinkedList<XBeeDevice> getList(){
		return xbee;
	}
	
	public int getNumberOfXBeeType(String s){
		int count=0;
		
		for(int i=0; i!=xbee.size(); i++)
			if(this.getType(i).equals(s))
				count++;
		
		return count;
	}
	
	public String getMySensor(int position){
		return xbee.get(position).getMySensor();
	}
	
	public LinkedList<String> getMyActuators(int position){
		return xbee.get(position).getMyActuators();
	}
	
	
	public void associateActuatorToSensor(String addrActuator, String addrSensor) {
		
		for(int i=0; i<xbee.size(); i++){
			if(xbee.get(i).getAdress().equals(addrSensor)){
				xbee.get(i).setActuator(addrActuator);
				
			}else if(xbee.get(i).getAdress().equals(addrActuator)){
				xbee.get(i).setSensor(addrSensor);
			}
		}
	}
	
	public void removeActuatorFromSensor(String addrActuator, String addrSensor){
		
		for(int i=0; i<xbee.size(); i++){
			if(xbee.get(i).getAdress().equals(addrSensor) && xbee.get(i).getMyActuators().size()>0){
				xbee.get(i).removeActuator(addrActuator);
				break;		
			}
		}
	}

}
