package Menu.Xbee;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class XbeeDetailsActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int position=this.getIntent().getExtras().getInt("position");
		
		ConnectionClass cc=(ConnectionClass) this.getIntent().getExtras().getSerializable("connection");

		String adress=cc.getAddress(position);
				
		
		ScrollView sv=new ScrollView(this);
        LinearLayout ll=new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        
        sv.addView(ll);
        
        TextView tv=new TextView(this);
        tv.setText("Dynamic Layout");
        
        ll.addView(tv);
        
        EditText et=new EditText(this);
        
        et.setText(adress);
        
        ll.addView(et);
        
        setContentView(sv);
	}
}
