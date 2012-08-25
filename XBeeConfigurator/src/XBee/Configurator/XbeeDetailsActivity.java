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
	private AuxiliarXBee aux;
	private String sType = "";
	private Context c = this;
	AlertMessage alert;
	int position;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		


		this.inicialization();
	}

	private void inicialization() {
		position = this.getIntent().getExtras().getInt("position");

		aux = (AuxiliarXBee) this.getIntent().getExtras()
				.getSerializable("auxiliar");

		alert = new AlertMessage(c, aux);

		
//		TEXTVIEWS INICIALIZATION
		TextView addr = (TextView) findViewById(R.id.tvAddress);
		TextView type = (TextView) findViewById(R.id.tvType);
		TextView list = (TextView) findViewById(R.id.tvListOf);
		TextView associated=(TextView) findViewById(R.id.tvAssociatedDevices);
		TextView mySensor=(TextView) findViewById(R.id.tvMySensor);
		
		
		if(aux.getType(position).equals(this.getString(R.string.actuator)) && !aux.getMySensor(position).equals("")){
			associated.setText("My sensor");
			mySensor.setText(aux.getMySensor(position));
			associated.setVisibility(0);
			mySensor.setVisibility(0);
			
		}else if(aux.getType(position).equals(this.getString(R.string.sensor)) && aux.getActuators(position).size()>0){
			associated.setText("My Actuators");
			associated.setVisibility(0);
			associatedDevices= (TableLayout) this.findViewById(R.id.associatedDevicesTable);
			
			populateAssociatedDevicesTable();
		}

		xbeeDevices = (TableLayout) this.findViewById(R.id.devicesTable);

		addr.setText(aux.getAddress(position));

		type.setText(aux.getType(position));

		if (aux.getType(position).equals(this.getString(R.string.sensor))) {
			list.setText(this.getString(R.string.listOfActuators));
			sType = this.getString(R.string.actuator);

		} else if (aux.getType(position).equals(
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

		for (int i = 0; i != aux.getListSize(); i++) {
			if (aux.getType(i).equals(sType)) {
				TableRow r = new TableRow(this);
				final TextView a = new TextView(this);
				TextView ss = new TextView(this);

				final int pos = i;


				a.setText(aux.getAddress(i));
				a.setId(i);
				a.setClickable(true);

				ss.setText(aux.getSignalStrength(i));

				a.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (aux.getType(pos).equals(c.getString(R.string.actuator)))
							aux = alert.newMessage(
									MessageType.SET_ACTUATOR,
									aux.getAddress(position),
									a.getText().toString());
						else if (aux.getType(pos).equals(c.getString(R.string.sensor)))
							aux = alert.newMessage(
									MessageType.SET_SENSOR,
									a.getText().toString(),
									aux.getAddress(position));
					}
				});

				r.addView(a);
				r.addView(ss);
				xbeeDevices.addView(r);

			}
		}
	}
	
	private void populateAssociatedDevicesTable(){
		for(int i=0;i!=aux.getMyActuators(position).size();i++){
			TableRow r=new TableRow(this);
			TextView a=new TextView(this);
			
			a.setClickable(true);
			a.setText(aux.getMyActuators(position).get(i));
			
			r.addView(a);
			associatedDevices.addView(r);
		}
	}
	
	
	public void onBackPressed() {
		Bundle b = new Bundle();
		b.putSerializable("auxiliar", aux);
		
		Intent mIntent = new Intent();
		mIntent.putExtras(b);
        setResult(RESULT_OK, mIntent);
		
		this.finish();
	}

	
}
