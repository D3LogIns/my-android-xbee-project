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

	// Commands
	private static final byte shRequest = (byte) 0xA1;
	private static final byte slRequest = (byte) 0xA2;
	private static final byte shResponse = (byte) 0xA3;
	private static final byte slResponse = (byte) 0xA4;
	private static final byte niRequest = (byte) 0xA5;
	private static final byte niResponse = (byte) 0xA6;
	private static final byte idRequest = (byte) 0xB1;
	private static final byte idResponse = (byte) 0xB2;
	private static final byte idChangeRequest = (byte) 0xB3;
	private static final byte idChangeResponse = (byte) 0xB4;
	private static final byte nodeDiscoveryRequest = (byte) 0xD1;
	private static final byte nodeDiscoveryResponse = (byte) 0xD2;
	private static final byte setActuatorToSensor =(byte) 0xE1;
	private static final byte getActuatorToSensor =(byte) 0xE2;

	private static final byte COMMAND_TEXT = 0xF;
	private static final byte TARGET_DEFAULT = 0xF;

	// COMPONTENTS VARIABLES
	TextView tvXbeeAddress;
	TextView tvPanID;
	TableLayout tlXBeeDevices;

	// OTHER VARIABLES
	private ProgressDialog progressDialog;
	private String shAddress = "sh";
	private String slAddress = "sl";
	private String panID = "id";

	final Context c = this;
	ConnectionClass cc;
	AuxiliarXBee auxXBee;
	String language = "";
	AuxiliarMethods aux;
	AlertMessage alert;
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

		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);
		
		setContentView(R.layout.main);

		cc = new ConnectionClass(c);

		auxXBee = new AuxiliarXBee();

		alert = new AlertMessage(c);

		xbee = new LinkedList<XBeeDevice>();

		this.inicialization();
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
			
			Toast.makeText(c, "accessory: "+accessory.toString(), Toast.LENGTH_LONG).show();
			
			if (mUsbManager.hasPermission(accessory)){
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
			
		} else {
			Toast.makeText(c, "mAccessory is null", Toast.LENGTH_LONG).show();
			Log.d(TAG, "mAccessory is null");
		}

	}

	/** Called when the activity is paused by the system. */
	@Override
	public void onPause() {
		super.onPause();
		closeAccessory();
	}

	/**
	 * Called when the activity is no longer needed prior to being removed from
	 * the activity stack.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
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

		/*
		 * XBEE DETECTED DEVICES TABLE
		 */
		tlXBeeDevices = (TableLayout) findViewById(R.id.tlXBeeDevices);

		/*
		 * BUTTONS INICIALIZATION
		 */
		final Button bOK = (Button) findViewById(R.id.bOKPan);
		Button bDetect = (Button) findViewById(R.id.bDetectDevices);
		Button brefresh = (Button) findViewById(R.id.bRefresh);
		bOK.setEnabled(false);

		/*
		 * TEXT BOX'S INICIALIZATION
		 */

		final EditText etPan = (EditText) findViewById(R.id.editPan);
		tvXbeeAddress = (TextView) findViewById(R.id.tvCoordAdress);
		tvPanID = (TextView) this.findViewById(R.id.tvPanID);

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
				if (Integer.parseInt(string) > 5000
						|| Integer.parseInt(string) <= 0)
					return false;
				return true;
			}

			private void changeXbeePanID(int id) {
				oldID = newID;
				newID = id;

				new requestThread(idChangeRequest, TARGET_DEFAULT, Integer
						.toString(id)).run();
				new LoadingScreen(4000, searchType.changePan).execute();

			}
		});

		// REFRESH BUTTON
		brefresh.setOnClickListener(new OnClickListener() {
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

				tlXBeeDevices.removeAllViews();

				new requestThread(nodeDiscoveryRequest, TARGET_DEFAULT, "")
						.run();
				new LoadingScreen(10000, searchType.searchForWirelessDevices)
						.execute();

			}

		});

	}

	private void callXBeeDetails(int position) {
		Intent i = new Intent(c, XbeeDetailsActivity.class);
		Bundle b = new Bundle();

		i.putExtra("position", position);

		b.putSerializable("auxiliar", auxXBee);

		i.putExtras(b);

		this.startActivityForResult(i, MODE_PRIVATE);

	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		Bundle extras = intent.getExtras();
		auxXBee = (AuxiliarXBee) extras.getSerializable("auxiliar");

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
					UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						Toast.makeText(c, "permission denied for accessory: "+accessory, Toast.LENGTH_LONG).show();
						Log.d(TAG, "permission denied for accessory "
								+ accessory);
					}
					mPermissionRequestPending = false;
					
				}

			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				Toast.makeText(c, "DETACHED", Toast.LENGTH_LONG).show();
				UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
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
			Toast.makeText(c, "accessory opened", Toast.LENGTH_LONG).show();
			Log.d(TAG, "accessory opened");
		} else {
			Toast.makeText(c, "accessory open fail", Toast.LENGTH_LONG).show();
			Log.d(TAG, "accessory open fail");
		}
	}

	private void closeAccessory() {
		try {
			if (mFileDescriptor != null) {
				Toast.makeText(c, "ENTREI NO CLOSE ACCESSORY", Toast.LENGTH_LONG).show();
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			Toast.makeText(c, "ENTREI NO EXCEPTION DO CLOSE ACCESSORY", Toast.LENGTH_LONG).show();
			mFileDescriptor = null;
			mAccessory = null;
		}
		try{
			thread.interrupt();
		}catch(NullPointerException e){
			
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
				case shRequest:
					break;
				case slRequest:
					break;
				case shResponse:

					shAddress = new AuxiliarMethods().getData(buffer);
					break;

				case slResponse:

					slAddress = new AuxiliarMethods().getData(buffer);
					break;

				case niRequest:
					break;
				case niResponse:
					break;
				case idRequest:
					break;
				case idResponse:

					panID = new AuxiliarMethods().getData(buffer).replaceAll(
							"0", "");
					break;

				case idChangeRequest:
					break;
				case idChangeResponse:
					break;
				case nodeDiscoveryRequest:
					break;
				case nodeDiscoveryResponse:

					try{
					byte shByte[]=new byte[4];
					byte slByte[]=new byte[4];
					
					String s = new AuxiliarMethods().getData(buffer);
					
					for(int i=0; i<4; i++){
						shByte[i]=buffer[i+3];
						slByte[i]=buffer[i+7];
					}
					
					String sh = s.substring(4, 12);
					String sl = s.substring(12, 20);
					String type = s.substring(28, 29);

					switch (Integer.parseInt(type)) {
					case 0:
						type = c.getString(R.string.coordinator);
						break;
					case 1:
						type = c.getString(R.string.router);
						break;
					case 2:
						type = c.getString(R.string.sensor);
						break;
					default:
						type = c.getString(R.string.unknown);
						break;

					}

					xbee.add(new XBeeDevice(sh, sl, type, "TESTE", shByte, slByte));
					}catch(Exception e){
						
					}

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
			try{
			if (mOutputStream != null) {
				try {
					mOutputStream.write(buffer);
				} catch (IOException e) {
					Toast.makeText(c, "write failed", Toast.LENGTH_LONG).show();
					Log.e(TAG, "write failed", e);
				}
			}
		}catch(Exception e){
			Toast.makeText(c, "PIMBAS!!!", Toast.LENGTH_SHORT);
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
	 * REQUEST THREAD
	 * 
	 * ##########################
	 */

	private class requestThread extends Thread {
		byte command;
		byte target;
		String text;

		public requestThread(byte cmd, byte target, String text) {
			this.command = cmd;
			this.target = target;
			this.text = text;

		}

		public void run() {
			sendText(this.command, this.target, this.text);
		}

	};

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
//			try{
//				Toast.makeText(c, "NUMERO DE DISPOSITIVOS: "+Integer.toString(mUsbManager.getAccessoryList().length), Toast.LENGTH_LONG).show();
//			}catch(Exception e){
//				Toast.makeText(c, "NAO ENCONTRADO", Toast.LENGTH_LONG).show();
//			}
			
			if (this.st == searchType.searchForWirelessDevices)
				progressDialog = ProgressDialog.show(
						XBeeConfiguratorActivity.this,
						c.getString(R.string.Searching),
						c.getString(R.string.SearchingDevices), false, false);
			else if (this.st == searchType.searchForConnectedDevice)
				progressDialog = ProgressDialog.show(
						XBeeConfiguratorActivity.this,
						c.getString(R.string.Searching),
						c.getString(R.string.SearchingConnectedDevice), false,
						false);
			else if (this.st == searchType.changePan)
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
							sendText(shRequest, TARGET_DEFAULT, "");
							sendText(slRequest, TARGET_DEFAULT, "");
							sendText(idRequest, TARGET_DEFAULT, "");

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

							sendText(idRequest, TARGET_DEFAULT, "");
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

				} else{
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