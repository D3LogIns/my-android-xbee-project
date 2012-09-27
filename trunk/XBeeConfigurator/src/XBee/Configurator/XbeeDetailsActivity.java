package XBee.Configurator;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class XbeeDetailsActivity extends Activity {

	private TableLayout xbeeDevices;
	private TableLayout associatedDevices;
	private AuxiliarXBee xbee;
	private String sType = "";
	private Context c = this;
	AlertMessage alert;
	int position;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.xbeedetails);
		this.inicialization();
	}

	private void inicialization() {
		position = this.getIntent().getExtras().getInt("position");

		xbee = (AuxiliarXBee) this.getIntent().getExtras().getSerializable("auxiliarXbee");

		alert = new AlertMessage(c, xbee);

		
//		TEXTVIEWS INICIALIZATION
		TextView addr = (TextView) findViewById(R.id.tvAddress);
		TextView type = (TextView) findViewById(R.id.tvType);
		TextView list = (TextView) findViewById(R.id.tvListOf);
		TextView associated=(TextView) findViewById(R.id.tvAssociatedDevices);
		TextView mySensor=(TextView) findViewById(R.id.tvMySensor);
		
		
		if(xbee.getType(position).equals(this.getString(R.string.actuator)) && !xbee.getMySensor(position).equals("")){
			associated.setText("My sensor");
			mySensor.setText(xbee.getMySensor(position));
			associated.setVisibility(0);
			mySensor.setVisibility(0);
			
		}else if(xbee.getType(position).equals(this.getString(R.string.sensor)) && xbee.getMyActuators(position).size()>0){
			associated.setText("My Actuators");
			associated.setVisibility(0);
			associatedDevices= (TableLayout) this.findViewById(R.id.associatedDevicesTable);
			
			populateAssociatedDevicesTable();
		}

		xbeeDevices = (TableLayout) this.findViewById(R.id.devicesTable);

		addr.setText(xbee.getAddress(position));

		type.setText(xbee.getType(position));

		if (xbee.getType(position).equals(this.getString(R.string.sensor))) {
			list.setText(this.getString(R.string.listOfActuators));
			sType = this.getString(R.string.actuator);

		} else if (xbee.getType(position).equals(
				this.getString(R.string.actuator))) {
			list.setText(this.getString(R.string.listOfSensors));
			sType = this.getString(R.string.sensor);

		} else {
			list.setText(this.getString(R.string.listOfChilds));
			sType = "all";
		}

		populateTableXBeeDevices();
	}

	private void populateTableXBeeDevices() {

		for (int i = 0; i != xbee.getListSize(); i++) {
			if (xbee.getType(i).equals(sType)) {
				TableRow r = new TableRow(this);
				final TextView a = new TextView(this);
				TextView ss = new TextView(this);

				final int pos = i;

				a.setTextAppearance(c, android.R.style.TextAppearance_Large);
				a.setText(xbee.getAddress(i));
				a.setId(i);
				a.setClickable(true);

				ss.setText(xbee.getSignalStrength(i));

				a.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						/*
						 * 1- If the user selects an actuator from the list, the first condition will be true
						 * 2- If the user selects a sensor from the list, the second condition will be true
						 */
						
						if (xbee.getType(pos).equals(c.getString(R.string.actuator))){
							
							xbee=alert.newMessage(MessageType.SET_ACTUATOR, xbee.getAddress(position), a.getText().toString());
						
						}else if (xbee.getType(pos).equals(c.getString(R.string.sensor))){		
							xbee=alert.newMessage(MessageType.SET_SENSOR, a.getText().toString(), xbee.getAddress(position));
						}
					}
				});

				r.addView(a);
				r.addView(ss);
				xbeeDevices.addView(r);

			}
		}
	}
	
	
	private void populateAssociatedDevicesTable(){
		for(int i=0;i!=xbee.getMyActuators(position).size();i++){
			TableRow r=new TableRow(this);
			final TextView a=new TextView(this);
			
			a.setClickable(true);
			a.setTextAppearance(c, android.R.style.TextAppearance_Large);
			a.setText(xbee.getMyActuators(position).get(i));
			
			a.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					
					xbee=alert.newMessage(MessageType.REMOVE_ACTUATOR, xbee.getAddress(position), a.getText().toString());
				}
				
			});
			
			r.addView(a);
			associatedDevices.addView(r);
		}
	}
	
	
	public void onBackPressed() {
		Bundle b = new Bundle();
		b.putSerializable("auxiliarXbee", xbee);
		
//		if(aux.getNumberOfActuators()>0 && aux.getSensor()!=null){
//			b.putSerializable("sensorAddress", aux.getSensor());
//			b.putSerializable("actuatorsList", aux.getActuators());
//		}
			

//		if(xbee.getMyActuators(position)!=null){
//			b.putSerializable("auxiliarXbee", xbee);
//			b.putSerializable("SensorsAndActuators", sa);
//		}
		
//		b.putSerializable("auxiliar", xbee);
		
		Intent mIntent = new Intent();
		mIntent.putExtras(b);
		mIntent.putExtra("position", position);
        setResult(RESULT_OK, mIntent);
		
		this.finish();
	}

	
}
