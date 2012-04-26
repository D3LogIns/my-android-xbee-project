package XBee.Configurator;


import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {

	Context c = this;
	AuxiliarLanguage aux;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		aux = new AuxiliarLanguage(c);

		aux.setLanguage();

		addPreferencesFromResource(R.xml.preferences);

		Preference language = (Preference) this.findPreference("listLanguage");

		language.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference pref, Object obj) {
				// TODO Auto-generated method stub
				// System.out.println(pref.getKey());
				// System.out.println(obj.toString());
				// SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(c);
				// System.out.println("Language "+shared.getString("listLanguage",
				// null));

				if(aux.changeLanguage(obj.toString()))
					refresh();

				return true;
			}

		});

	}
	

	private void refresh() {
		this.startActivity(getIntent());
		this.finish();
	}

//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		switch (requestCode) {
//		case (XBeeDetailsActivity.class): {
//			if (resultCode == Activity.RESULT_OK) {
//				// TODO Extract the data returned from the child Activity.
//			}
//			break;
//		}
//		}
//	}
}