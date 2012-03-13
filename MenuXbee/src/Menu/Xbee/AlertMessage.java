package Menu.Xbee;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertMessage extends AlertDialog {

	Context c;
	ConnectionClass cc;
	String addrSensor;
	String addrActuator;

	protected AlertMessage(Context context) {
		super(context);
		this.c = context;

	}
	
	protected AlertMessage(Context context, ConnectionClass cc, String addrSensor, String addrActuator) {
		super(context);
		this.c = context;
		this.cc=cc;
		this.addrSensor=addrSensor;
		this.addrActuator=addrActuator;

	}


	public Object newMessage(MessageType msg) {

		if (msg.equals(MessageType.COORDINATOR_NOT_DETECTED)) {
			coordNotDetectedMessage();

		} else if (msg.equals(MessageType.PAN_ID_OUT_OF_BOUNDS)) {
			panIDOutOfBounds();

		} else if (msg.equals(MessageType.TEXT_OUT_OF_BOUNDS)) {
			textOutOfBounds();
		} else if (msg.equals(MessageType.SET_ACTUATOR)) {
			setActuator();
		} else if (msg.equals(MessageType.SET_SENSOR)) {
			return (setSensor());
		} else if (msg.equals(MessageType.DELETE_ACTUATOR)) {
			return (deleteActuator());
		} else if (msg.equals(MessageType.DELETE_SENSOR)) {
			return (deleteSensor());
		}
		return -1;
	}

	private void coordNotDetectedMessage() {
		this.setMessage("Coordinator not found!\nApplication will be terminated.");
		this.setButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		this.show();
	}

	private void panIDOutOfBounds() {
		this.setMessage("PAN ID is not acceptable. Values must be between 1 and 50000.");
		this.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		this.show();
	}

	private void textOutOfBounds() {
		this.setMessage("Value is too high, please insert lower values.");
		this.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		this.show();
	}

	private void setActuator() {

		AlertDialog.Builder b = new AlertDialog.Builder(c);

		b.setMessage("Are you sure you want to associate this actuator?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
									cc.associateActuatorToSensor(addrActuator, addrSensor);
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).show();
	}

	private int setSensor() {

		return 0;
	}

	private int deleteActuator() {

		return 0;
	}

	private int deleteSensor() {

		return 0;
	}

}
