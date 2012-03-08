package Menu.Xbee;

import android.content.Context;
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

	private LinearLayout ll;

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

		tlMain = new TableLayout(c);
		tlMain.setColumnStretchable(0, true);
		tlMain.setColumnStretchable(1, true);

		TextView tvMyAdress = new TextView(c);
		TextView tvMyType = new TextView(c);

		TextView tvmyadd = new TextView(c);
		TextView tvmyty = new TextView(c);

		tvmyadd.setText("My Address");
		tvmyty.setText("My type");

		tvMyAdress.setText(cc.getAddress(position));
		tvMyType.setText(cc.getType(position));
		
		TableRow rMyadd=new TableRow(c);
		TableRow rMytype=new TableRow(c);

		this.addView(tlMain);
		tlMain.addView(ll);

		rMyadd.addView(tvmyadd);
		rMyadd.addView(tvMyAdress);
		
		rMytype.addView(tvmyty);
		rMytype.addView(tvMyType);
		
		tlMain.addView(rMyadd);
		tlMain.addView(rMytype);

	}

	private void creationActuator() {
		// TODO Auto-generated method stub

	}

	private void creationRouter() {
		// TODO Auto-generated method stub

	}

	private void creationSensor() {

	}
}
