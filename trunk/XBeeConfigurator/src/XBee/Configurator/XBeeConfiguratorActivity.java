package XBee.Configurator;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

	private static final byte COMMAND_TEXT = 0xF;
	private static final byte TARGET_DEFAULT = 0xF;

	// OTHER VARIABLES

	private String shAddress="NOT";
	private String slAddress="FOUND";

	final Context c = this;
	ConnectionClass cc;
	AuxiliarXBee auxXBee;
	String language = "";
	AuxiliarLanguage aux;
	AlertMessage alert;

	TextView tvXbeeAddress;

	/**
	 *  Called when the activity is first created. 
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cc = new ConnectionClass(c);

		auxXBee = new AuxiliarXBee();

		// aux=new AuxiliarLanguage(c);

		// aux.setLanguage();

		// language=aux.getLanguage();

		alert = new AlertMessage(c);

		this.registerUsbConnection();
		
		setContentView(R.layout.main);
		
		this.inicialization();
	}
	
	/**
	 * Called when the activity is resumed from its paused state and immediately
	 * after onCreate().
	 */
	@Override
	public void onResume() {
		super.onResume();
		openUsbConnection();
		
	}

	/** Called when the activity is paused by the system. */
	@Override
	public void onPause() {
		super.onPause();
		closeAccessoryConnection();
	}

	/**
	 * Called when the activity is no longer needed prior to being removed from
	 * the activity stack.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterUsbReceiver();
	}
	
	
	/*
	 * 
	 * METHODS FOR VISUALIZATION
	 * 
	 */

	private void inicialization() {

		/*
		 * XBEE DETECTED DEVICES TABLE
		 */
		final TableLayout tlXBeeDevices = (TableLayout) findViewById(R.id.tlXBeeDevices);

		/*
		 * BUTTONS INICIALIZATION
		 */
		final Button bOK = (Button) findViewById(R.id.bOKPan);
		Button bDetect = (Button) findViewById(R.id.bDetectDevices);
		Button brefresh=(Button) findViewById(R.id.bRefresh);

		/*
		 * TEXT BOX'S INICIALIZATION
		 */

		final EditText etPan = (EditText) findViewById(R.id.editPan);
		tvXbeeAddress = (TextView) findViewById(R.id.tvCoordAdress);

		/*
		 * TEXT BOX'S LISTENERS
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
		 * BUTTONS LISTENERS
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

			private void changeXbeePanID(int parseInt) {

			}
		});
		
		
		//REFRESH BUTTON
		brefresh.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				retrieveXBeeAddress();
			}
			
		});

		// DETECT BUTTON
		bDetect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				auxXBee.clearList();

				tlXBeeDevices.removeAllViews();

				cc.searchXBeeDevices();
				auxXBee.setList(cc.getList());

				if (auxXBee.getListSize() < 0)
					alert.newMessage(MessageType.DEVICES_NOT_DETECTED);
				else {
					for (int i = 0; i != auxXBee.getListSize(); i++) {
						TableRow r = new TableRow(c);
						final TextView addr = new TextView(c);
						TextView type = new TextView(c);
						TextView ss = new TextView(c);

						addr.setText(auxXBee.getAddress(i));
						addr.setId(i);
						addr.setClickable(true);
						addr.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {

								callXBeeDetails(addr.getId());

							}

						});

						type.setText(auxXBee.getType(i));

						ss.setText(auxXBee.getSignalStrength(i));

						r.addView(addr);
						r.addView(type);
						r.addView(ss);
						tlXBeeDevices.addView(r);

					}
				}
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
	 * 
	 * Methods for the USB connection
	 * 
	 */


	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					// UsbAccessory accessory = UsbManager.getAccessory(intent);
					UsbAccessory accessory = (UsbAccessory) intent
							.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						Log.d(TAG, "permission denied for accessory "
								+ accessory);
					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				// UsbAccessory accessory = UsbManager.getAccessory(intent);
				UsbAccessory accessory = (UsbAccessory) intent
						.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
				if (accessory != null && accessory.equals(mAccessory)) {
					closeAccessory();
				}
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
			Thread thread = new Thread(null, commRunnable, TAG);
			thread.start();
			Log.d(TAG, "accessory opened");
		} else {
			Log.d(TAG, "accessory open fail");
		}
	}

	private void closeAccessory() {
		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
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
				case shRequest:
					break;
				case slRequest:
					break;
				case shResponse:
					shAddress = getData(buffer).replaceAll(" ", "");
					break;

				case slResponse:
					
					slAddress = getData(buffer).replaceAll(" ", "");
					break;
					
				case niRequest:
					break;
				case niResponse:
					break;
				case idRequest:
					break;
				case idResponse:
					break;
				case idChangeRequest:
					break;
				case idChangeResponse:
					break;
				case nodeDiscoveryRequest:
					break;
				case nodeDiscoveryResponse:
					break;
				default:
					Log.d(TAG, "unknown msg: " + buffer[0]);
					break;
				}
			}
		}

		private String getData(byte[] buffer) {
			final StringBuilder textBuilder = new StringBuilder();
			int textLength = buffer[2];
			int textEndIndex = 3 + textLength;
			for (int x = 3; x < textEndIndex; x++) {

				textBuilder.append(Integer.toHexString(buffer[x]));

				if (buffer[x] == 0) {
					textBuilder.append('0');
				}
				// textBuilder.append(" ");
				// textBuilder.append((char) buffer[x]);
			}
			return checkStringBuilder(textBuilder);
		}

		private String checkStringBuilder(StringBuilder textBuilder) {
			// TODO Auto-generated method stub
			int cont = 0;
			for (int i = 0; i < textBuilder.length(); i++) {
				if (textBuilder.charAt(i) == 'f') {
					cont++;
					if (cont == 6) {
						for (int j = i; j > i - cont; j--) {
							textBuilder.setCharAt(j, ' ');
						}
					}
				}
				if (textBuilder.charAt(i) == ' ')
					cont = 0;
			}
			return textBuilder.toString();
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
			if (mOutputStream != null) {
				try {
					mOutputStream.write(buffer);
				} catch (IOException e) {
					Log.e(TAG, "write failed", e);
				}
			}
		}
	}

	
