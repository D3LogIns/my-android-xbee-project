package XBee.Configurator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class XBeeConfiguratorActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Context c=this;

		
		/*
		 * BUTTONS INICIALIZATION
		 */
		final Button bOK = (Button) findViewById(R.id.bOKPan);
		Button bDetect = (Button) findViewById(R.id.bDetectDevices);
		
		/*
		 * TEXT BOX'S INICIALIZATION
		 */
		
		final EditText etPan=(EditText)findViewById(R.id.editPan);
		
		
		/*
		 * TEXT BOX'S LISTENERS
		 */
		
		etPan.addTextChangedListener(new TextWatcher() {

			// METHOD THAT CHECKS IF THE TEXT IS CHANGED
			public void afterTextChanged(Editable s) {
				// IF TEXT SIZE IS HIGHER THAN 5, APPLICATIOAN LAUNCHES AN ERROR
				if (s.length() > 5) {
					new AlertMessage(c).newMessage(MessageType.TEXT_OUT_OF_BOUNDS);
					etPan.setText("");
					// IF TEXT SIZE IS HIGHER THAN 0, OK BUTTON TURNS ACTIVE
				} else if (s.length() > 0)
					bOK.setEnabled(true);
				// IF TEXT SIZE IS LOWER OR EQUAL TO 0, OK BUTTON TURNS DEACTIVATED
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
		bOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				/*
				 * if (inBoundsPanID(etPan.getText().toString())) {
				 * changeXbeePanID(Integer
				 * .parseInt(etPan.getText().toString()));
				 * 
				 * } else new
				 * AlertMessage(c).newMessage(MessageType.PAN_ID_OUT_OF_BOUNDS);
				 * 
				 * etPan.setText("");
				 */
			}
		});

		bDetect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}

		});
	}
}