package XBee.Configurator;

import android.app.Activity;
import android.os.Bundle;


public class XbeeDetailsActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xbeedetails);
		
		int position=this.getIntent().getExtras().getInt("position");
		
		ConnectionClass cc=(ConnectionClass) this.getIntent().getExtras().getSerializable("connection");
		
		//InterfaceXbeeDetails ixd=new InterfaceXbeeDetails(this, cc, position);
        
        //setContentView(ixd);
	}
}
