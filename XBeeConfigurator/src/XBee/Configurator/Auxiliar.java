package XBee.Configurator;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

public class Auxiliar {

	Context c;
	Locale l;
	SharedPreferences shared;
	Configuration config = new Configuration();

	public Auxiliar(Context c) {
		this.c = c;
		this.shared = PreferenceManager.getDefaultSharedPreferences(c);
	}

	public String getLanguage() {
		return shared.getString("listLanguage", null);
	}

	public boolean changeLanguage(String s) {
		if (!s.equals(shared.getString("listLanguage", null)))
			if (s.equals("EN")) {
				updateConfiguration("en_US");
				return true;
			} else if (s.equals("PT")) {
				updateConfiguration("pt_PT");
				return true;
			}
		return false;
	}

	public void setLanguage() {

		if (getLanguage().equals("EN")) {
			updateConfiguration("en_US");

		} else if (getLanguage().equals("PT")) {
			updateConfiguration("pt_PT");
		} else {
		}
	}

	private void updateConfiguration(String lang) {
		l=new Locale(lang);
		Locale.setDefault(l);

		config.locale = l;
		c.getResources().updateConfiguration(config, null);
	}
}
