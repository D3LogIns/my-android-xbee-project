package Menu.Xbee;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class InterfaceXbeeDetails extends ScrollView {

	private Context c;
	private ConnectionClass cc;
	private int position;

	private TableLayout tlMain;
	private TableLayout tlDevice;

	private LinearLayout ll;

	TextView tvText;

	public InterfaceXbeeDetails(Context context, ConnectionClass cc,
			int position) {
		super(context);

		this.c = context;
		this.cc = cc;
		this.position = position;

		this.initializeGlobalVars();

		if (cc.getType(position).equals("Router"))
			this.creationRouter();
		else if (cc.getType(position).equals("Sensor"))
			this.creationSensor();
		else if (cc.getType(position).equals("Actuator"))
			this.creationActuator();

	}

	private void initializeGlobalVars() {
		ll = new LinearLayout(c);
		ll.setOrientation(LinearLayout.VERTICAL);

		// TABELAS
		tlMain = new TableLayout(c);
		tlMain.setColumnStretchable(0, true);
		tlMain.setColumnStretchable(1, true);

		tlDevice = new TableLayout(c);
		for (int i = 0; i < 3; i++)
			tlDevice.setColumnStretchable(i, true);

		// TEXTVIEWS
		TextView tvMyAdress = new TextView(c);
		TextView tvMyType = new TextView(c);

		TextView tvmyadd = new TextView(c);
		TextView tvmyty = new TextView(c);

		tvText = new TextView(c);

		tvmyadd.setText("My Address");
		tvmyty.setText("My Type");

		tvMyAdress.setText(cc.getAddress(position));
		tvMyType.setText(cc.getType(position));

		tvmyadd.setHeight(50);
		tvmyty.setHeight(50);
		tvMyAdress.setHeight(50);
		tvMyType.setHeight(50);

		//tvText.setHeight(60);

		// TABLE ROWS
		TableRow rMyadd = new TableRow(c);
		TableRow rMytype = new TableRow(c);

		// ADDs

		this.addView(tlMain);
		tlMain.addView(ll);

		rMyadd.addView(tvmyadd);
		rMyadd.addView(tvMyAdress);

		rMytype.addView(tvmyty);
		rMytype.addView(tvMyType);

		tlMain.addView(rMyadd);
		tlMain.addView(rMytype);

		tlMain.addView(tvText);
		tlMain.addView(tlDevice);

	}

	private void creationActuator() {
		// TODO Auto-generated method stub
		tvText.setText("List of Sensors");

		this.populateList("Sensor");
	}

	private void creationRouter() {
		// TODO Auto-generated method stub
		tvText.setText("List of Childs");

	}

	private void creationSensor() {
		tvText.setText("List of Actuators");

		this.populateList("Actuator");
	}

	private void populateList(final String type) {
		for (int i = 0; i < cc.getListSize(); i++)
			if (cc.getType(i).equals(type)) {

				if (i == 0) {
					TableRow r0 = new TableRow(c);
					TextView a1 = new TextView(c);
					TextView s1 = new TextView(c);
					
					a1.setText("Address");
					s1.setText("Signal Strength");
					
//					a1.setHeight(30);
//					s1.setHeight(30);

					r0.addView(a1);
					r0.addView(s1);

					tlDevice.addView(r0);
				}
				TableRow r = new TableRow(c);
				final TextView addr = new TextView(c);
				TextView ss = new TextView(c);
				
				addr.setId(i);
				addr.setClickable(true);
				addr.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if(type.equals("Actuator"))
							new AlertMessage(c, cc,cc.getAddress(position), addr.getText().toString()).newMessage(MessageType.SET_ACTUATOR);
						
						else if(type.equals("Sensor"))
							new AlertMessage(c).newMessage(MessageType.SET_SENSOR);
						
					}
					
					
				});
				

				
				
				r.addView(addr);
				
				r.addView(ss);

				tlDevice.addView(r);

				addr.setText(cc.getAddress(i));
				ss.setText(cc.getSignalStrength(i));

			}
	}
}