public void registerUsbConnection(){
	mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

	mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
			ACTION_USB_PERMISSION), 0);
	IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
	filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
	registerReceiver(mUsbReceiver, filter);
}
	
	public void openUsbConnection(){
		if (mInputStream != null && mOutputStream != null) {
			return;
		}
		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
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
			Log.d(TAG, "mAccessory is null");
		}
	}
	
	public void closeAccessoryConnection(){
		closeAccessory();
	}
	
	public void unregisterUsbReceiver(){
		unregisterReceiver(mUsbReceiver);
	}

	/*
	 * Methods for the communication between Android and Arduino
	 */
	
	Thread t =new Thread(){
		
		public void run(){
			registerUsbConnection();
			openUsbConnection();
			boolean complete=false;
			int timeout=0;
			//openAcessory(accessory);
			while(!complete){
				sendText(shRequest, TARGET_DEFAULT, "");
				sendText(slRequest, TARGET_DEFAULT, "");				
				try {
					sleep(1000);
					timeout+=1000;
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if((!shAddress.equals("NOT") && !slAddress.equals("FOUND")) || timeout>6000){
					complete=true;
					if(!shAddress.equals("NOT") && !slAddress.equals("FOUND"))
						tvXbeeAddress.setText(shAddress.toUpperCase()+" "+slAddress.toUpperCase());
					else
						tvXbeeAddress.setText(R.string.deviceNotFound);
					
					//System.out.println("ENTREI!! TEMPO: "+timeout+" shAddress: "+shAddress+" slAddress: "+slAddress+" complete: "+complete);
				}
			}

		}
		
	};

	private void retrieveXBeeAddress() {
		try {
//	FAZER THREAD

			t.run();
			
//			try {
//				this.wait(7000);
//			} catch (InterruptedException e) {
//
//			}
		} catch (NullPointerException e) {

		}
	}
}