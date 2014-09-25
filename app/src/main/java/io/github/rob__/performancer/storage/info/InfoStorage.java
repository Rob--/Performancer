package io.github.rob__.performancer.storage.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.github.rob__.performancer.R;

public class InfoStorage {

	StatFs internal = new StatFs(Environment.getDataDirectory().getPath());

	SharedPreferences prefs;

	public void init(Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * @return Available internal storage in KB.
	 */
	public double getAvailInternal(){
		if(Build.VERSION.SDK_INT >= 18) {
			return (internal.getFreeBlocksLong() * internal.getBlockSizeLong()) / 1024.0;
		} else {
			return ((long) internal.getFreeBlocks() * (long) internal.getBlockSize()) / 1024.0;
		}
	}

	/**
	 * @return Total internal storage in KB.
	 */
	public double getTotalInternal(){
		if(Build.VERSION.SDK_INT >= 18) {
			return (internal.getBlockSizeLong() * internal.getBlockCountLong()) / 1024.0;
		} else {
			return ((long) internal.getBlockSize() * (long) internal.getBlockCount()) / 1024.0;
		}
	}

	/**
	 * @return Used internal storage in KB.
	 */
	public double getUsedInternal(){
		return getTotalInternal() - getAvailInternal();
	}

	/**
	 *
	 */

	ArrayAdapter<String> adapter;
	List<String> info;

	public ArrayAdapter<String> populateListView(Context context){
		adapter = null;
		info = new ArrayList<>();

		info.add("Internal Storage:");
		info.add("    Total: " + validateValue(getTotalInternal()                       , false));
		info.add("    Available: " + validateValue(getAvailInternal()                   , false));
		info.add("    Used: " + validateValue(getTotalInternal() - getAvailInternal()   , false));

		adapter = new ArrayAdapter<>(context, (prefs.getString("theme", "col").equals("col")) ? R.layout.listview_layout_colourful : R.layout.listview_layout_minimalistic, info);
		return adapter;
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
