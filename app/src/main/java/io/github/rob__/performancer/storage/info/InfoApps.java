package io.github.rob__.performancer.storage.info;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class InfoApps {
	SharedPreferences prefs;

	public void init(Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	List<PackageInfo> appPackages;
	List<ActivityManager.RunningAppProcessInfo> runningApps;
	String[] names;
	String[] packages;
	long[] sizes;

	/**
	 * @return (List) List of app packages. [0] = names[], [1] = sizes[];
	 */
	public Object[] returnAppNamesAndSizes(Context context) {
		appPackages = context.getPackageManager().getInstalledPackages(0);
		names       = new String[appPackages.size()];
		packages    = new String[appPackages.size()];
		sizes       = new long[appPackages.size()];
		for(int i = 0; i < appPackages.size(); i++){
			names   [i] = appPackages.get(i).applicationInfo.loadLabel(context.getPackageManager()).toString();
			packages[i] = appPackages.get(i).packageName;
			sizes   [i] = new File(appPackages.get(i).applicationInfo.publicSourceDir).length() / 1024;
		}
		return new Object[] { names, packages, sizes };
	}

	/**
	 * @return (List<RunningAppProcessInfo>) List of running apps.
	 */
	public String[] returnRunningAppList(Context context){
		runningApps = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
		names = new String[runningApps.size()];
		for(int i  = 0; i < runningApps.size(); i++){
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
