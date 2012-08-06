package XBee.Configurator;

import android.app.Activity;
import android.os.Bundle;


//import com.android.future.usb.UsbAccessory;
//import com.android.future.usb.UsbManager;

import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;

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
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.TextView;

public class ArduinoAndroid extends Activity {

	private static final String TAG = ArduinoAndroid.class.getSimpleName();
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
	
	private String shAddress;
	private String slAddress;

	public ArduinoAndroid(){

		// mUsbManager = UsbManager.getInstance(this);
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);

	}

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

					//shAddress = getData(buffer).replaceAll(" ", "");
					
					StringBuilder textBuilder = new StringBuilder();
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
					
					shAddress=checkStringBuilder(textBuilder).replace(" ", "");
					

					break;

				case slResponse:
					//slAddress = getData(buffer).replaceAll(" ", "");
					
					StringBuilder textBuilder2 = new StringBuilder();
					int textLength2 = buffer[2];
					int textEndIndex2 = 3 + textLength2;
					for (int x = 3; x < textEndIndex2; x++) {

						textBuilder2.append(Integer.toHexString(buffer[x]));

						if (buffer[x] == 0) {
							textBuilder2.append('0');
						}
						// textBuilder.append(" ");
						// textBuilder.append((char) buffer[x]);
					}
					
					slAddress=checkStringBuilder(textBuilder2).replace(" ", "");
					
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
	 * Methods for the Android Application
	 * 
	 */
	
	public void getAddress(TextView tv){

		
		sendText(shRequest, TARGET_DEFAULT, "");
		sendText(slRequest, TARGET_DEFAULT, "");
		
		if(shAddress==null){
			shAddress="NOT";
			slAddress="FOUND";
		}		
		tv.setText(shAddress + " " + slAddress);
		
	}
}