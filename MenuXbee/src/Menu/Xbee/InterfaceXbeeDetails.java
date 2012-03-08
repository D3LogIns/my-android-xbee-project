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
		tlMain.addView(ll);
		
		this.addView(tlMain);
		
	}

	private void creation(){
		
		TextView tvMyAdress=new TextView(c);
		
		tvMyAdress.setText(cc.getAddress(position));
		
		tlMain.addView(tvMyAdress);
		
	}
}
