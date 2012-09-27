package XBee.Configurator;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

public class XBeeConfiguratorActivity extends Activity {

	private String debug = "debug";

	// ARUINO-ANDROID COMMUNICATION VARIABLES
	private static final String TAG = XBeeConfiguratorActivity.class
			.getSimpleName();
	private PendingIntent mPermissionIntent;
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private boolean mPermissionRequestPending;
	private UsbManager mUsbManager;
	private UsbAccessory mAccessory;
	private ParcelFileDescriptor mFileDescriptor;
	private FileInputStream mInputStream;
	private FileOutputStream mOutputStream;

	// COMMANDS
	private static final byte SH_REQUEST = (byte) 0xA1;
	private static final byte SL_REQUEST = (byte) 0xA2;
	private static final byte SH_RESPONSE = (byte) 0xA3;
	private static final byte SL_RESPONSE = (byte) 0xA4;
	private static final byte NI_REQUEST = (byte) 0xA5;
	private static final byte NI_RESPONSE = (byte) 0xA6;
	private static final byte ID_REQUEST = (byte) 0xB1;
	private static final byte ID_RESPONSE = (byte) 0xB2;
	private static final byte ID_CHANGE_REQUEST = (byte) 0xB3;
	private static final byte ID_CHANGE_RESPONSE = (byte) 0xB4;
	private static final byte NODE_DISCOVERY_REQUEST = (byte) 0xD1;
	private static final byte NODE_DISCOVERY_RESPONSE = (byte) 0xD2;
	private static final byte SET_ACTUATOR_TO_SENSOR = (byte) 0xE1;
	private static final byte GET_ACTUATOR_FROM_SENSOR = (byte) 0xE2;
	private static final byte ASSOCIATE_DEVICE = (byte) 0xAD;
	private static final byte DESSASSOCIATE_DEVICE = (byte) 0xDA;

	private static final byte COMMAND_TEXT = 0xF;
	private static final byte TARGET_DEFAULT = 0xF;

	// COMPONTENTS VARIABLES
	TextView tvXbeeAddress;
	TextView tvPanID;
	TableLayout tlXBeeDevices;

	// CLASSES VARIABLES
	private AlertMessage alert;
	private ConnectionClass cc;
	private AuxiliarXBee auxXBee;
	private SensorsAndActuators sa;
	
	// COLOR VARIABLES
	private String green="#7EFFA6";
	private String black="#000000";
	private String light_grey="#F3F5E7";
			

	// OTHER VARIABLES
	private ProgressDialog progressDialog;
	private String shAddress = "sh";
	private String slAddress = "sl";
	private String panID = "id";

	final Context c = this;
	private LinkedList<XBeeDevice> xbee;
	private int oldID = 0;
	private int newID = 0;
	private Thread thread;

	private enum searchType {
		searchForConnectedDevice, searchForWirelessDevices, changePan
	};

	/**
	 * Called when the activity is first created.
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.registerAccessory();

		setContentView(R.layout.main);

		cc = new ConnectionClass(c);

		auxXBee = new AuxiliarXBee();

		alert = new AlertMessage(c);

		sa = new SensorsAndActuators();

		xbee = new LinkedList<XBeeDevice>();

		this.inicialization();

	}
	
	private void registerAccessory(){
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);
	}

	/**
	 * Called when the activity is resumed from its paused state and immediately
	 * after onCreate().
	 */
	@Override
	public void onResume() {
		super.onResume();

		if (mInputStream != null && mOutputStream != null) {
			return;
		}

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);

