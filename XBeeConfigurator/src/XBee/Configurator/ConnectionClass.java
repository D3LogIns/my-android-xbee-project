package XBee.Configurator;

import java.util.LinkedList;
import java.util.Random;
import android.content.Context;
import android.util.Log;

public class ConnectionClass {
	

	private LinkedList<XBeeDevice> xbee = new LinkedList<XBeeDevice>();

	Context c;
	
	int NUMBER_OF_DEVICES= 20;

	public ConnectionClass(Context c) {
		this.c = c;
	}

	public void searchXBeeDevices() {
		this.clearList();
		
		Random r = new Random();
		int numXbee = (r.nextInt(NUMBER_OF_DEVICES));

		String sh="142A3E5F";
		String sl = "122D4B";
		byte[] shB={0x14, 0x2A,0x3E,0x5f};
		byte[] slB={0x12,0x2D, 0x4B, 0x0};
		int type = 3;
		String sType = "UNDEFINED";
		String ss = "DEFAULT";

		for (int i = 0; i < numXbee; i++) {

			type = r.nextInt(10);
			
			switch (type) {
			case 0:
				sType = c.getString(R.string.coordinator);
				break;
			case 1:
				sType = c.getString(R.string.router);
				break;
			case 2:
				sType = c.getString(R.string.routerMotionSensor);
				break;
			case 3:
				sType = c.getString(R.string.routerLuminanceSensor);
				break;
			case 4:
				sType = c.getString(R.string.sensor);
				break;
			case 5:
				sType = c.getString(R.string.motionSensor);
				break;
			case 6:
				sType = c.getString(R.string.luminanceSensor);
				break;
			case 7:
				sType = c.getString(R.string.actuator);
				break;
			case 8:
				sType=c.getString(R.string.actuatorMotion);
				break;
			case 9:
				sType=c.getString(R.string.actuatorLuminance);
				break;
			default:
				sType = c.getString(R.string.unknown);
				break;

			}

			switch (r.nextInt(4)) {
			case 0:
				ss = c.getString(R.string.VeryGood);
				break;
			case 1:
				ss = c.getString(R.string.Good);
				break;
			case 2:
				ss = c.getString(R.string.Medium);
				break;
			case 3:
				ss = c.getString(R.string.Weak);
				break;
			case 4:
				ss = c.getString(R.string.VeryWeak);
				break;
			}

			String s="";
			
			if(i==10){
				s="0A";
			} else if(i==11){
				s="0B";
			} else if(i==12){
				s="0C";
			} else if(i==13){
				s="0D";
			} else if(i==14){
				s="0E";
			} else if(i==15){
				s="0F";
			}else if(i>=0 && i<10)
				s="0"+String.valueOf(i);
			else
				s=String.valueOf(i);

			
			slB[3]=Byte.valueOf(String.valueOf(i));
			xbee.add(new XBeeDevice(sh,sl + s, sType, ss, shB, slB));

		}
	}

	public LinkedList<XBeeDevice> getList() {
		return xbee;
	}
	
	public void clearList(){
		xbee.clear();
	}
	
}
