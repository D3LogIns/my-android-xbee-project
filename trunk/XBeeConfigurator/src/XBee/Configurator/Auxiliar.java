package XBee.Configurator;

import java.io.Serializable;
import java.util.LinkedList;

public class Auxiliar implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6963040436481928378L;
	
	LinkedList<FakeXBee> xbee= new LinkedList<FakeXBee>();
	
	public Auxiliar(LinkedList<FakeXBee> xbee){
		this.xbee=xbee;
	}
	
	public Auxiliar(){
		
	}
	
	public void setList(LinkedList<FakeXBee> xbee){
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
			return xbee.get(position).getActuators();
	}
	
	public LinkedList getList(){
		return xbee;
	}
	
	public void associateActuatorToSensor(String addrActuator, String addrSensor) {
		
		for(int i=0; i<xbee.size(); i++){
			if(xbee.get(i).getAdress().equals(addrSensor)){
				xbee.get(i).setActuator(addrActuator);
				break;
			}
		}		
	}
	
	public void removeActuatorFromSensor(String addrActuator, String addrSensor){
		
		for(int i=0; i<xbee.size(); i++){
			if(xbee.get(i).getAdress().equals(addrSensor) && xbee.get(i).getActuators().size()>0){
				xbee.get(i).removeActuator(addrActuator);
				break;		
			}
		}
	}
}
