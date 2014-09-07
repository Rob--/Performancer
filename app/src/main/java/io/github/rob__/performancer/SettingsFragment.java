package io.github.rob__.performancer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment {
	SharedPreferences prefs;
	String theme;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		theme = (prefs.getString("theme", "col").equals("col")) ? "Colourful" : "Minimalistic";

		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onPause(){
		if(!theme.equals((prefs.getString("theme", "col").equals("col")) ? "Colourful" : "Minimalistic")){
			getActivity().finish();
			startActivity(new Intent(getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
			getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
		super.onPause();
	}
}
