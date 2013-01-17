package XBee.Configurator;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

	private static final byte NODE_DISCOVERY_REQUEST = (byte) 0xD1;
	private static final byte NODE_DISCOVERY_RESPONSE = (byte) 0xD2;

	private static final byte GET_ASSOCIATED_DEVICES_REQUEST = (byte) 0xE1;
	private static final byte GET_ASSOCIATED_DEVICES_RESPONSE = (byte) 0xE2;

	private static final byte CHANGE_CONFIG=(byte) 0xCC;
	private static final byte CONTROL_REQUEST=(byte) 0xC1;
//	private static final byte ASSOCIATE_DEVICE = (byte) 0xAD;
//	private static final byte DESASSOCIATE_DEVICE = (byte) 0xDA;
//	private static final byte CHANGE_LIGHT_CONTROL=(byte)0xCB;
//	private static final byte LIGHT_CONTROL_REQUEST=(byte)0xCD;
//	private static final byte LIGHT_CONTROL_RESPONSE=(byte)0xCE;

	private static final byte COMMAND_TEXT = 0xF;

	private static final byte TARGET_DEFAULT = 0xF;
	private static final byte TARGET_REMOTE_XBEE = (byte) 0xFF;

	// COMPONTENTS VARIABLES
	TextView tvXbeeAddress;
	TextView tvPanID;
	TextView tvDeviceType;
	TableLayout tlXBeeDevices;
	TableLayout associatedDevices;
	TableRow rowAssociateDevice;
	TableRow rowDesassociateDevice;
	TableRow rowSetLightControl;

	// CLASSES VARIABLES
	private AlertMessage alert;
	private AlertClass privateAlert;
	private ConnectionClass cc;
	private AuxiliarXBee auxXBee;
	private SensorsAndActuators sa;
	AuxiliarMethods auxM;

	// COLOR VARIABLES
	private String green = "#BB7EFFA6";
	private String black = "#000000";
	private String light_grey = "#BBF3F5E7";
	private String silver = "#BBC0C0C0";
	private String light_blue = "#BBADDFFF";

	// OTHER VARIABLES
	private ProgressDialog progressDialog;
	private String shAddress = "sh";
	private String slAddress = "sl";
	private String panID = "id";
	private String dType = "type";
	private boolean getAssocDevsControl = false;
	private int xbeePosControl = 0;
	private int lightLevelControl=0;

	final Context c = this;
	private LinkedList<XBeeDevice> xbee;
	private LinkedList<MyActuator> myActuators;
	private int DEVICE_LIMIT = 2;
	private int oldID = 0;
	private int newID = 0;
	private Thread thread, tListener;

	private enum loadingType {
		searchForConnectedDevice, searchForWirelessDevices, changePan, associateDevice, desassociateDevice, getAssociatedDevices
	};

	private enum deviceType {
		ACTUATOR, COORDINATOR, ROUTER, SENSOR
	}

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
		
		auxM=new AuxiliarMethods();

		alert = new AlertMessage(c);

		privateAlert = new AlertClass(c);

		myActuators = new LinkedList<MyActuator>();

		sa = new SensorsAndActuators();

		xbee = new LinkedList<XBeeDevice>();

		this.inicialization();

		tListener = new Thread(null, listener, TAG);
		tListener.start();
		

		rowSetLightControl.setVisibility(View.VISIBLE);

	}

	private void registerAccessory() {
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

		// progressDialog = new ProgressDialog(XBeeConfiguratorActivity.this,
		// R.style.Theme_MyDialog);
		progressDialog = new ProgressDialog(c);

		/*
		 * Configuration Rows
		 */
		rowAssociateDevice = (TableRow) findViewById(R.id.rowAssociateDevice);
		rowDesassociateDevice = (TableRow) findViewById(R.id.rowDesassociateDevice);
		rowSetLightControl=(TableRow) findViewById(R.id.rowSetLightControl);

		/*
		 * DEVICES TABLE
		 */
		associatedDevices = (TableLayout) findViewById(R.id.associatedDevicesTable);
		tlXBeeDevices = (TableLayout) findViewById(R.id.tlXBeeDevices);

		/*
		 * BUTTONS INICIALIZATION
		 */
		final Button bOkPanId = (Button) findViewById(R.id.bOKPan);
		Button bDetect = (Button) findViewById(R.id.bDetectDevices);
		Button bRefresh = (Button) findViewById(R.id.bRefresh);
		final Button bAssociate = (Button) findViewById(R.id.bOK_Associate);
		final Button bDesassociate = (Button) findViewById(R.id.bOK_Desassociate);
		final Button bSetLightControl = (Button) findViewById(R.id.bOK_setLightControl);
		bOkPanId.setEnabled(false);

		/*
		 * TEXT BOX'S INICIALIZATION
		 */

		final EditText etPan = (EditText) findViewById(R.id.editPan);
		tvXbeeAddress = (TextView) findViewById(R.id.tvCoordAdress);
		tvPanID = (TextView) findViewById(R.id.tvPanID);
		tvDeviceType = (TextView) findViewById(R.id.tvDeviceType);
		final EditText etAssociate = (EditText) findViewById(R.id.editAssociateDevice);
		final EditText etDesassociate = (EditText) findViewById(R.id.editDesassociateDevice);
		
		/*
		 * SEEK BAR INICIALIZATION
		 */

		final SeekBar setLightControl=(SeekBar) findViewById(R.id.seek_setLightControl);
		
		setLightControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				bSetLightControl.setEnabled(true);
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
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
					bOkPanId.setEnabled(true);
				// IF TEXT SIZE IS LOWER OR EQUAL TO 0, OK BUTTON TURNS
				// DEACTIVATED
				else if (s.length() <= 0)
					bOkPanId.setEnabled(false);
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

		etDesassociate.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable e) {
				// TODO Auto-generated method stub
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

		// OK PAN ID BUTTON
		bOkPanId.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (inBoundsPanID(etPan.getText().toString())) {
					/*
					 * ATENCAO
					 */
					changeXbeePanID(Integer.parseInt(etPan.getText().toString()));

				} else{
					alert.newMessage(MessageType.PAN_ID_OUT_OF_BOUNDS);
					
				}
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

				/*
				 * PRECISA DE ATENCAO
				 */
				byte msg[]=auxM.newConfigPacket();
				msg=auxM.packet_put_panId(msg, idByte);
				
				
				new SendInformationThread(CHANGE_CONFIG, TARGET_DEFAULT, msg).run();

				// new SimpleRequestThread(idChangeRequest, TARGET_DEFAULT,
				// Integer
				// .toString(id)).run();
				//new LoadingScreen(4000, loadingType.changePan).execute();

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
				tlXBeeDevices.removeAllViews();

				/*
				 * 
				 * METODOS PARA A CRIACAO DE NOS VIRTUAIS
				 */
				cc.searchXBeeDevices();
				xbee = cc.getList();
				auxXBee.setList(xbee);
				populateXbeeTable();

				/*
				 * METODOS PARA A DETECACAO DE NOS NA REDE
				 */
//				 new SimpleRequestThread(NODE_DISCOVERY_REQUEST,
//				 TARGET_DEFAULT,
//				 "1").run();
//				 new LoadingScreen(10000,
//				 loadingType.searchForWirelessDevices)
//				 .execute();

			}

		});

		// ASSOCIATE BUTTON
		bAssociate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String s = etAssociate.getText().toString();
				etAssociate.setText("");

				if (s.length() >= 14) {
					try {

						byte[] b = new AuxiliarMethods().convertStringAddressToByte(s);
						addActuator(s, c.getString(R.string.actuator));

						byte msg[]=auxM.newConfigPacket();
						msg=build_association_packet(msg);

						new SendInformationThread(CHANGE_CONFIG,TARGET_DEFAULT, msg).run();
						
			
						

					} catch (Exception e) {
						alert.newMessage(MessageType.ADDRESS_NOT_ACCEPTABLE);
					}
				} else
					alert.newMessage(MessageType.ADDRESS_SHORT_LENGTH);

			}

		});
		this.auxXBee.getList();
		// DESASSOCIATE BUTTON

		bDesassociate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String s = etDesassociate.getText().toString();
				etDesassociate.setText("");

				if (s.length() >= 14) {
					try {
						/*
						 * PRECISA DE ATENCAO!!!!
						 */
						byte[] address = new AuxiliarMethods()
								.convertStringAddressToByte(s);

						byte msg[]=auxM.newConfigPacket();
						
						removeActuator(s);
				
						msg=auxM.packet_put_DessAssociation(msg, myActuators.getFirst().getAddressByte(), true);
						
						new SendInformationThread(CHANGE_CONFIG,
								TARGET_DEFAULT, address).run();
						

					} catch (Exception e) {
						alert.newMessage(MessageType.ADDRESS_NOT_ACCEPTABLE);
					}
				} else
					alert.newMessage(MessageType.ADDRESS_SHORT_LENGTH);
			}

		});
		
		bSetLightControl.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				int level=setLightControl.getProgress();
				if(level!=lightLevelControl){
					lightLevelControl=level;
					byte b[]=auxM.intToByteArray(level);
					/*
					 * PRECISA DE ATENCAO!!!!
					 */
					
					byte msg[]=auxM.newConfigPacket();
					msg=auxM.packet_put_data(msg, b);
					
					new SendInformationThread(CHANGE_CONFIG, TARGET_DEFAULT, msg).run();
				}
			}
			
		});

	}
	
	private byte[] build_association_packet(byte[] msg){
		if(myActuators.size()>0){
			byte addresses[]=new byte[8*myActuators.size()];
			int x=0;
			
			for(int i=0; i<myActuators.size(); i++){
				for(int j=0; j<myActuators.get(i).getAddressByte().length; j++){
					addresses[x]=myActuators.get(i).getAddressByte()[j];
					x++;
				}
			}
			
			msg[8]=1;
			msg[11]=(byte) myActuators.size();
			msg=auxM.packet_put_DessAssociation(msg, addresses, true);

		}
		
		return msg;
	}

	/*
	 * ######################################
	 * 
	 * METHODS FOR THE XBeeDetails Activity
	 * 
	 * ######################################
	 */

	private void callXBeeDetails(int position) {
		/*
		 * Here the SensorsAndActuators class will be "populated" The reason of
		 * this is that this class will be used later to send to Arduino the
		 * associated devices without repeated values
		 * 
		 * 1- If the selected device is a sensor 2- If the selected device is an
		 * actuator
		 */
		sa.clearTable();
		if (!auxXBee.getType(position).equals(getString(R.string.actuator))
				&& !auxXBee.getType(position)
						.equals(getString(R.string.router))
				&& !auxXBee.getType(position).equals(
						getString(R.string.coordinator))
				&& !auxXBee.getType(position).equals(getString(R.string.actuatorLuminance))
				&& !auxXBee.getType(position).equals(getString(R.string.actuatorMotion))) {

			if (auxXBee.getMyActuators(position).size() > 0) {
				sa.setActuatorsByte(auxXBee.getMyActuatorsByte(position));
				// sa.setActuatorsString(auxXBee.getMyActuators(position));
				sa.setSensorByte(auxXBee.getAddressByte(position));
			}

		}
		;
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
		
		//4Green
		Bundle extras = intent.getExtras();

		auxXBee = (AuxiliarXBee) extras.getSerializable("auxiliarXbee");

		int position = extras.getInt("position");

		getAssocDevsControl = false;

		String remoteType = auxXBee.getType(position);
		if (remoteType.equals(getString(R.string.sensor))
				|| remoteType.equals(getString(R.string.luminanceSensor))
				|| remoteType.equals(getString(R.string.motionSensor))
				|| remoteType.equals(getString(R.string.routerLuminanceSensor))
				|| remoteType.equals(getString(R.string.routerMotionSensor))) {

			byte[] remotePanId = extras.getByteArray("panId");

			if (remotePanId.length > 4) {
				remotePanId = null;
			}
			

//			setConfigsToRemoteXBee(auxXBee.getMyActuatorsByte(position),
//					auxXBee.getAddressByte(position), remotePanId);
			
//			byte msg[]=new AuxiliarMethods().newRemoteConfigMessage(
//					auxXBee.getMyActuatorsByte(position),
//					sa.getActuatorsByte().size(),
//					auxXBee.getAddressByte(position),
//					remotePanId);
			
			
			
			byte msg[]=auxM.newConfigPacket();
			msg=auxM.packet_put_panId(msg, remotePanId);
			msg=auxM.packet_put_remoteAddress(msg, auxXBee.getAddressByte(position));
			msg=auxM.packet_put_DessAssociation(msg, auxXBee.getMyActuatorsByte(position), sa.getActuatorsByte().size());
			
			
			new SendInformationThread(CHANGE_CONFIG, TARGET_REMOTE_XBEE, msg).run();
		}

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

				case SH_RESPONSE:

					shAddress = new AuxiliarMethods().getData(buffer);
					break;

				case SL_RESPONSE:

					slAddress = new AuxiliarMethods().getData(buffer);
					break;

				case NI_RESPONSE:

					dType = new AuxiliarMethods()
							.getDeviceType(buffer, c, "NI");

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

						// String type = s.substring(28, 29);
						String type = new AuxiliarMethods().getDeviceType(
								buffer, c, "ND");

						xbee.add(new XBeeDevice(sh, sl, type, "TESTE", shByte,
								slByte));
					} catch (Exception e) {

					}

					break;

				case GET_ASSOCIATED_DEVICES_RESPONSE:

					if (buffer[1] == TARGET_REMOTE_XBEE) {
						if (!getAssocDevsControl) {
							byte b[] = new AuxiliarMethods()
									.getDataByte(buffer);
							int nDevices = b[0];

							if (nDevices > 0) {
								byte addr[] = new byte[nDevices * 8];
								for (int i = 0; i < nDevices; i++) {
									for (int j = 1; j < 9; j++) {
										addr[j - 1] = b[j];
									}
									auxXBee.associateActuatorToSensor(
											new AuxiliarMethods()
													.convertByteToString(addr),
											xbee.get(xbeePosControl)
													.getAddress(), null);
									// xbee.get(xbeePosControl).setActuator(addr);
								}

							}
						}
					} else {

						byte b[] = new AuxiliarMethods().getDataByte(buffer);
						int nDevices = b[0];
						if (nDevices > 0) {
							byte addr[];
							int x = 1;
							for (int i = 0; i < nDevices; i++) {
								addr = new byte[8];
								for (int j = 0; j < 8; j++) {
									addr[j] = b[x];
									x++;
								}
								addActuator(
										new AuxiliarMethods()
												.convertByteToString(addr),
										c.getString(R.string.actuator));
							}
						}
					}
					getAssocDevsControl = true;
					break;
					
				case CHANGE_CONFIG:
					if(buffer[1]==TARGET_REMOTE_XBEE){
						byte b[]=new AuxiliarMethods().getDataByte(buffer);
						int lightLevel=buffer[0];
					}
					
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
		dType = "type";
		new LoadingScreen(6000, loadingType.searchForConnectedDevice).execute();

	}

	private void populateXbeeTable() {
		if (xbee.size() > 0) {
			auxXBee.setList(xbee);
			TableRow tr = new TableRow(c);
			TextView a = new TextView(c);
			TextView t = new TextView(c);

			a.setText(getString(R.string.address));
			a.setTextAppearance(c, android.R.style.TextAppearance_Large);

			t.setText(getString(R.string.type));
			t.setTextAppearance(c, android.R.style.TextAppearance_Large);

			tr.setBackgroundColor(Color.parseColor(silver));
			tr.setPadding(0, 3, 0, 3);

			a.setTextColor(Color.parseColor(black));
			t.setTextColor(Color.parseColor(black));

			tr.addView(a);
			tr.addView(t);
			// tr.addView(s);
			tlXBeeDevices.addView(tr);

			for (int i = 0; i != auxXBee.getListSize(); i++) {

				TableRow r = new TableRow(c);
				final TextView addr = new TextView(c);
				final TextView type = new TextView(c);

				Button bDetails = new Button(c);
				Button bAss = new Button(c);

				if (i % 2 == 0) {
					r.setBackgroundColor(Color.parseColor(green));
				} else {
					r.setBackgroundColor(Color.parseColor(light_grey));
				}

				addr.setTextAppearance(c, android.R.style.TextAppearance_Large);
				addr.setText(auxXBee.getAddress(i).toUpperCase());
				addr.setId(i);

				type.setTextAppearance(c, android.R.style.TextAppearance_Large);
				type.setText(auxXBee.getType(i));
				type.setGravity(Gravity.LEFT);

				bDetails.setText(c.getString(R.string.details));
				bDetails.setBackgroundResource(R.drawable.button);
				bDetails.setClickable(true);

				bAss.setText(c.getString(R.string.associate));
				bAss.setBackgroundResource(R.drawable.button);
				bAss.setClickable(true);

				if (type.getText().equals(c.getString(R.string.actuator))
						|| type.getText().equals(
								c.getString(R.string.actuatorLuminance))
						|| type.getText().equals(
								c.getString(R.string.actuatorMotion))) {
					bAss.setVisibility(View.VISIBLE);
				} else {
					bAss.setVisibility(View.GONE);
				}

				// addr.setClickable(true);
				bDetails.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (type.getText().equals(
								c.getString(R.string.actuator))
								|| type.getText().equals(
										c.getString(R.string.router))
								|| type.getText().equals(
										c.getString(R.string.coordinator))
								|| type.getText()
										.equals(c
												.getString(R.string.actuatorLuminance))
								|| type.getText().equals(
										c.getString(R.string.actuatorMotion)))
							callXBeeDetails(addr.getId());

						else {
							new LoadingScreen(8000,
									loadingType.getAssociatedDevices, addr
											.getId()).execute();

						}
					}

				});

				bAss.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						try {
							String s = addr.getText().toString();

							privateAlert.newMessage(MessageType.SET_ACTUATOR,
									s, type.getText().toString());

						} catch (Exception e) {

							e.printStackTrace();
						}

					}
				});

				r.setPadding(0, 10, 0, 10);

				addr.setTextColor(Color.parseColor(black));
				type.setTextColor(Color.parseColor(black));

				r.addView(addr);
				r.addView(type);

				r.addView(bDetails);
				r.addView(bAss);
				tlXBeeDevices.addView(r);

			}

		} else {
			alert.newMessage(MessageType.DEVICES_NOT_DETECTED);
		}
	}

	private int checkRepeated(String addr) {
		for (int i = 0; i < myActuators.size(); i++) {
			if (addr.equals(myActuators.get(i).getAddressString())) {
				return i;
			}
		}

		return -1;
	}

	private void addActuator(String addr, String type) {

		if (!myActuators.isEmpty()) {
			if (checkRepeated(addr) == -1) {
				if (myActuators.size() < DEVICE_LIMIT) {

					try {
						byte b[] = new AuxiliarMethods()
								.convertStringAddressToByte(addr);
						
						/*
						 * PRECISA DE ATENCAO!!!!
						 */
						
						
						myActuators.add(new MyActuator(addr, b, type));
						
						byte msg[]=auxM.newConfigPacket();
						msg=build_association_packet(msg);
						
						
						new SendInformationThread(CHANGE_CONFIG,TARGET_DEFAULT, msg).run();
						
					} catch (Exception e) {
					}
				} else {
					alert.newMessage(MessageType.ACTUATORS_LIMIT_REACHED);
				}
			} else {
				alert.newMessage(MessageType.REPEATED_ADDRESS);
			}
		} else {
			try {
				
				byte b[] = new AuxiliarMethods().convertStringAddressToByte(addr);
				
				/*
				 * PRECISA DE ATENCAO!!!!
				 */
				myActuators.add(new MyActuator(addr, b, type));
				
				byte msg[]=auxM.newConfigPacket();
				
				msg=build_association_packet(msg);
				
				new SendInformationThread(CHANGE_CONFIG,TARGET_DEFAULT, msg).run();
			} catch (Exception e) {
			}
		}
	}

	private void removeActuator(String addr) {
		if (!myActuators.isEmpty()) {
			int pos = checkRepeated(addr);
			if (pos > -1) {
				try {
					byte b[] = new AuxiliarMethods()
							.convertStringAddressToByte(addr);
					myActuators.remove(pos);
					
					/*
					 * PRECISA DE ATENCAO!!!!
					 */
					
					new SendInformationThread(CHANGE_CONFIG,
							TARGET_DEFAULT, b).run();
					
				} catch (Exception e) {
				}
			}
		}
	}

	private void populateAssociatedDevicesTable() {

		associatedDevices.removeAllViews();

		if (!myActuators.isEmpty()) {
			TextView text = new TextView(this);
			text.setText(this.getString(R.string.myActuators));
			text.setTextAppearance(c, android.R.style.TextAppearance_Large);
			associatedDevices.addView(text);

			for (int i = 0; i < myActuators.size(); i++) {
				final TextView a = new TextView(this);
				TextView t = new TextView(this);
				Button bDes = new Button(this);
				TableRow r = new TableRow(this);

				a.setText(myActuators.get(i).getAddressString());
				a.setTextAppearance(this, android.R.style.TextAppearance_Large);
				a.setTextColor(Color.parseColor(black));
				a.setBackgroundColor(Color.parseColor(light_grey));
				a.setId(i);

				t.setText(myActuators.get(i).getType());
				t.setTextAppearance(this, android.R.style.TextAppearance_Large);
				t.setTextColor(Color.parseColor(black));
				t.setBackgroundColor(Color.parseColor(light_grey));

				bDes.setText(R.string.desassociate);
				bDes.setBackgroundResource(R.drawable.button);

				bDes.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						String s = a.getText().toString();
						removeActuator(s);
					}

				});

				text.setTextColor(Color.parseColor(black));

				r.setPadding(0, 3, 0, 3);

				r.addView(a);
				r.addView(t);
				r.addView(bDes);
				associatedDevices.addView(r);

			}

			associatedDevices.setVisibility(View.VISIBLE);
		} else {
			associatedDevices.setVisibility(View.GONE);
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

	}

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
		private loadingType loadType;
		private boolean everythingOK = false;
		private int id;

		public LoadingScreen(int timeInMillisecs, loadingType loadType) {
			this.time = timeInMillisecs;
			this.loadType = loadType;

		}

		public LoadingScreen(int timeInMillisecs, loadingType loadType, int id) {
			this.time = timeInMillisecs;
			this.loadType = loadType;
			this.id = id;
		}

		@Override
		protected void onPreExecute() {

			if (this.loadType == loadingType.searchForWirelessDevices) {
				progressDialog = ProgressDialog.show(
						XBeeConfiguratorActivity.this,
						c.getString(R.string.Searching),
						c.getString(R.string.SearchingDevices), false, false);

			} else if (this.loadType == loadingType.searchForConnectedDevice) {

				progressDialog = ProgressDialog.show(
						XBeeConfiguratorActivity.this,
						c.getString(R.string.Searching),
						c.getString(R.string.SearchingConnectedDevice), false,
						false);
			} else if (this.loadType == loadingType.changePan) {
				progressDialog = ProgressDialog.show(
						XBeeConfiguratorActivity.this,
						c.getString(R.string.Wait),
						c.getString(R.string.WaitForChange), false, false);
			} else if (this.loadType == loadingType.associateDevice) {
				progressDialog = ProgressDialog.show(
						XBeeConfiguratorActivity.this,
						c.getString(R.string.Wait),
						c.getString(R.string.WaitForChange), false, false);
			} else if (this.loadType == loadingType.getAssociatedDevices) {
				progressDialog = ProgressDialog.show(
						XBeeConfiguratorActivity.this,
						c.getString(R.string.Wait),
						c.getString(R.string.WaitForRetrieveInformation),
						false, false);
			}

		}

		// The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params) {
			try {
				// Get the current thread's token
				synchronized (this) {

					int counter = 0;

					if (this.loadType == loadingType.searchForWirelessDevices)
						while (counter < time) {

							this.wait(1000);
							counter += 1000;
						}
					else if (this.loadType == loadingType.searchForConnectedDevice) {
						boolean complete = false;
						everythingOK = false;
						getAssocDevsControl = false;

						sendText(GET_ASSOCIATED_DEVICES_REQUEST,
								TARGET_DEFAULT, "");

						while (!complete) {
							if (shAddress.equals("sh"))
								sendText(SH_REQUEST, TARGET_DEFAULT, "");
							if (slAddress.equals("sl"))
								sendText(SL_REQUEST, TARGET_DEFAULT, "");
							if (panID.equals("id"))
								sendText(ID_REQUEST, TARGET_DEFAULT, "");
							if (dType.equals("type"))
								sendText(NI_REQUEST, TARGET_DEFAULT, "");
							// if (getAssocDevsControl == false)
							// sendText(GET_ASSOCIATED_DEVICES_REQUEST,
							// TARGET_DEFAULT, "");

							this.wait(1000);
							counter += 1000;

							if (counter > time) {
								complete = true;
							} else if ((!shAddress.equals("sh")
									&& !slAddress.equals("sl")
									&& !panID.equals("id") && !dType
										.equals("type"))
									&& getAssocDevsControl == true) {
								complete = true;
								everythingOK = true;

							}
						}
					}

					else if (this.loadType == loadingType.changePan) {

						while (counter < time) {
							sendText(ID_REQUEST, TARGET_DEFAULT, "");
							this.wait(1000);
							counter += 1000;
						}
					}

					else if (this.loadType == loadingType.associateDevice) {
						while (counter < time) {
							this.wait(1000);
							counter += 1000;
						}
					}

					else if (this.loadType == loadingType.getAssociatedDevices) {
						boolean complete = false;
						xbeePosControl = this.id;
						byte address[] = xbee.get(xbeePosControl).getAddressByte();
						getAssocDevsControl = false;
						new SendInformationThread(
								GET_ASSOCIATED_DEVICES_REQUEST,
								TARGET_REMOTE_XBEE, address).run();
						
						if(xbee.get(xbeePosControl).getType().equals(getString(R.string.luminanceSensor))
								|| xbee.get(xbeePosControl).getType().equals(getString(R.string.routerLuminanceSensor))){
							/*
							 * PRECISA DE ATENCAO!!!!
							 */
							new SendInformationThread(CONTROL_REQUEST, TARGET_REMOTE_XBEE, address).run();
						}
						
						while (complete == false) {

							if (counter > time) {
								complete = true;
							} else if (getAssocDevsControl == true) {
								complete = true;
							}

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

			if (this.loadType == loadingType.searchForWirelessDevices) {
				populateXbeeTable();

			} else if (this.loadType == loadingType.searchForConnectedDevice) {

				if (everythingOK) {

					tvXbeeAddress.setText(shAddress.toUpperCase() + " "
							+ slAddress.toUpperCase());
					tvPanID.setText(panID);
					tvDeviceType.setText(dType);

					newID = Integer.valueOf(panID);

					if (dType.equals(c
							.getString(R.string.routerLuminanceSensor))
							|| dType.equals(c
									.getString(R.string.routerMotionSensor))
							|| dType.equals(c
									.getString(R.string.luminanceSensor))
							|| dType.equals(c.getString(R.string.motionSensor))
							|| dType.equals(c.getString(R.string.sensor))) {

						rowAssociateDevice.setVisibility(0);
						rowDesassociateDevice.setVisibility(0);
						
						if(dType.equals(c.getString(R.string.routerLuminanceSensor))
								|| dType.equals(c.getString(R.string.luminanceSensor))){
						
							rowSetLightControl.setVisibility(View.VISIBLE);
						}
					}

				} else {
					tvXbeeAddress.setText("");
					tvPanID.setText("");
					tvDeviceType.setText("");
					myActuators.clear();

					alert.newMessage(MessageType.DEVICE_NOT_FOUND);

				}

			} else if (this.loadType == loadingType.changePan) {
				if (!panID.equals("id")) {
					if (Integer.valueOf(panID) == newID)
						tvPanID.setText(panID);
					else
						newID = oldID;
				} else
					alert.newMessage(MessageType.PAN_ID_NOT_CHANGED);

				panID = "id";

			} else if (this.loadType == loadingType.associateDevice) {

			} else if (this.loadType == loadingType.getAssociatedDevices) {
				callXBeeDetails(id);
			}
		}
	}

	/*
	 * ##########################
	 * 
	 * MY ACTUATOR CLASS
	 * 
	 * ########################
	 */

	private class MyActuator {
		private String addrS;
		private byte addrB[] = new byte[8];
		private String type;

		public MyActuator(String addrString, byte addrByte[], String type) {
			this.addrS = addrString;
			this.addrB = addrByte;
			this.type = type;
		}

		public String getAddressString() {
			return this.addrS;
		}

		public byte[] getAddressByte() {
			return this.addrB;
		}

		public String getType() {
			return this.type;
		}

	}

	/*
	 * ##########################
	 * 
	 * ALERT DIALOG
	 * 
	 * ########################
	 */

	private class AlertClass extends AlertDialog {

		public AlertClass(Context context) {
			super(context);

		}

		public void newMessage(MessageType msg, String addr, String type) {
			if (msg.equals(MessageType.SET_ACTUATOR)) {
				setActuator(addr, c.getString(R.string.setActuator), type);
			} else if (msg.equals(MessageType.REMOVE_ACTUATOR)) {

			}
		}

		private void setActuator(final String addrActuator, String text,
				final String type) {
			AlertDialog.Builder b = new AlertDialog.Builder(c);

			b.setMessage(text + " " + addrActuator)
					.setPositiveButton(c.getString(R.string.yes),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									addActuator(addrActuator, type);
								}
							})
					.setNegativeButton(c.getString(R.string.no),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).show();

		}

	}

	// Runnable listener = new Runnable() {
	//
	// @Override
	// public void run() {
	// int size = 0;
	// while (true) {
	// // if the device is some sort of sensor
	// if (dType.equals(getString(R.string.sensor))
	// || dType.equals(getString(R.string.luminanceSensor))
	// || dType.equals(getString(R.string.motionSensor))
	// || dType.equals(getString(R.string.routerLuminanceSensor))
	// || dType.equals(getString(R.string.routerMotionSensor))
	// ) {
	//
	// if (myActuators.size() != size) {
	//
	// runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// populateAssociatedDevicesTable();
	//
	// }
	// });
	// size = myActuators.size();
	//
	// } else if (myActuators.size() == 0) {
	//
	// }
	//
	// // if the device is an actuator
	// }
	// }
	// }
	//
	// };

	Runnable listener = new Runnable() {

		@Override
		public void run() {
			int size = 0;
			while (true) {
				synchronized (myActuators) {
					if (myActuators.size() != size) {

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								populateAssociatedDevicesTable();

							}
						});
						size = myActuators.size();

					} else if (myActuators.size() == 0) {

					}
				}
			}
		}

	};
}
