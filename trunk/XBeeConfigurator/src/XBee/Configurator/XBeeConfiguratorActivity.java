package XBee.Configurator;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class XBeeConfiguratorActivity extends Activity {

	final Context c = this;
	ConnectionClass cc;
	AuxiliarXBee auxXBee;
	String language="";
	AuxiliarLanguage aux;
	AlertMessage alert;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		cc=new ConnectionClass(c);
		
		auxXBee=new AuxiliarXBee();
		
		//aux=new AuxiliarLanguage(c);
		
		//aux.setLanguage();
		
		//language=aux.getLanguage();
		
		alert=new AlertMessage(c);
		
		setContentView(R.layout.main);
		this.inicialization();
	}

	public void teste(){
		
	}
	

	
	private void inicialization() {

		/*
		 * XBEE DETECTED DEVICES TABLE
		 */
		final TableLayout tlXBeeDevices = (TableLayout) findViewById(R.id.tlXBeeDevices);

		/*
		 * BUTTONS INICIALIZATION
		 */
		final Button bOK = (Button) findViewById(R.id.bOKPan);
		Button bDetect = (Button) findViewById(R.id.bDetectDevices);

		/*
		 * TEXT BOX'S INICIALIZATION
		 */

		final EditText etPan = (EditText) findViewById(R.id.editPan);

		/*
		 * TEXT BOX'S LISTENERS
		 */

		etPan.addTextChangedListener(new TextWatcher() {

			// METHOD THAT CHECKS IF THE TEXT IS CHANGED
			public void afterTextChanged(Editable s) {
				// IF TEXT SIZE IS HIGHER THAN 5, APPLICATIOAN LAUNCHES AN ERROR
				if (s.length() > 5) {
					alert
							.newMessage(MessageType.TEXT_OUT_OF_BOUNDS);
					etPan.setText("");
					// IF TEXT SIZE IS HIGHER THAN 0, OK BUTTON TURNS ACTIVE
				} else if (s.length() > 0)
					bOK.setEnabled(true);
				// IF TEXT SIZE IS LOWER OR EQUAL TO 0, OK BUTTON TURNS
				// DEACTIVATED
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
		
		//OK BUTTON
		bOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (inBoundsPanID(etPan.getText().toString())) {
					changeXbeePanID(Integer
							.parseInt(etPan.getText().toString()));

				} else
					alert
							.newMessage(MessageType.PAN_ID_OUT_OF_BOUNDS);

				etPan.setText("");

			}

			private boolean inBoundsPanID(String string) {
				if (Integer.parseInt(string) > 5000
						|| Integer.parseInt(string) <= 0)
					return false;
				return true;
			}

			private void changeXbeePanID(int parseInt) {

			}
		});

		//DETECT BUTTON
		bDetect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				
				auxXBee.clearList();
				
				tlXBeeDevices.removeAllViews();
				
				cc.searchXBeeDevices();
				auxXBee.setList(cc.getList());
				
				if (auxXBee.getListSize() < 0)
					alert
							.newMessage(MessageType.DEVICES_NOT_DETECTED);
				else {
					for (int i = 0; i != auxXBee.getListSize(); i++) {
						TableRow r = new TableRow(c);
						final TextView addr=new TextView(c);
						TextView type=new TextView(c);
						TextView ss=new TextView(c);
						
						addr.setText(auxXBee.getAddress(i));
						addr.setId(i);
						addr.setClickable(true);
						addr.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								
								callXBeeDetails(addr.getId());

							}


						});
						
						type.setText(auxXBee.getType(i));
						
						ss.setText(auxXBee.getSignalStrength(i));
						
						r.addView(addr);
						r.addView(type);
						r.addView(ss);
						tlXBeeDevices.addView(r);

					}
				}
			}

		});
	
	}
	
private void callXBeeDetails(int position){
	Intent i = new Intent(c, XbeeDetailsActivity.class);
	Bundle b= new Bundle();
	
	i.putExtra("position", position);
	
	b.putSerializable("auxiliar", auxXBee);
	
	i.putExtras(b);
	
	this.startActivityForResult(i, MODE_PRIVATE);

}

protected void onActivityResult(int requestCode, int resultCode, Intent intent){
	Bundle extras=intent.getExtras();
	auxXBee=(AuxiliarXBee) extras.getSerializable("auxiliar");
	
}
	
	
//public void onResume(){
//	super.onResume();
//	
//	if(!language.equals(aux.getLanguage()))
//		refresh();
//	
//}
	
private void refresh() {
//	ViewGroup vg =(ViewGroup) findViewById (R.id.main);
	 
//	vg.invalidate();
//	setContentView(R.layout.main);
//	this.startActivity(getIntent());
//	this.finish();
	
}
	/*
	public boolean onCreateOptionsMenu(Menu menu) {  
	    //menu.add(1, new Languages().getPreferences(""));  
	    //return super.onCreateOptionsMenu(menu);
		 MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.menu, menu);
		    return true;
	  }
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.Preferences:
	            preferencesMenu();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
		//preferencesMenu();
		//return true;
	}

	private void preferencesMenu(){
		Intent i = new Intent(c, PreferencesActivity.class);
		//setResult(Activity.RESULT_OK, i);
		this.startActivityForResult(i, MODE_PRIVATE);
		//startActivityForResult(i, 0);
	}*/

}