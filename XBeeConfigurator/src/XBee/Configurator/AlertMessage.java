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
		}else if(msg.equals(MessageType.ADDRESS_NOT_ACCEPTABLE)){
			this.alertMessageOK(c.getString(R.string.addressNotAcceptable));
		}else if(msg.equals(MessageType.ADDRESS_OUT_OF_BOUNDS)){
			this.alertMessageOK(c.getString(R.string.addressOutOfBounds));
		}else if(msg.equals(MessageType.ADDRESS_SHORT_LENGTH)){
			this.alertMessageOK(c.getString(R.string.addressShortLength));
		}else if(msg.equals(MessageType.ACTUATORS_LIMIT_REACHED)){
			this.alertMessageOK(c.getString(R.string.actuatorLimitReached));
		}else if(msg.equals(MessageType.SENSOR_LIMIT_REACHED)){
			this.alertMessageOK(c.getString(R.string.sensorLimitReached));
		}else if(msg.equals(MessageType.REPEATED_ADDRESS)){
			this.alertMessageOK(c.getString(R.string.repeatedAddress));
		}
		
	}
	
	/*
	 * Method for creating an alert message, requiring the Sensor and Actuator addresses
	 */
	public AuxiliarXBee newMessage(MessageType msg, String addrSensor, String addrActuator, String type){
		
		if(msg.equals(MessageType.SET_ACTUATOR)){
//			setActuator(addrSensor, addrActuator);
			setActuatorAndSensor(addrSensor, addrActuator, type, c.getString(R.string.setActuator)+"\n"+addrActuator);

		}else if(msg.equals(MessageType.REMOVE_ACTUATOR)){
			removeActuatorAndSensor(addrSensor, addrActuator, c.getString(R.string.removeActuator)+"\n"+addrActuator);
		}
		
		return aux;
	}

	/*
	 * Creates a simple alert message with an OK button only
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
	 * 
	 * Sets an Actuator to a Sensor and vice-versa 
	 * 
	 */
	private void setActuatorAndSensor(final String addrSensor, final String addrActuator, final String type, String text){
		AlertDialog.Builder b = new AlertDialog.Builder(c);
		
				b.setMessage(text)
				.setPositiveButton(c.getString(R.string.yes),
						new DialogInterface.OnClickListener() {
		
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
									aux.associateActuatorToSensor(addrActuator, addrSensor, type);
							}
						})
				.setNegativeButton(c.getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).show();
	}
	
	/*
	 * 
	 * Dissassociates an actuator from a sensor
	 * 
	 */
	private void removeActuatorAndSensor(final String addrSensor,final String addrActuator, String text) {
		AlertDialog.Builder b = new AlertDialog.Builder(c);
		
		b.setMessage(text)
		.setPositiveButton(c.getString(R.string.yes),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
							aux.removeActuatorFromSensor(addrActuator, addrSensor);
					}
				})
		.setNegativeButton(c.getString(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		}).show();
		
	}
}
