package XBee.Configurator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class XbeeDetailsActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xbeedetails);
		
		
		int position=this.getIntent().getExtras().getInt("position");
		
		AuxiliarXBee aux=(AuxiliarXBee) this.getIntent().getExtras().getSerializable("auxiliar");
		
		TextView addr=(TextView) findViewById(R.id.tvAddress);
		TextView type=(TextView) findViewById(R.id.tvType);
		TextView list=(TextView) findViewById(R.id.tvListOf);
		
		addr.setText(aux.getAddress(position));
		
		type.setText(aux.getType(position));
		
		if(aux.getType(position).equals(this.getString(R.string.sensor)))
			list.setText(this.getString(R.string.listOfActuators));
		else if(aux.getType(position).equals(this.getString(R.string.actuator)))
			list.setText(this.getString(R.string.listOfSensors));
		else
			list.setText(this.getString(R.string.listOfChilds));
		
	}
}
