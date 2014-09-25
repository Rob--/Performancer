package io.github.rob__.performancer.info;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.github.rob__.performancer.R;

public class InfoRAM {
	
	ActivityManager activityManager;
	DecimalFormat formatter = new DecimalFormat("###,###,###,###");
	SharedPreferences prefs;
	
	RandomAccessFile reader;
	ArrayAdapter<String> adapter;
	List<String> info;
	String memTotal;
	String memFree;
	String line;
	
	public void init(Context context, ActivityManager ACTIVITY_SERVICE){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(!prefs.contains("Memory Threshold kB")){ prefs.edit().putLong("Memory Threshold kB", getMemThreshold(ACTIVITY_SERVICE, true))    .apply();   }
		if(!prefs.contains("Memory Threshold MB")){ prefs.edit().putLong("Memory Threshold MB", getMemThreshold(ACTIVITY_SERVICE, false))   .apply();   }
	}
	
	/**
	 * Gets low memory threshold in KB.
	 */
	public long getMemThreshold(ActivityManager ACTIVITY_SERVICE, boolean kB){
		MemoryInfo mi = new MemoryInfo();
		activityManager = ACTIVITY_SERVICE;
		activityManager.getMemoryInfo(mi);
		activityManager.getMemoryInfo(mi);
		if(kB){
			return mi.threshold / 1000L;
		} else {
			return mi.threshold / 1000000L;
		}
	}
	
	private Runnable mempr = new Runnable(){
		@Override
		public void run(){
			try {
				reader      = null;
				memTotal    = null;
				memFree     = null;
				
		        reader = new RandomAccessFile("/proc/meminfo", "r");
		        String line;
				try {
					while ((line = reader.readLine()) != null) {
						if (line.contains("MemTotal")) {
							memTotal = line.replace("kB", "").replace(" ", "").substring(line.indexOf(":") + 1);
						}
						if (line.contains("MemFree")) {
							memFree = line.replace("kB", "").replace(" ", "").substring(line.indexOf(":") + 1);
						}
					}
				} catch(NullPointerException e){
					e.printStackTrace();
					// File is null / line is null.
				}
		        reader.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	};
	
	/**
	 * Returns memory percentage.
	 * @return double
	 */
	public double getMemPercentage() {
		Thread mempt = new Thread(mempr);
		mempt.run();

		try {
			return (memFree == null || memTotal == null) ? 100 : round(100 - ((Double.parseDouble(memFree) / Double.parseDouble(memTotal)) * 100), 3);
		} catch(NumberFormatException e){
			e.printStackTrace();
			return 80;
			// The Random Access File was either printed wrongly
			// or read wrongly. This generally happens upon
			// initial launch of the activity.
			// Return a legit number to "hide" this error.
		}
	}
	
	public String getMemFree(){
		Thread memInfo = new Thread(mempr);
		memInfo.run();
		
		return memFree;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
	}

	public ArrayAdapter<String> populateListView(Context context){
		reader = null;
		adapter = null;
		info = new ArrayList<>();
		if(prefs.getBoolean("advanced", false)){
	    	try {
	        	reader = new RandomAccessFile("/proc/meminfo", "r");
			    try {
				    while ((line = reader.readLine()) != null) {
					    info.add(String.valueOf(line));
				    }
			    } catch(IOException e){
				    e.printStackTrace();
				    reader.close();
			    }
	        	reader.close();
	        	adapter = new ArrayAdapter<>(context, (prefs.getString("theme", "col").equals("col")) ? R.layout.listview_layout_colourful : R.layout.listview_layout_minimalistic, info);
	        	return adapter;
	    	} catch (IOException e) {
	        	e.printStackTrace();
	    	}
	    	info.add("\u200B");
	    	adapter = new ArrayAdapter<>(context, (prefs.getString("theme", "col").equals("col")) ? R.layout.listview_layout_colourful : R.layout.listview_layout_minimalistic, info);
        	return adapter;
		} else {
			double highTotal    = 0;
			double highFree     = 0;
			double lowTotal     = 0;
			double lowFree      = 0;
			double high;
			double low;

			try {
				reader = new RandomAccessFile("/proc/meminfo", "r");
				try {
					while ((line = reader.readLine()) != null) {
						if (line.contains("MemTotal:"))
							info.add(simplify("Total Memory", line, false));
						if (line.contains("MemFree:"))
							info.add(simplify("Free Memory", line, false));
						if (line.contains("Cached:") && !line.contains("Swap"))
							info.add(simplify("Cached", line, false));
						if (line.contains("Active:")) info.add(simplify("Active", line, false));
						if (line.contains("Inactive:")) info.add(simplify("Inactive", line, false));
						if (line.contains("HighTotal:"))
							highTotal = Integer.parseInt(simplify(null, line, true));
						if (line.contains("HighFree:"))
							highFree = Integer.parseInt(simplify(null, line, true));
						if (line.contains("LowTotal:"))
							lowTotal = Integer.parseInt(simplify(null, line, true));
						if (line.contains("LowFree:"))
							lowFree = Integer.parseInt(simplify(null, line, true));
					}
				} catch(IOException e){
					e.printStackTrace();
					reader.close();
				}
				high = round(100    - ((highFree    / highTotal)    * 100), 2);
				low = round(100     - ((lowFree     / lowTotal)     * 100), 2);
				
				info.add("Kernel: "     + String.valueOf(low)   + "%");
				info.add("Non-kernel: " + String.valueOf(high)  + "%");
				info.add((prefs.getString("data unit", "kb").equals("kb")) ? "Mem Threshold: " + String.valueOf(formatter.format(prefs.getLong("Memory Threshold kB", 1))) + " kB" : "Mem Threshold: " + String.valueOf(formatter.format(prefs.getLong("Memory Threshold MB", 1))) + " MB");

				reader.close();
				adapter = new ArrayAdapter<>(context, (prefs.getString("theme", "col").equals("col")) ? R.layout.listview_layout_colourful : R.layout.listview_layout_minimalistic, info);
	        	return adapter;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return adapter;
		}
	}

	private String simplify(String string, String line, boolean onlyNumber) {
		int number;
		number = Integer.parseInt(line.replace(" ", "").replace("kB",  "").substring(line.indexOf(":") + 1));
		if(!onlyNumber){
			return (prefs.getString("data unit", "mb").equals("mb")) ? string + ": " + String.valueOf(formatter.format(number / 1000)) + " MB" : string + ": " + String.valueOf(formatter.format(number)) + " kB";
		} else {
			return String.valueOf(number);
		}
	}
}
