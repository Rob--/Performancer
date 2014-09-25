package io.github.rob__.performancer.storage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import io.github.rob__.performancer.R;
import io.github.rob__.performancer.Tools;
import io.github.rob__.performancer.storage.info.InfoApps;

public class AppsFragment extends Fragment {

	SharedPreferences prefs;

	final Handler handler = new Handler();
	InfoApps infoApps = new InfoApps();
	Tools tools = new Tools();

	Parcelable lvState;
	ListView lvApps;
	Button btnSwitchApps;
	Object[] ai;

	View v;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//prefs = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

		if(prefs.getString("theme", "col").equals("col")){
			// Matching Action Bars
			//setTheme(R.style.AppThemeColourful);
			v = inflater.inflate(R.layout.fragment_apps_colourful, container, false);
		} else {
			//setTheme(R.style.AppThemeMinimalistic);
			v = inflater.inflate(R.layout.fragment_apps_minimalistic, container, false);
		}

		infoApps.init(v.getContext());
		// No sliding menu - thanks to TabActivity
		// we can attach it directly

		lvApps          = (ListView) v.findViewById(R.id.lvApps);
		btnSwitchApps   = (Button) v.findViewById(R.id.btnSwitchApps);

		handler.post(update);

		btnSwitchApps.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				animate();
				handler.post(update);
			}
		});

		lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String[][] np = infoApps.getAppNamesPackages(v.getContext());

				if(!prefs.getBoolean("advanced", false)){
					int index;
					if(lvApps.getItemAtPosition(position).toString().contains("com.") || lvApps.getItemAtPosition(position).toString().contains("android.")){
						List<String> p = Arrays.asList(infoApps.getAppNamesPackages(v.getContext())[1]);
						index = p.indexOf(lvApps.getItemAtPosition(position));
						ai = infoApps.getAppInfo(v.getContext(), index);
					} else {
						ai = infoApps.getAppInfo(v.getContext(), Arrays.asList(np[0]).indexOf(lvApps.getItemAtPosition(position).toString()));
					}
				} else {
					ai = infoApps.getAppInfo(v.getContext(), Arrays.asList(np[1]).indexOf(lvApps.getItemAtPosition(position).toString()));
				}

				np = new String[][] {};

				tools.MsgBox(   Html.fromHtml(
									"<small>Name:</small><br>"          + ai[0] + "<br><br>" +
									"<small>Package:</small><br>"       + ai[1] + "<br><br>" +
									"<small>Size:</small><br>"          + infoApps.validateValue((double) ai[2], true) + "<br><br>" +
									"<small>Version code:</small><br>"  + ai[3] + "<br><br>" +
									"<small>Version name:</small><br>"  + ai[4] + "<br><br>" +
									"<small>Directory:</small><br>"     + ai[5] + "<br><br>" +
									"<small>Installed:</small><br>"     + ai[6] + "<br><br>" +
									(
										(ai[7] != "") ? "Permissions:<br><small>" + ai[7] + "</small><br><br>" : ""
									) +
									(
										(ai[8] != "") ? "<small>Features:</small><br>" + ai[8] + "<br><br>" : ""
									) +
									"<small>Target SDK Version:</small><br>" + ai[9]
							),
								Html.fromHtml   (
									ai[0] + ",<br>build " + ai[3]
							),
						v.getContext()
				);
			}
		});

		lvApps.setLongClickable(true);
		lvApps.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				String p;
				String item = lvApps.getItemAtPosition(position).toString();
				if(!prefs.getBoolean("advanced", false)) {
					List<String> names = Arrays.asList(infoApps.getAppNamesPackages(v.getContext())[0]);
					List<String> packages = Arrays.asList(infoApps.getAppNamesPackages(v.getContext())[1]);
					p = packages.get(names.indexOf(item));
				} else {
					List<String> packages = Arrays.asList(infoApps.getAppNamesPackages(v.getContext())[1]);
					p = packages.get(packages.indexOf(item));
				}

				try {
					if(!p.equals(getActivity().getApplication().getPackageName())) {
						Intent il = getActivity().getPackageManager().getLaunchIntentForPackage(p);
						startActivity(il);
					}
				} catch(NullPointerException e){
					// No such "application" per se, exists for this package.
					// It's either a service, or a something
					// Don't even print a stack trace, utterly useless.
					e.printStackTrace();
					// We would show the user a message box, but what would it say?
					// "The selected application is (or has a) service."
				}
				return true;
			}
		});

		return v;
	}

	public void animate(){
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(btnSwitchApps.getText().toString().contains("Running")){
					btnSwitchApps.setText(getResources().getString(R.string.showInstalled));
					btnSwitchApps.startAnimation(tools.fadeIn());
				} else {
					btnSwitchApps.setText(getResources().getString(R.string.showRunning));
					btnSwitchApps.startAnimation(tools.fadeIn());
				}
			}
		});
	}

	Runnable update = new Runnable(){
		@Override
		public void run(){
			if(btnSwitchApps.getText().toString().contains("Running")){
				lvState = lvApps.onSaveInstanceState();
				lvApps.setAdapter(infoApps.populateListView(v.getContext(), "i"));
				lvApps.onRestoreInstanceState(lvState);
			} else {
				lvState = lvApps.onSaveInstanceState();
				lvApps.setAdapter(infoApps.populateListView(v.getContext(), "r"));
				lvApps.onRestoreInstanceState(lvState);
			}
		}
	};
	public static AppsFragment newInstance() {
		return new AppsFragment();
	}
}
