package XBee.Configurator;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class PreferencesActivity extends PreferenceActivity {
	
	Context c=this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.getLanguage();

		addPreferencesFromResource(R.xml.preferences);

		Preference language = (Preference) this.findPreference("listLanguage");

		
//		language.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//
//			public boolean onPreferenceClick(Preference preference) {
////				SharedPreferences shared= PreferenceManager.getDefaultSharedPreferences(c);
////				System.out.println("Language "+shared.getString("listLanguage", null));
//
//				//getLanguage();
//				//refresh();
//				
//				SharedPreferences customSharedPreference = getSharedPreferences("listLanguage", Activity.MODE_PRIVATE);
//				SharedPreferences.Editor editor = customSharedPreference.edit();
//
//				editor.commit();
//				
//				SharedPreferences shared= PreferenceManager.getDefaultSharedPreferences(c);
//				System.out.println("Language "+shared.getString("listLanguage", null));
//				
//				return true;
//			}
//		});
		
		language.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

			@Override
			public boolean onPreferenceChange(Preference pref, Object obj) {
				// TODO Auto-generated method stub
				System.out.println(pref.getKey());
				System.out.println(obj.toString());
				SharedPreferences shared= PreferenceManager.getDefaultSharedPreferences(c);
				System.out.println("Language "+shared.getString("listLanguage", null));
				
				
				setLanguage(obj.toString());
				
				refresh();
				
				return true;
			}
			
			
		});

	}
	
	private void getLanguage(){
		SharedPreferences shared= PreferenceManager.getDefaultSharedPreferences(this);

		
		if(shared.getString("listLanguage", null).equals("EN")){
			Locale l=new Locale("en_US");
			Locale.setDefault(l);
			
			Configuration config2 = new Configuration();
		    config2.locale = l;
		    this.getResources().updateConfiguration(config2, null);
		    
		}else if(shared.getString("listLanguage", null).equals("PT")){
			Locale l=new Locale("pt_PT");
			Locale.setDefault(l);
			
			Configuration config2 = new Configuration();
		    config2.locale = l;
		    this.getResources().updateConfiguration(config2, null);
		}else{}
	}
	
	private void setLanguage(String s){
		if(s.equals("EN")){
			Locale l=new Locale("en_US");
			Locale.setDefault(l);
			
			Configuration config2 = new Configuration();
		    config2.locale = l;
		    this.getResources().updateConfiguration(config2, null);
		}else if(s.equals("PT")){
			Locale l=new Locale("pt_PT");
			Locale.setDefault(l);
			
			Configuration config2 = new Configuration();
		    config2.locale = l;
		    this.getResources().updateConfiguration(config2, null);
		}
	}
	
	private void refresh(){
		startActivity(getIntent());
		finish();
	}
}