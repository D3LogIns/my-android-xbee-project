package Menu.Xbee;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

public class InterfaceXbeeDetails extends ScrollView{

	private Context c;
	private ConnectionClass cc;
	private int position;
	
	
	private TableLayout tlMain;
	
	private LinearLayout ll;
	

	
	
	public InterfaceXbeeDetails(Context context, ConnectionClass cc, int position) {
		super(context);
		
		this.c=context;
		this.cc=cc;
		this.position=position;
		
		this.initializeGlobalVars();
		this.creation();
	}

	private void initializeGlobalVars() {
		ll=new LinearLayout(c);
		ll.setOrientation(LinearLayout.VERTICAL);
		
		tlMain=new TableLayout(c);
		
		TextView tvMyAdress = new TextView(c);
		TextView tvMyType = new TextView(c);
		
		tvMyAdress.setText(cc.getAddress(position));
		tvMyType.setText(cc.getType(position));
		
		
		ghgh
		
		this.addView(tlMain);
		tlMain.addView(ll);
		
		tlMain.addView(tvMyAdress);
		tlMain.addView(tvMyType);
		
		
	}

	private void creation(){
		

	}
}
