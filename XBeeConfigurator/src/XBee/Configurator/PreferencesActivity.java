package XBee.Configurator;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class PreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			
			return true;
		}
		return false;

	}
}