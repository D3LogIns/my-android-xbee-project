package XBee.Configurator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertMessage extends AlertDialog{

	Context c;
	String language;
	AuxiliarXBee aux;
	
	public AlertMessage(Context c) {
		super(c);
		this.c=c;
	}
	
	public AlertMessage(Context c, AuxiliarXBee a){
		super(c);
		this.c=c;
		this.aux=a;
	}
	
	public void newMessage(MessageType msg){
		
		if(msg.equals(MessageType.COORDINATOR_NOT_DETECTED)){
			coordenatorNotDetected();
		}else if(msg.equals(MessageType.PAN_ID_OUT_OF_BOUNDS)){
			panIdOutOfBounds();
		}else if(msg.equals(MessageType.TEXT_OUT_OF_BOUNDS)){
			textOutOfBounds();
		}else if(msg.equals(MessageType.DEVICES_NOT_DETECTED)){
			devicesNotDetected();
		}else if(msg.equals(MessageType.DEVICE_NOT_FOUND)){
			deviceNotFound();
		}else if(msg.equals(MessageType.PAN_ID_NOT_CHANGED)){
			panIdNotChanged();
		}
		
	}
	




	public AuxiliarXBee newMessage(MessageType msg, String addrSensor, String addrActuator){
		
		if(msg.equals(MessageType.SET_ACTUATOR)){
			setActuator(addrSensor, addrActuator);
		}else if(msg.equals(MessageType.SET_SENSOR)){
			setSensor(addrSensor, addrActuator);
		}
		
		return aux;
	}

	private void devicesNotDetected() {
		this.setMessage(c.getString(R.string.devicesNotDetected));
		this.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		this.show();
		
	}

	private void coordenatorNotDetected() {
		this.setMessage(c.getString(R.string.coordNotFound));
		this.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		this.show();
	}
	
	private void panIdOutOfBounds(){
		this.setMessage(c.getString(R.string.panIdOutOfBounds));
		this.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		this.show();
	}

	private void textOutOfBounds(){
		this.setMessage(c.getString(R.string.textOutOfBounds));
		//this.setMessage(new Languages().getMessageAlert_ValueTooHigh(language));
		this.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		this.show();
	}
	
	private void deviceNotFound() {
		this.setMessage(c.getString(R.string.deviceNotFound));
		this.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		this.show();
	}
	
	private void panIdNotChanged() {
		this.setMessage(c.getString(R.string.panIdNotChanged));
		this.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		this.show();
	}
	
	
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
