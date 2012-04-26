package XBee.Configurator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class XbeeDetailsActivity extends Activity{
	
	private TableLayout xbeeDevices;
	private AuxiliarXBee aux;
	private String sType="";
	private Context c=this;
	AlertMessage alert;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xbeedetails);
		
		alert=new AlertMessage(c);
		
		this.inicialization();
	}
	
	private void inicialization(){
		int position=this.getIntent().getExtras().getInt("position");
		
		aux=(AuxiliarXBee) this.getIntent().getExtras().getSerializable("auxiliar");
		
		TextView addr=(TextView) findViewById(R.id.tvAddress);
		TextView type=(TextView) findViewById(R.id.tvType);
		TextView list=(TextView) findViewById(R.id.tvListOf);
		
		xbeeDevices=(TableLayout) this.findViewById(R.id.devicesTable);
		
		addr.setText(aux.getAddress(position));
		
		type.setText(aux.getType(position));
		
		if(aux.getType(position).equals(this.getString(R.string.sensor))){
			list.setText(this.getString(R.string.listOfActuators));
			sType=this.getString(R.string.actuator);
		}else if(aux.getType(position).equals(this.getString(R.string.actuator))){
			list.setText(this.getString(R.string.listOfSensors));
			sType=this.getString(R.string.sensor);
		}else{
			list.setText(this.getString(R.string.listOfChilds));
			sType="all";
		}
		
		populateTableXBeeDevices();
	}
	
	private void populateTableXBeeDevices(){
		
		for(int i=0;i!=aux.getListSize();i++){
			if(aux.getType(i).equals(sType)){
				TableRow r=new TableRow(this);
				final TextView a=new TextView(this);
				TextView ss=new TextView(this);
				
				final int pos=i;
				
				a.setText(aux.getAddress(i));
				a.setId(i);
				a.setClickable(true);
				
				ss.setText(aux.getSignalStrength(i));
				
				a.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if(aux.getType(pos).equals(c.getString(R.string.actuator)))
							alert.newMessage(MessageType.SET_ACTUATOR);
						else if(aux.getType(pos).equals(c.getString(R.string.sensor)))
							alert.newMessage(MessageType.SET_SENSOR);
					}


				});
				
				r.addView(a);
				r.addView(ss);
				xbeeDevices.addView(r);
				
			}
		}
	}
}
