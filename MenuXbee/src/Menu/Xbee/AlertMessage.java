package Menu.Xbee;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertMessage extends AlertDialog {

	Context c;

	protected AlertMessage(Context context, ErrorTypes e) {
		super(context);
		// TODO Auto-generated constructor stub
		this.c = context;

		if (e.equals(ErrorTypes.COORDINATOR_NOT_DETECTED)) {
			coordNotDetectedMessage();
			
		} else if (e.equals(ErrorTypes.PAN_ID_OUT_OF_BOUNDS)) {
			panIDOutOfBounds();
			
		}else if(e.equals(ErrorTypes.TEXT_OUT_OF_BOUNDS)){
			textOutOfBounds();
		}
		
		this.show();
	}

	public void coordNotDetectedMessage() {
		this.setMessage("Coordinator not found!\nApplication will be terminated.");
		this.setButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
	}

	public void panIDOutOfBounds() {
		this.setMessage("PAN ID is not acceptable. Values must be between 1 and 50000.");
		this.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void textOutOfBounds() {
		this.setMessage("Value is too high, please insert lower values.");
		this.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