		if (accessory != null) {

			// Toast.makeText(c, "accessory: "+accessory.toString(),
			// Toast.LENGTH_LONG).show();

			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}

		} else {
			// Toast.makeText(c, "mAccessory is null",
			// Toast.LENGTH_LONG).show();
			Log.d(TAG, "mAccessory is null");
		}

	}

	/** Called when the activity is paused by the system. */
	@Override
	public void onPause() {
		super.onPause();
		// closeAccessory();
	}

	/**
	 * Called when the activity is no longer needed prior to being removed from
	 * the activity stack.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		closeAccessory();
		unregisterReceiver(mUsbReceiver);
	}

	/*
	 * ############################
	 * 
	 * METHODS FOR VISUALIZATION
	 * 
	 * #############################
	 */

	private void inicialization() {
		
		//progressDialog = new ProgressDialog(XBeeConfiguratorActivity.this, R.style.Theme_MyDialog);
		progressDialog = new ProgressDialog(c);

		/*
		 * XBEE DETECTED DEVICES TABLE
		 */
		tlXBeeDevices = (TableLayout) findViewById(R.id.tlXBeeDevices);

		/*
		 * BUTTONS INICIALIZATION
		 */
		final Button bOK = (Button) findViewById(R.id.bOKPan);
		Button bDetect = (Button) findViewById(R.id.bDetectDevices);
		Button bRefresh = (Button) findViewById(R.id.bRefresh);
		final Button bAssociate = (Button) findViewById(R.id.bOK_Associate);
		bOK.setEnabled(false);

		/*
		 * TEXT BOX'S INICIALIZATION
		 */

		final EditText etPan = (EditText) findViewById(R.id.editPan);
		tvXbeeAddress = (TextView) findViewById(R.id.tvCoordAdress);
		tvPanID = (TextView) this.findViewById(R.id.tvPanID);
		final EditText etAssociate = (EditText) findViewById(R.id.editAssociateDevice);
		
		
//		progressDialog=new ProgressDialog=new ProgressDialog(this, R.sty);
		

		/*
		 * #############################
		 * 
		 * TEXT BOX'S LISTENERS
		 * 
		 * #############################
		 */
	
		etPan.addTextChangedListener(new TextWatcher() {

			// METHOD THAT CHECKS IF THE TEXT IS CHANGED
			public void afterTextChanged(Editable s) {
				// IF TEXT SIZE IS HIGHER THAN 5, APPLICATIOAN LAUNCHES AN ERROR
				if (s.length() > 5) {
					alert.newMessage(MessageType.TEXT_OUT_OF_BOUNDS);
					etPan.setText("");
					// IF TEXT SIZE IS HIGHER THAN 0, OK BUTTON TURNS ACTIVE
				} else if (s.length() > 0)
					bOK.setEnabled(true);
				// IF TEXT SIZE IS LOWER OR EQUAL TO 0, OK BUTTON TURNS
				// DEACTIVATED
				else if (s.length() <= 0)
					bOK.setEnabled(false);
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
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

		});

		/*
		 * #########################
		 * 
		 * BUTTONS LISTENERS
		 * 
		 * ##########################
		 */

		// OK BUTTON
		bOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (inBoundsPanID(etPan.getText().toString())) {
					changeXbeePanID(Integer
							.parseInt(etPan.getText().toString()));

				} else
					alert.newMessage(MessageType.PAN_ID_OUT_OF_BOUNDS);

				etPan.setText("");

			}

			private boolean inBoundsPanID(String string) {
				if (Integer.parseInt(string) > 50000
						|| Integer.parseInt(string) <= 0)
					return false;
				return true;
			}

			private void changeXbeePanID(int id) {
				oldID = newID;
				newID = id;

				byte idByte[] = new AuxiliarMethods().intToByteArray(id);

				new SendInformationThread(ID_CHANGE_REQUEST, TARGET_DEFAULT,
						idByte).run();

				// new SimpleRequestThread(idChangeRequest, TARGET_DEFAULT,
				// Integer
				// .toString(id)).run();
				new LoadingScreen(4000, searchType.changePan).execute();

			}
		});

		// REFRESH BUTTON
		bRefresh.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

					retrieveXBeeAddress();

			}

		});

		// DETECT BUTTON
		bDetect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				xbee.clear();
				auxXBee.clearList();

				/*
				 * 
				 * METODOS PARA A CRIACAO DE NOS VIRTUAIS
				 */
				 cc.searchXBeeDevices();
				 tlXBeeDevices.removeAllViews();
				 xbee=cc.getList();
				 populateXbeeTable();

				/*
				 * METODOS NECESSARIOS PARA A DETECACAO DE NOS NA REDE
				 */
