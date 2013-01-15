package XBee.Configurator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class XbeeDetailsActivity extends Activity {

	private static final String TAG = XbeeDetailsActivity.class.getSimpleName();

	private TableLayout xbeeDevices;
	private TableLayout associatedDevices;
	// private TextView mySensor;
	// TextView associated;
	private AuxiliarXBee auxXbee;
	private XBeeDevice xbee;
	private Context c = this;
	AlertMessage alert;
	int position;
	boolean configsChanged = false;
	private Thread thread;

	private byte[] panId;

	private int LIMIT_ACTUATORS = 2;

	// COLOR VARIABLES
	private String green = "#7EFFA6";
	private String black = "#000000";
	private String light_grey = "#F3F5E7";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// The position of the selected XBee in XBeeConfiguratorActivity
		position = this.getIntent().getExtras().getInt("position");

		auxXbee = (AuxiliarXBee) this.getIntent().getExtras()
				.getSerializable("auxiliarXbee");

		alert = new AlertMessage(c, auxXbee);

		xbee = auxXbee.getDevice(position);

		this.setContentView(R.layout.xbeedetails);
		this.inicialization();

	}

	private void inicialization() {

		// TEXTVIEWS INICIALIZATION
		TextView addr = (TextView) findViewById(R.id.tvAddress);
		TextView type = (TextView) findViewById(R.id.tvType);
		TextView list = (TextView) findViewById(R.id.tvListOf);
		final EditText etPan = (EditText) findViewById(R.id.editPanDetails);
		final EditText etAssociate = (EditText) findViewById(R.id.editAssociateDeviceDetails);
		final EditText etDesassociate = (EditText) findViewById(R.id.editDesassociateDeviceDetails);
		// associated = (TextView) findViewById(R.id.tvAssociatedDevices);
		// mySensor = (TextView) findViewById(R.id.tvMySensor);

		// TABLES INICIALIZATION
		associatedDevices = (TableLayout) findViewById(R.id.associatedDevicesTableDetails);
		xbeeDevices = (TableLayout) this.findViewById(R.id.devicesTable);

		// ROWS INICIALIZATION
		TableRow rAssociate = (TableRow) findViewById(R.id.rowAssociateDeviceDetails);
		TableRow rDesassociate = (TableRow) findViewById(R.id.rowDesassociateDeviceDetails);

		// BUTTONS INICIALIZATION
		final Button bOkPanId = (Button) findViewById(R.id.bOKPanDetails);
		final Button bAssociate = (Button) findViewById(R.id.bOK_AssociateDetails);
		final Button bDesassociate = (Button) findViewById(R.id.bOK_DesassociateDetails);
		
		addr.setText(xbee.getAddress());

		type.setText(xbee.getType());

		// TextViews Listeners

		etPan.addTextChangedListener(new TextWatcher() {

			// METHOD THAT CHECKS IF THE TEXT IS CHANGED
			public void afterTextChanged(Editable s) {
				// IF TEXT SIZE IS HIGHER THAN 5, APPLICATIOAN LAUNCHES AN ERROR
				if (s.length() > 5) {
					alert.newMessage(MessageType.TEXT_OUT_OF_BOUNDS);
					etPan.setText("");
					// IF TEXT SIZE IS HIGHER THAN 0, OK BUTTON TURNS ACTIVE
				} else if (s.length() > 0) {
					bOkPanId.setEnabled(true);
					// IF TEXT SIZE IS LOWER OR EQUAL TO 0, OK BUTTON TURNS
					// DEACTIVATED
				} else if (s.length() <= 0) {
					bOkPanId.setEnabled(false);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

		});

		etAssociate.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable e) {
				String s = e.toString();

				s = s.replaceAll(" ", "").trim();

				if (s.length() > 16) {
					alert.newMessage(MessageType.ADDRESS_OUT_OF_BOUNDS);
					etAssociate.setText("");
				} else if (s.length() <= 0) {
					bAssociate.setEnabled(false);
				} else if (s.length() > 0) {
					bAssociate.setEnabled(true);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

		});

		etDesassociate.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable e) {
				String s = e.toString();
				s = s.replaceAll(" ", "").trim();

				if (s.length() > 16) {
					alert.newMessage(MessageType.ADDRESS_OUT_OF_BOUNDS);
					etDesassociate.setText("");
				} else if (s.length() <= 0) {
					bDesassociate.setEnabled(false);
				} else if (s.length() > 0) {
					bDesassociate.setEnabled(true);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

		});

		/*
		 * BUTTONS LISTENERS
		 */

		// OK PAN ID BUTTON
		bOkPanId.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (inBoundsPanID(etPan.getText().toString())) {
					changeXbeePanID(Integer.parseInt(etPan.getText().toString()));

				} else
					alert.newMessage(MessageType.PAN_ID_OUT_OF_BOUNDS);

				etPan.setText("");

			}

			private boolean inBoundsPanID(String string) {
				if (Integer.parseInt(string) > 50000 || Integer.parseInt(string) <= 0)
					return false;
				return true;
			}

			private void changeXbeePanID(int id) {

				panId= new AuxiliarMethods().intToByteArray(id);
//				byte idByte[] = new AuxiliarMethods().intToByteArray(id);
//				panId = idByte;

			}
		});

		// ASSOCIATE BUTTON
		bAssociate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String s = etAssociate.getText().toString().toUpperCase();
				s = s.replace(" ", "");
				etAssociate.setText("");
				
				String type=c.getString(R.string.actuator);

				if (s.length() >= 14) {
					try {
						// byte[] b = new
						// AuxiliarMethods().convertStringAddressToByte(s);

						String t = s.substring(8);
						s = s.replace(t, " ");
						s = s + t;

						try {
							auxXbee = alert.newMessage(
									MessageType.SET_ACTUATOR,
									xbee.getAddress(), s, type);
						} catch (Exception e) {

						}
					} catch (Exception e) {
						alert.newMessage(MessageType.ADDRESS_NOT_ACCEPTABLE);
					}
				} else
					alert.newMessage(MessageType.ADDRESS_SHORT_LENGTH);

			}

		});

		// DESASSOCIATE BUTTON

		bDesassociate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String s = etDesassociate.getText().toString().toUpperCase();
				s.replaceAll(" ", "");
				Log.d("debug", s);

				etDesassociate.setText("");

				if (s.length() >= 14) {
					try {
						// byte[] b = new
						// AuxiliarMethods().convertStringAddressToByte(s);

						String t = s.substring(8);
						s = s.replace(t, "");
						s = s + t;
						Log.d("debug", s);

						try {
							auxXbee = alert.newMessage(
									MessageType.REMOVE_ACTUATOR,
									xbee.getAddress(), s, null);
						} catch (Exception e) {

						}
					} catch (Exception e) {
						alert.newMessage(MessageType.ADDRESS_NOT_ACCEPTABLE);
					}
				} else
					alert.newMessage(MessageType.ADDRESS_SHORT_LENGTH);
			}

		});

		//if it's some sort of sensor
		if (xbee.getType().equals(this.getString(R.string.sensor))
				|| xbee.getType().equals(this.getString(R.string.luminanceSensor))
				|| xbee.getType().equals(this.getString(R.string.motionSensor))
				|| xbee.getType().equals(getString(R.string.routerLuminanceSensor))
				|| xbee.getType().equals(getString(R.string.routerMotionSensor))) {

			rAssociate.setVisibility(View.VISIBLE);
			rDesassociate.setVisibility(View.VISIBLE);

			if (xbee.getMyActuators().size() > 0
					&& (xbee.getMyActuators().size() == xbee
							.getMyActuatorsByte().size())) {
				populateAssociatedDevicesTable();
			}

			

			list.setText(this.getString(R.string.listOfActuators));

			populateTableXBeeDevices();
		}

		thread = new Thread(null, listener, TAG);
		thread.start();
	}

	private void populateTableXBeeDevices() {

		int cont = 0;
		for (int i = 0; i != auxXbee.getListSize(); i++) {
			if (auxXbee.getType(i).equals(getString(R.string.actuator))
					|| auxXbee.getType(i).equals(getString(R.string.actuatorLuminance))
					|| auxXbee.getType(i).equals(getString(R.string.actuatorMotion))
					) {
				TableRow r = new TableRow(this);
				final TextView a = new TextView(this);
				final TextView t=	new TextView(this);
				TextView ss = new TextView(this);

				a.setTextAppearance(c, android.R.style.TextAppearance_Large);
				a.setText(auxXbee.getAddress(i));
				a.setId(i);
				a.setClickable(true);
				
				t.setTextAppearance(c, android.R.style.TextAppearance_Large);
				t.setText(auxXbee.getType(i));

				ss.setTextAppearance(c, android.R.style.TextAppearance_Large);
				ss.setText(auxXbee.getSignalStrength(i));

				if (cont % 2 == 0)
					r.setBackgroundColor(Color.parseColor(green));
				else
					r.setBackgroundColor(Color.parseColor(light_grey));

				a.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

							int size = xbee.getMyActuators().size();

							if (size < LIMIT_ACTUATORS) {
								auxXbee = alert.newMessage(
										MessageType.SET_ACTUATOR,
										xbee.getAddress(),
										a.getText().toString(),
										t.getText().toString());

								// populateAssociatedDevicesTable();
							} else
								alert.newMessage(MessageType.ACTUATORS_LIMIT_REACHED);

						
					}
				});

				r.setPadding(0, 10, 0, 10);

				a.setTextColor(Color.parseColor(black));
				t.setTextColor(Color.parseColor(black));
				ss.setTextColor(Color.parseColor(black));
				
				r.addView(a);
				r.addView(t);
				r.addView(ss);
				xbeeDevices.addView(r);

				cont++;

			}
		}
	}

	private void populateAssociatedDevicesTable() {

		associatedDevices.removeAllViews();
		TextView text = new TextView(this);

		if (xbee.getMyActuators().size() != 0) {

			text.setText(this.getString(R.string.myActuators));
			text.setTextAppearance(c, android.R.style.TextAppearance_Large);
			associatedDevices.addView(text);

			for (int i = 0; i != auxXbee.getMyActuators(position).size(); i++) {
				TableRow r = new TableRow(this);
				final TextView myActuator = new TextView(this);
				TextView type=new TextView(this);

				myActuator.setClickable(true);
				myActuator.setTextAppearance(c,
						android.R.style.TextAppearance_Large);
				myActuator.setText(auxXbee.getMyActuators(position).get(i));

				myActuator.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						auxXbee = alert.newMessage(MessageType.REMOVE_ACTUATOR,
								xbee.getAddress(), myActuator.getText()
										.toString(), null);
					}
				});
				
				type.setText(auxXbee.getMyActuatorsType(position).get(i));
				type.setTextAppearance(c,
						android.R.style.TextAppearance_Large);

				r.setPadding(0, 10, 0, 10);
				myActuator.setTextColor(Color.parseColor(black));
				text.setTextColor(Color.parseColor(black));
				type.setTextColor(Color.parseColor(black));

				// r.addView(text);
				r.addView(myActuator);
				r.addView(type);
				associatedDevices.addView(r);
			}
			associatedDevices.setVisibility(View.VISIBLE);
		} else {
			associatedDevices.setVisibility(View.GONE);
		}

	}

	public void onBackPressed() {
		Bundle b = new Bundle();
		b.putSerializable("auxiliarXbee", auxXbee);

		Intent mIntent = new Intent();
		mIntent.putExtras(b);

		if (panId != null) {
			byte panId_temp[]={panId[2], panId[3]};
			
			mIntent.putExtra("panId", panId_temp);
		}else{
			panId=new byte[6];
			mIntent.putExtra("panId", panId);
		}

		mIntent.putExtra("position", position);
		setResult(RESULT_OK, mIntent);

		this.finish();
	}

	Runnable listener = new Runnable() {

		@Override
		public void run() {
			int size = 0;
			while (true) {
				// if the device is some sort of sensor
				if (xbee.getType().equals(getString(R.string.sensor))
						|| xbee.getType().equals(getString(R.string.luminanceSensor))
						|| xbee.getType().equals(getString(R.string.motionSensor))
						|| xbee.getType().equals(getString(R.string.routerLuminanceSensor))
						|| xbee.getType().equals(getString(R.string.routerMotionSensor))
						) {

					if (auxXbee.getMyActuators(position).size() != size 
							&& auxXbee.getMyActuators(position).size()==auxXbee.getMyActuatorsByte(position).size() 
							&& auxXbee.getMyActuators(position).size()==auxXbee.getMyActuatorsType(position).size()) {

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								populateAssociatedDevicesTable();

							}
						});
						size = auxXbee.getMyActuators(position).size();

					} else if (auxXbee.getMyActuators(position).size() == 0) {

					}

					// if the device is an actuator
				}
			}
		}

	};

}
