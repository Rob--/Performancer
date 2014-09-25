package io.github.rob__.performancer.storage.info;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.github.rob__.performancer.R;

public class InfoApps {

	ArrayAdapter<String> adapter;
	List<String> info;

	SharedPreferences prefs;
	public void init(Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	List<PackageInfo> appPackages;
	List<ActivityManager.RunningAppProcessInfo> runningApps;
	String[] names;
	String[] packages;

	PackageManager pm;
	PackageInfo pi;

	/**
	 * @return Object array.
	 */
	public Object[] getAppInfo(Context context, int index) {
		pm = context.getPackageManager();
		pi = pm.getInstalledPackages(0).get(index);

		String perms = "";
		String[] permissions = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS).get(index).requestedPermissions;
		if(permissions != null) {
			for (String perm : permissions){
				perms += perm.replaceAll("[.a-z]", "") + "\n";
			}
		}

		String feats = "";
		FeatureInfo[] features = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES).get(index).reqFeatures;
		if(features != null) {
			for (FeatureInfo fi : features) {
				feats += fi.name + "\n";
			}
		}

		return new Object[] {
				pi.applicationInfo.loadLabel(pm).toString(),
				pi.applicationInfo.packageName,
				new File(pi.applicationInfo.publicSourceDir).length() / 1024.0,
				String.valueOf(pi.versionCode),
				pi.versionName,
				pi.applicationInfo.publicSourceDir,
				DateFormat.getDateTimeInstance().format(new Date(pi.firstInstallTime)),
				perms,
				feats,
				String.valueOf(pi.applicationInfo.targetSdkVersion)
		};
	}

	/**
	 * @return String[][], Names & Packages.
	 */
	public String[][] getAppNamesPackages(Context context){
		appPackages = context.getPackageManager().getInstalledPackages(0);
		names       = new String[appPackages.size()];
		packages    = new String[appPackages.size()];
		for(int i = 0; i < appPackages.size(); i++){
			names[i]    = appPackages.get(i).applicationInfo.loadLabel(context.getPackageManager()).toString();
			packages[i] = appPackages.get(i).packageName;
		}
		return new String[][] {
				names,
				packages
		};
	}

	public ArrayAdapter<String> populateListView(Context context, String t){
		adapter = null;
		info = new ArrayList<>();

		/*
			O(nm)
		 */

		if(t.equals("i")) {
			String[] names = getAppNamesPackages(context)[0];
			String[] packages = getAppNamesPackages(context)[1];
			if (!prefs.getBoolean("advanced", false)){
				for(int i = 0; i < names.length; i++) {
					if(!(packages[i].contains("com.android") || packages[i].contains("com.google"))){
						info.add(names[i]);
					}
				}
			} else {
				for(int i = 0; i < names.length; i++) {
					info.add(packages[i]);
				}
			}
		} else {
			String[] apps = getRunningAppList(context);
			List<String> n_ = Arrays.asList(getAppNamesPackages(context)[0]);
			List<String> p_ = Arrays.asList(getAppNamesPackages(context)[1]);
			for(String app : apps){
				if(!app.equals("system")) {
					if (!prefs.getBoolean("advanced", false)){
						try {
							if( !n_.get(p_.indexOf(app)).contains("com.")           &&
								!p_.get(p_.indexOf(app)).contains("com.android")    &&
								!p_.get(p_.indexOf(app)).contains("com.google")) {
								info.add(n_.get(p_.indexOf(app)));
							}
						} catch(IndexOutOfBoundsException e){
							// Is probably a system app or a service.
							// Nevertheless, completely useless.
						}
					} else {
						info.add(app);
					}
				}
			}

		}

		adapter = new ArrayAdapter<>(context, (prefs.getString("theme", "col").equals("col")) ? R.layout.listview_layout_colourful : R.layout.listview_layout_minimalistic, info);
		return adapter;
	}

	/**
	 * @return List of running apps.
	 */
	public String[] getRunningAppList(Context context){
		runningApps = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
		names = new String[runningApps.size()];
		for(int i = 0; i < runningApps.size(); i++){
			names[i] = runningApps.get(i).processName;
		}
		return names;
	}

	DecimalFormat d5 = new DecimalFormat("###,###,###.#####");
	DecimalFormat d2 = new DecimalFormat("###,###,###.##");

	/**
	 * @param value - value to validate
	 * @param t - use two decimal places? true -> 2 dp; false -> 5 dp
	 * @return Validated value (string).
	 */
	public String validateValue(double value, boolean t){
		if(value < 1024){
			return t ? d2.format(value)                 + " KB" : d5.format(value)                  + " KB";
		} else if(value < (1024 * 1024)){
			return t ? d2.format(value / 1024)          + " MB" : d5.format(value / 1024)           + " MB";
		} else {
			return t ? d2.format(value / (1024 * 1024)) + " GB" : d5.format(value / (1024 * 1024))  + " GB";
		}
	}
}
