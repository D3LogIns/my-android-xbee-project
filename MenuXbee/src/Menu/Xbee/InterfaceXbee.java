package Menu.Xbee;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class InterfaceXbee extends ScrollView {


	private Context c;
	private ConnectionClass cc = new ConnectionClass();

	private TextView tvDetDev;

	private TableRow rowDevices;

	private TableLayout tlMain;
	private TableLayout tlDevice;


	public InterfaceXbee(Context context) {
		super(context);
		this.c = context;
		Creation();
	}

	public void Creation() {
		// Inicializa‹o dos elementos

		tlMain = new TableLayout(c);
		tlDevice = new TableLayout(c);

		// Botao
		final Button bOK = new Button(c);
		Button bDetectDevice = new Button(c);

		// Caixa de Texto
		final EditText etPan = new EditText(c);

		// TextViews
		TextView tvCoordinator = new TextView(c);
		TextView tvPan = new TextView(c);
		TextView tvAddrText = new TextView(c);
		TextView tvAddress = new TextView(c);
		TextView tvOpText = new TextView(c);
		TextView tvOP = new TextView(c);
		tvDetDev = new TextView(c);

		LinearLayout ll = new LinearLayout(c);
		ll.setOrientation(LinearLayout.VERTICAL);

		TableRow rowPanID = new TableRow(c);
		TableRow rowAddr = new TableRow(c);
		TableRow rowPanOP = new TableRow(c);
		rowDevices = new TableRow(c);

		tlMain.setColumnStretchable(0, true);
		tlMain.setColumnStretchable(1, true);
		tlMain.setColumnStretchable(2, true);

		
		 tlDevice.setColumnStretchable(0, true);
		 tlDevice.setColumnStretchable(1, true);
		 tlDevice.setColumnStretchable(2, true);
		 

		this.addView(tlMain);

		tlMain.addView(ll);

		tlMain.addView(rowPanID);
		tlMain.addView(rowAddr);
		tlMain.addView(rowPanOP);
		tlMain.addView(rowDevices);
		tlMain.addView(tlDevice);

		tvCoordinator.setText("Coordinator");

		ll.addView(tvCoordinator);

		tvPan.setText("PAN ID");
		tvPan.setHeight(50);
		rowPanID.addView(tvPan);

		// Configura‹o da caixa de texto
		etPan.setInputType(InputType.TYPE_CLASS_NUMBER);
		etPan.setGravity(Gravity.RIGHT);
		etPan.setHeight(50);
		etPan.addTextChangedListener(new TextWatcher() {

			// MƒTODO QUE VERIFICA SE O TEXTO ƒ ALTERADO
			public void afterTextChanged(Editable s) {
				// SE O TEXTO INSERIDO FOR MAIOR QUE 5 CARACTERES LAN‚A UM ERRO
				if (s.length() > 5) {
					new AlertMessage(c, ErrorTypes.TEXT_OUT_OF_BOUNDS);
					etPan.setText("");
					// SE O TAMANHO FOR MAIOR QUE 0 O BOTAO OK FICA ACTIVO
				} else if (s.length() > 0)
					bOK.setEnabled(true);
				// SE O TAMANHO FOR MENOR OU IGUAL A 0 O BOTAO OK FICA DESACTIVO
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

		rowPanID.addView(etPan);

		// CONFIGURACAO DAS FUNCIONALIDADES DO BOTAO
		bOK.setEnabled(false);
		bOK.setText("OK");
		bOK.setHeight(50);
		bOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (inBoundsPanID(etPan.getText().toString())) {
					changeXbeePanID(Integer
							.parseInt(etPan.getText().toString()));

				} else
					new AlertMessage(c, ErrorTypes.PAN_ID_OUT_OF_BOUNDS);

				etPan.setText("");
			}

		});
		rowPanID.addView(bOK);

		tvAddrText.setText("Address");
		tvAddrText.setHeight(50);
		rowAddr.addView(tvAddrText);

		tvAddress.setText("0132A20B6D35");
		tvAddress.setGravity(Gravity.RIGHT);
		tvAddress.setHeight(50);
		rowAddr.addView(tvAddress);

		tvOpText.setText("Operating PAN ID");
		tvOpText.setHeight(50);
		rowPanOP.addView(tvOpText);

		tvOP.setText("500000");
		tvOP.setGravity(Gravity.RIGHT);
		tvOP.setHeight(50);
		rowPanOP.addView(tvOP);

		bDetectDevice.setText("Detect");
		rowDevices.addView(bDetectDevice);

		tvDetDev.setHeight(50);
		rowDevices.addView(tvDetDev);

		bDetectDevice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				clearTable();
				connection();
				retrieveXbees();

			}

		});

		// this.retrieveXbees(tvDetDev, tvAddtv, tvType, tvSS, rowDD, tlDevice);

	}

	// -------------------------------------------------------------------
	// Fun‹o que vai inicializar a comunica‹o com o XBee
	protected void changeXbeePanID(int parseInt) {

	}

	private void clearTable() {

			if(tlDevice.getChildCount()>0){
				tlDevice.removeAllViews();
				cc.clearList();
			}

		/*
		 * if (tlDevice.getChildCount() > 0){
		 * 
		 * for (int i = (tlDevice.getChildCount()-1); i >=0; i--) { View child =
		 * tlDevice.getChildAt(i); if (child instanceof TableRow){ ((TableRow)
		 * child).removeAllViews(); tlDevice.removeView(child); }
		 * 
		 * tlDevice.removeViewAt(i); }
		 * 
		 * }
		 */

	}

	private void retrieveXbees() {
		int size = this.getListSize();

		if (size != 0) {

			tvDetDev.setText("Detected Devices");


			TextView tvAddtv = new TextView(c);
			TextView tvType = new TextView(c);
			TextView tvSS = new TextView(c);
			TableRow rowDD = new TableRow(c);
			
			tvAddtv = new TextView(c);
			tvType = new TextView(c);
			tvSS = new TextView(c);
			rowDD = new TableRow(c);
			

			rowDD.addView(tvAddtv);
			rowDD.addView(tvType);
			rowDD.addView(tvSS);

			tlDevice.addView(rowDD);

			tvAddtv.setText("Address");
			tvAddtv.setGravity(Gravity.LEFT);

			tvType.setText("Type");
			tvType.setGravity(Gravity.LEFT);

			tvSS.setText("Signal Strength");
			tvSS.setGravity(Gravity.LEFT);


			for (int i = 0; i < size; i++) {
				TableRow r = new TableRow(c);
				final TextView vAddr = new TextView(c);
				TextView vType = new TextView(c);
				TextView vSS = new TextView(c);
				

				vAddr.setId(i);
				vAddr.setClickable(true);
				vAddr.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						String addr = cc.getAddress(vAddr.getId());
						Intent i = new Intent(c, XbeeDetailsActivity.class);
						c.startActivity(i);
					}

				});

				vAddr.setText(cc.getAddress(i));
				vType.setText(cc.getType(i));
				vSS.setText(cc.getSignalStrength(i));

				r.addView(vAddr);
				r.addView(vType);
				r.addView(vSS);

				tlDevice.addView(r);

			}

		} else {
			tvDetDev.setText("Devices Not Found");
		}
	}

	private boolean inBoundsPanID(String sID) {
		int id = Integer.parseInt(sID);
		if (id > 0 && id <= 50000) {
			return true;
		} else
			return false;
	}

	private void connection() {

		cc.SeekNDestroy();

	}

	private int getListSize() {
		return cc.getListSize();
	}

}
