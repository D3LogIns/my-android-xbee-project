package XBee.Configurator;

import java.util.LinkedList;
import java.util.Random;

import android.content.Context;

public class ConnectionClass {

	private LinkedList<FakeXBee> xbee = new LinkedList<FakeXBee>();

	Context c;

	public ConnectionClass(Context c) {
		this.c = c;
	}

	public void searchXBeeDevices() {
		this.clearList();
		
		Random r = new Random();
		int numXbee = (r.nextInt(11));

		String address = "142AFC2D00C";
		int type = 3;
		String sType = "UNDEFINED";
		String ss = "DEFAULT";

		for (int i = 0; i < numXbee; i++) {

			type = r.nextInt(3);

			switch (type) {
			case 0:
				sType = c.getString(R.string.router);
				break;
			case 1:
				sType = c.getString(R.string.sensor);
				break;
			case 2:
				sType = c.getString(R.string.actuator);
				break;
			}

			switch (r.nextInt(5)) {
			case 0:
				ss = "Excelente";
				break;
			case 1:
				ss = "Muito Bom";
				break;
			case 2:
				ss = "Bom";
				break;
			case 3:
				ss = "MŽdio";
				break;
			case 4:
				ss = "Fraco";
				break;
			case 5:
				ss = "Muito Fraco";
			}

			xbee.add(new FakeXBee(address + i, sType, ss));

		}
	}

	public LinkedList<FakeXBee> getList() {
		return xbee;
	}
	
	public void clearList(){
		xbee.clear();
	}

}