//				tlXBeeDevices.removeAllViews();
//
//				new SimpleRequestThread(NODE_DISCOVERY_REQUEST, TARGET_DEFAULT,
//						"1").run();
//				new LoadingScreen(10000, searchType.searchForWirelessDevices)
//						.execute();

			}

		});

		// ASSOCIATE BUTTON
		bAssociate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String s = etAssociate.getText().toString();
				etAssociate.setText("");
				Log.d(debug, "s.length= " + s.length());
				if (s.length() >= 14) {
					try {
						byte[] b = new AuxiliarMethods()
								.convertStringAddressToByte(s);

						try {
							new SendInformationThread(ASSOCIATE_DEVICE,
									TARGET_DEFAULT, b).run();
						} catch (Exception e) {

						}
					} catch (Exception e) {
						alert.newMessage(MessageType.ADDRESS_NOT_ACCEPTABLE);
					}
				} else
					alert.newMessage(MessageType.ADDRESS_SHORT_LENGTH);

			}

		});

	}

	/*
	 * ######################################
	 * 
	 * METHODS FOR THE XBeeDetails Activity
	 * 
	 * ######################################
	 */

	private void callXBeeDetails(int position) {
		Log.d(debug,
				"size: "
						+ String.valueOf(auxXBee.getMyActuators(position)
								.size()));
		/*
		 * Here the SensorsAndActuators class will be "populated" The reason of
		 * this is that this class will be used later to send to Arduino the
		 * associated devices without repeated values
		 * 
		 * 1- If the selected device is a sensor 2- If the selected device is an
		 * actuator
		 */
		sa.clearTable();
		if (auxXBee.getMyActuators(position).size() > 0) {
			sa.setActuatorsByte(auxXBee.getMyActuatorsByte(position));
			// sa.setActuatorsString(auxXBee.getMyActuators(position));
			sa.setSensorByte(auxXBee.getAddressByte(position));
		} else if (!auxXBee.getMySensor(position).equals("")) {
			sa.setSensorByte(auxXBee.getMySensorByte(position));
			sa.addActuatorByte(auxXBee.getAddressByte(position));
		}

		Intent i = new Intent(c, XbeeDetailsActivity.class);
		Bundle b = new Bundle();

		i.putExtra("position", position);

		/*
		 * The class AuxiliarXbee must be sent in order to be able to work with
		 * the linkedlist<XBeeDevice>. The android cannot send a 'raw'
		 * linkedlist
		 */
		b.putSerializable("auxiliarXbee", auxXBee);

		i.putExtras(b);

		this.startActivityForResult(i, MODE_PRIVATE);

	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		Bundle extras = intent.getExtras();

		auxXBee = (AuxiliarXBee) extras.getSerializable("auxiliarXbee");

		int position = extras.getInt("position");

		int size = auxXBee.getMyActuators(position).size();

		if (size > 0) {

			xbee = auxXBee.getList();

			for (int i = 0; i < size; i++)
				this.sendSetActuatorToSensor(retrieveByteAddress(auxXBee
						.getMyActuators(position).get(i)), auxXBee
						.getAddressByte(position));

		} else if (!auxXBee.getMySensor(position).equals("")) {
			this.sendSetActuatorToSensor(auxXBee.getAddressByte(position),
					retrieveByteAddress(auxXBee.getMySensor(position)));
		}

	}

	private void sendSetActuatorToSensor(byte[] addrAct, byte[] addrSens) {

	}

	private byte[] retrieveByteAddress(String addr) {
		int pos = -1;

		for (int i = 0; i < xbee.size(); i++)
			if (xbee.get(i).getAddress().equals(addr))
				pos = i;

		if (pos != -1)
			return xbee.get(pos).getAddressByte();
		else
			return null;
	}

	/*
	 * ################################
	 * 
	 * Methods for the USB Connection
	 * 
	 * #################################
	 */

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (ACTION_USB_PERMISSION.equals(action)) {

				synchronized (this) {
					UsbAccessory accessory = (UsbAccessory) intent
							.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						// Toast.makeText(c,
						// "permission denied for accessory: "+accessory,
						// Toast.LENGTH_LONG).show();
						Log.d(TAG, "permission denied for accessory "
								+ accessory);
					}
					mPermissionRequestPending = false;

				}

			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				// Toast.makeText(c, "DETACHED", Toast.LENGTH_LONG).show();
				UsbAccessory accessory = (UsbAccessory) intent
						.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
				if (accessory != null && accessory.equals(mAccessory))
					closeAccessory();

			}
		}
	};

	private void openAccessory(UsbAccessory accessory) {

		mFileDescriptor = mUsbManager.openAccessory(accessory);

		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			thread = new Thread(null, commRunnable, TAG);
			thread.start();
			// Toast.makeText(c, "accessory opened", Toast.LENGTH_LONG).show();
			Log.d(TAG, "accessory opened");
		} else {
			// Toast.makeText(c, "accessory open fail",
			// Toast.LENGTH_LONG).show();
			Log.d(TAG, "accessory open fail");
		}
	}

	private void closeAccessory() {
		try {
			if (mFileDescriptor != null) {
				// Toast.makeText(c, "ENTREI NO CLOSE ACCESSORY",
				// Toast.LENGTH_LONG).show();
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			// Toast.makeText(c, "ENTREI NO EXCEPTION DO CLOSE ACCESSORY",
			// Toast.LENGTH_LONG).show();
			mFileDescriptor = null;
			mAccessory = null;
		}
	}

	// Metodo que vai ler os dados
	Runnable commRunnable = new Runnable() {
		@Override
		public void run() {
			int ret = 0;
			byte[] buffer = new byte[255];
			while (ret >= 0) {
				try {
					ret = mInputStream.read(buffer);
				} catch (IOException e) {
					break;
				}

				switch (buffer[0]) {
				// case COMMAND_TEXT:
				case SH_REQUEST:
					break;
				case SL_REQUEST:
					break;
				case SH_RESPONSE:

					shAddress = new AuxiliarMethods().getData(buffer);
					break;

				case SL_RESPONSE:

					slAddress = new AuxiliarMethods().getData(buffer);
					break;

				case NI_REQUEST:
					break;
				case NI_RESPONSE:
					break;
				case ID_REQUEST:
					break;
				case ID_RESPONSE:

					// panID = new
					// AuxiliarMethods().getData(buffer).replaceAll("0", "");
					// remove the left zeros
					// panID = new
					// AuxiliarMethods().getData(buffer).replaceFirst("^0+(?!$)",
					// "");
					panID = String.valueOf(new AuxiliarMethods()
							.getDataInt(buffer));
					break;

				case ID_CHANGE_REQUEST:
					break;
				case ID_CHANGE_RESPONSE:
					break;
				case NODE_DISCOVERY_REQUEST:
					break;
				case NODE_DISCOVERY_RESPONSE:

					try {
						byte shByte[] = new byte[4];
						byte slByte[] = new byte[4];

						String s = new AuxiliarMethods().getData(buffer);

						for (int i = 0; i < 4; i++) {
							shByte[i] = buffer[i + 5];
							slByte[i] = buffer[i + 9];
						}

						String sh = s.substring(4, 12);
						String sl = s.substring(12, 20);
						
						//String type = s.substring(28, 29);
						String type =new AuxiliarMethods().getDeviceType(buffer, c);

						xbee.add(new XBeeDevice(sh, sl, type, "TESTE", shByte,
								slByte));
					} catch (Exception e) {

					}

					break;

				case SET_ACTUATOR_TO_SENSOR:
					break;

				case GET_ACTUATOR_FROM_SENSOR:
					break;

				case COMMAND_TEXT:
					break;
				default:

					break;
				}
			}
		}

	};

	public void sendText(byte command, byte target, String text) {

		int textLength = text.length();
		byte[] buffer = new byte[3 + textLength];
		if (textLength <= 252) {
			buffer[0] = command;
			buffer[1] = target;
			buffer[2] = (byte) textLength;
			byte[] textInBytes = text.getBytes();
			for (int x = 0; x < textLength; x++) {
				buffer[3 + x] = textInBytes[x];
			}
			try {
				if (mOutputStream != null) {
					try {
						mOutputStream.write(buffer);
					} catch (IOException e) {
						// Toast.makeText(c, "write failed",
						// Toast.LENGTH_LONG).show();
						Log.e(TAG, "write failed", e);
					}
				}
			} catch (Exception e) {
				// Toast.makeText(c, "PIMBAS!!!", Toast.LENGTH_SHORT);
			}
		}
	}

	private void sendByteText(byte command, byte target, byte[] text) {
		int textLength = text.length;
		byte[] buffer = new byte[3 + textLength];
		if (textLength <= 252) {
			buffer[0] = command;
			buffer[1] = target;
			buffer[2] = (byte) textLength;
			for (int i = 0; i < textLength; i++) {
				buffer[3 + i] = text[i];
			}

			for (int i = 0; i < buffer.length; i++) {
				Log.d(debug, "" + Integer.toHexString(buffer[i] & 0xff));
			}

			try {
				if (mOutputStream != null) {
					try {
						mOutputStream.write(buffer);
					} catch (IOException e) {
						// Toast.makeText(c, "write failed",
						// Toast.LENGTH_LONG).show();
						Log.e(TAG, "write failed", e);
					}
				}
			} catch (Exception e) {
				// Toast.makeText(c, "PIMBAS!!!", Toast.LENGTH_SHORT);
			}
		}

	}

	/*
	 * ###########################################################
	 * 
	 * Methods for the communication between Android and Arduino
	 * 
	 * ############################################################
	 */

	private void retrieveXBeeAddress() {

		shAddress = "sh";
		slAddress = "sl";
		panID = "id";
		new LoadingScreen(6000, searchType.searchForConnectedDevice).execute();

	}

	private void populateXbeeTable() {
		if (xbee.size() > 0) {
			auxXBee.setList(xbee);
			TableRow tr=new TableRow(c);
			TextView a=new TextView(c);
			TextView t=new TextView(c);
			TextView s=new TextView(c);
			
			a.setText(R.string.address);
			a.setTextAppearance(c, android.R.style.TextAppearance_Large);
			
			t.setText(R.string.type);
			t.setTextAppearance(c, android.R.style.TextAppearance_Large);
			
			s.setText(R.string.signalStrength);
			
			s.setTextAppearance(c, android.R.style.TextAppearance_Large);
			
			
			tr.setBackgroundColor(Color.parseColor(light_grey));
			tr.setPadding(0, 10, 0, 10);
			
			a.setTextColor(Color.parseColor(black));
			t.setTextColor(Color.parseColor(black));
			s.setTextColor(Color.parseColor(black));
			
			tr.addView(a);
			tr.addView(t);
			tr.addView(s);
			tlXBeeDevices.addView(tr);
			
			for (int i = 0; i != auxXBee.getListSize(); i++) {

				TableRow r = new TableRow(c);
				final TextView addr = new TextView(c);
				TextView type = new TextView(c);
				TextView ss = new TextView(c);
				
				


				addr.setTextAppearance(c, android.R.style.TextAppearance_Large);
				addr.setText(auxXBee.getAddress(i).toUpperCase());
				addr.setId(i);
				addr.setClickable(true);
				addr.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						callXBeeDetails(addr.getId());

					}

				});

				type.setTextAppearance(c, android.R.style.TextAppearance_Large);
				type.setText(auxXBee.getType(i));
				type.setGravity(Gravity.LEFT);

				ss.setTextAppearance(c, android.R.style.TextAppearance_Large);
				ss.setText(auxXBee.getSignalStrength(i));
				
				r.setPadding(0, 10, 0, 10);
				if(i%2==0){
					r.setBackgroundColor(Color.parseColor(green));
				}else{
					r.setBackgroundColor(Color.parseColor(light_grey));
				}

				addr.setTextColor(Color.parseColor(black));
				type.setTextColor(Color.parseColor(black));
				ss.setTextColor(Color.parseColor(black));
				
				r.addView(addr);
				r.addView(type);
				r.addView(ss);
				tlXBeeDevices.addView(r);

			}

		} else {
			alert.newMessage(MessageType.DEVICES_NOT_DETECTED);
		}
	}

	/*
	 * #########################
	 * 
	 * REQUEST THREADS
	 * 
	 * ##########################
	 */

	private class SimpleRequestThread extends Thread {
		byte command;
		byte target;
		String text;

		public SimpleRequestThread(byte cmd, byte target, String text) {
			this.command = cmd;
			this.target = target;
			this.text = text;

		}

		public void run() {
			sendText(this.command, this.target, this.text);
		}

	};

	private class SendInformationThread extends Thread {
		byte command;
		byte target;
		byte[] text;

		public SendInformationThread(byte cmd, byte target, byte[] text) {
			this.command = cmd;
			this.target = target;
			this.text = text;
		}

		public void run() {
			sendByteText(this.command, this.target, this.text);
		}

	}

	/*
	 * ##########################
	 * 
	 * LOADING SCREEN
	 * 
	 * ########################
	 */

	private class LoadingScreen extends AsyncTask<Void, Integer, Void> {

		private int time;
		private searchType st;

		public LoadingScreen(int timeInMillisecs, searchType st) {
			this.time = timeInMillisecs;
			this.st = st;
			
		}

		@Override
		protected void onPreExecute() {
			// try{
			// Toast.makeText(c,
			// "NUMERO DE DISPOSITIVOS: "+Integer.toString(mUsbManager.getAccessoryList().length),
			// Toast.LENGTH_LONG).show();
			// }catch(Exception e){
			// Toast.makeText(c, "NAO ENCONTRADO", Toast.LENGTH_LONG).show();
			// }

			if (this.st == searchType.searchForWirelessDevices){
				progressDialog = ProgressDialog.show(
						XBeeConfiguratorActivity.this,
						c.getString(R.string.Searching),
						c.getString(R.string.SearchingDevices), false, false);
			
			}else if (this.st == searchType.searchForConnectedDevice){

//				progressDialog.setTitle("sdf");
//				progressDialog.setMessage("dsfsdfsdf");
//				progressDialog.setCancelable(false);  
//	            progressDialog.setIndeterminate(false);
//	            progressDialog.show();
				progressDialog = ProgressDialog.show(
						XBeeConfiguratorActivity.this,
						c.getString(R.string.Searching),
						c.getString(R.string.SearchingConnectedDevice), false,
						false);
			}else if (this.st == searchType.changePan)
				progressDialog = ProgressDialog.show(
						XBeeConfiguratorActivity.this,
						c.getString(R.string.Wait),
						c.getString(R.string.WaitForChange), false, false);

		}

		// The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params) {
			try {
				// Get the current thread's token
				synchronized (this) {

					int counter = 0;

					if (this.st == searchType.searchForWirelessDevices)
						while (counter < time) {

							this.wait(1000);
							counter += 1000;
						}
					else if (this.st == searchType.searchForConnectedDevice) {
						boolean complete = false;
						while (!complete) {
							sendText(SH_REQUEST, TARGET_DEFAULT, "");
							sendText(SL_REQUEST, TARGET_DEFAULT, "");
							sendText(ID_REQUEST, TARGET_DEFAULT, "");

							this.wait(1000);
							counter += 1000;

							if ((!shAddress.equals("sh")
									&& !slAddress.equals("sl") && !panID
										.equals("id")) || counter > time) {
								complete = true;
							}
						}
					} else if (this.st == searchType.changePan) {

						while (counter < time) {

							sendText(ID_REQUEST, TARGET_DEFAULT, "");
							this.wait(1000);
							counter += 1000;

						}
					}

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		// after executing the code in the thread
		@Override
		protected void onPostExecute(Void result) {
			// close the progress dialog
			progressDialog.dismiss();

			if (this.st == searchType.searchForWirelessDevices)
				populateXbeeTable();

			else if (this.st == searchType.searchForConnectedDevice)

				if (!shAddress.equals("sh") && !slAddress.equals("sl")
						&& !panID.equals("id")) {

					tvXbeeAddress.setText(shAddress.toUpperCase() + " "
							+ slAddress.toUpperCase());
					tvPanID.setText(panID);
					newID = Integer.valueOf(panID);

				} else {
					tvXbeeAddress.setText("");
					tvPanID.setText("");

					alert.newMessage(MessageType.DEVICE_NOT_FOUND);

				}
			if (this.st == searchType.changePan) {
				if (!panID.equals("id")) {
					if (Integer.valueOf(panID) == newID)
						tvPanID.setText(panID);
					else
						newID = oldID;
				} else
					alert.newMessage(MessageType.PAN_ID_NOT_CHANGED);

				panID = "id";
			}
		}
	}

}