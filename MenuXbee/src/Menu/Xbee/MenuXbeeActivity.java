package Menu.Xbee;

import android.app.Activity;
import android.os.Bundle;

public class MenuXbeeActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean a = true;
		if (a) {
			InterfaceXbee ix = new InterfaceXbee(this);
			setContentView(ix);
			availableNetworks();
		} else {
			new AlertMessage(this, ErrorTypes.COORDINATOR_NOT_DETECTED);
			//this.finish();
		}

	}

	private void availableNetworks() {
		// TODO Auto-generated method stub

	}
	
}