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
		}
		
	}
	
	/*
	 * Method for creating an alert message, requiring the Sensor and Actuator addresses
	 */
	public AuxiliarXBee newMessage(MessageType msg, String addrSensor, String addrActuator){
		
		if(msg.equals(MessageType.SET_ACTUATOR)){
//			setActuator(addrSensor, addrActuator);
			setActuatorAndSensor(addrSensor, addrActuator, c.getString(R.string.setActuator));
		}else if(msg.equals(MessageType.SET_SENSOR)){
//			setSensor(addrSensor, addrActuator);
			setActuatorAndSensor(addrSensor, addrActuator, c.getString(R.string.setSensor));
		}else if(msg.equals(MessageType.REMOVE_ACTUATOR)){
			removeActuatorAndSensor(addrSensor, addrActuator, c.getString(R.string.removeActuator));
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
	 * Method to associate an actuator to a sensor
	 */
//	private void setActuator(final String addrSensor, final String addrActuator){
//		AlertDialog.Builder b = new AlertDialog.Builder(c);
//
//		b.setMessage(c.getString(R.string.setActuator))
//				.setPositiveButton(c.getString(R.string.yes),
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								
//									aux.associateActuatorToSensor(addrActuator, addrSensor);
//							}
//						})
//				.setNegativeButton(c.getString(R.string.no), new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int id) {
//						dialog.cancel();
//					}
//				}).show();
//	}
	
	/*
	 * Method to associate a sensor to an actuator
	 */
//	private void setSensor(final String addrSensor, final String addrActuator){
//		AlertDialog.Builder b = new AlertDialog.Builder(c);
//
//		b.setMessage(c.getString(R.string.setSensor))
//		.setPositiveButton(c.getString(R.string.yes),
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog,
//							int which) {
//							aux.associateActuatorToSensor(addrActuator, addrSensor);
//					}
//				})
//		.setNegativeButton(c.getString(R.string.no), new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				dialog.cancel();
//			}
//		}).show();
//	}
	
	/*
	 * 
	 * Sets an Actuator to a Sensor and vice-versa 
	 * 
	 */
	private void setActuatorAndSensor(final String addrSensor, final String addrActuator, String text){
		AlertDialog.Builder b = new AlertDialog.Builder(c);
		
				b.setMessage(text)
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
					public void onClick(DialogInterface dialog,
							int which) {
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
