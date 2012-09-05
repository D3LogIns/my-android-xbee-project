package XBee.Configurator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertMessage extends AlertDialog{

	Context c;
	String language;
	AuxiliarXBee aux;
	SensorsAndActuators sa;
	
	public AlertMessage(Context c) {
		super(c);
		this.c=c;
	}
	
	public AlertMessage(Context c, AuxiliarXBee a){
		super(c);
		this.c=c;
		this.aux=a;
	}
	
	public AlertMessage(Context c, SensorsAndActuators sa) {
		super(c);
		this.c=c;
		this.sa=sa;
		
	}

	/*
	 * Method for creating a simple alert message
	 */
	public void newMessage(MessageType msg){
		
		if(msg.equals(MessageType.COORDINATOR_NOT_DETECTED)){
			this.alertMessageOK(c.getString(R.string.coordNotFound));
		}else if(msg.equals(MessageType.PAN_ID_OUT_OF_BOUNDS)){
			this.alertMessageOK(c.getString(R.string.panIdOutOfBounds));
		}else if(msg.equals(MessageType.TEXT_OUT_OF_BOUNDS)){
			this.alertMessageOK(c.getString(R.string.textOutOfBounds));
		}else if(msg.equals(MessageType.DEVICES_NOT_DETECTED)){
			this.alertMessageOK(c.getString(R.string.devicesNotDetected));
		}else if(msg.equals(MessageType.DEVICE_NOT_FOUND)){
			this.alertMessageOK(c.getString(R.string.deviceNotFound));
		}else if(msg.equals(MessageType.PAN_ID_NOT_CHANGED)){
			this.alertMessageOK(c.getString(R.string.panIdNotChanged));
		}
		
	}
	
	/*
	 * Method for creating an alert message, requiring the Sensor and Actuator addresses
	 */
	public AuxiliarXBee newMessage(MessageType msg, String addrSensor, String addrActuator){
		
		if(msg.equals(MessageType.SET_ACTUATOR)){
			setActuator(addrSensor, addrActuator);
		}else if(msg.equals(MessageType.SET_SENSOR)){
			setSensor(addrSensor, addrActuator);
		}
		
		return aux;
	}
	
//	public String newMessage(MessageType msg, String addrSensor, String addrActuator){
//		if(msg.equals(MessageType.SET_ACTUATOR)){
//			setActuator(addrSensor, addrActuator);
//		}else if(msg.equals(MessageType.SET_SENSOR)){
//			setSensor(addrSensor, addrActuator);
//		}
//		
//		Log.d("myDebug", "yesOrNo: "+this.yesOrNo);
//		return yesOrNo;
//	}

	/*
	 * Creates an alert message with an OK button only
	 */
	private void alertMessageOK(String text){
		this.setMessage(text);
		this.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		this.show();
	}
	
	/*
	 * Method to associate an actuator to a sensor
	 */
	private void setActuator(final String addrSensor, final String addrActuator){
		AlertDialog.Builder b = new AlertDialog.Builder(c);

		b.setMessage(c.getString(R.string.setActuator))
				.setPositiveButton(c.getString(R.string.yes),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								
									aux.associateActuatorToSensor(addrActuator, addrSensor);
							}
						})
				.setNegativeButton(c.getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).show();
	}
	
	/*
	 * Method to associate a sensor to an actuator
	 */
	private void setSensor(final String addrSensor, final String addrActuator){
		AlertDialog.Builder b = new AlertDialog.Builder(c);

		b.setMessage(c.getString(R.string.setSensor))
		.setPositiveButton(c.getString(R.string.yes),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
							aux.associateActuatorToSensor(addrActuator, addrSensor);
					}
				})
		.setNegativeButton(c.getString(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		}).show();
	}
}
