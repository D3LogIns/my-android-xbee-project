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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class XbeeDetailsActivity extends Activity {
	
	
	private static final String TAG = XbeeDetailsActivity.class.getSimpleName();
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

	

	private TableLayout xbeeDevices;
	private TableLayout associatedDevices;
	private AuxiliarXBee aux;
	private String sType = "";
	private Context c = this;
	AlertMessage alert;
	int position;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);
		
		
		
		setContentView(R.layout.xbeedetails);

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

	
	
	/*
	 * 
	 * Methods for the USB connection
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
			
					break;

				case slResponse:


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
				if (textBuilder.charAt(i) == 'f')
					cont++;
				else
					cont = 0;

				if (cont == 6)
					for (int j = i; j > i - cont; j--)
						textBuilder.setCharAt(j, ' ');

			}

			return textBuilder.toString().replaceAll(" ", "")
					.replaceAll("ffffff", "");
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
}
