package io.github.rob__.performancer.info;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import io.github.rob__.performancer.R;

public class InfoBattery {
	
	Intent intent = new Intent();
	float level = 0;
	String health = "";
	String tech = "";
	String plugged = "";
	String status = "";
	float voltage = 0;
	float temp = 0;
	boolean present = false;
	boolean charging = false;

	SharedPreferences prefs;
	public void init(Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public ArrayAdapter<String> populateListView(Context context){
		ArrayAdapter<String> adapter = null;
		List<String> info = new ArrayList<String>();
		
		info.add("Health: "                 + health);
		info.add("Tech: "                   + tech);
		if(charging){ info.add("Plug Type: "+ plugged); }
		info.add("Status: "                 + status);
		info.add("Voltage: "                + String.valueOf(voltage / 1000) + "V");
        info.add("Temp: "                   + ((prefs.getString("temp unit", "c").equals("c")) ? (String.valueOf(temp / 10) + "\u2103") : String.valueOf(round((((temp / 10) * 1.8) + 32), 2) + "\u2109")));
		info.add("Battery Present: "        + ((present) ? "True" : "False"));
		
		adapter = new ArrayAdapter<String>(context, (prefs.getString("theme", "col").equals("col")) ? R.layout.listview_layout_colourful : R.layout.listview_layout_minimalistic, info);
    	return adapter;
	}
	
	public void updateBatteryInformation(Context context){
		Intent  bI          = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		boolean present_    = bI.getBooleanExtra("present", false);
		tech =  bI.getStringExtra("technology");
		int     plugged_    = bI.getIntExtra("plugged", -1);
		float   scale_      = bI.getIntExtra("scale", -1);
		int     health_     = bI.getIntExtra("health", 0);
		int     status_     = bI.getIntExtra("status", 0);
		float   level_      = bI.getIntExtra("level", -1);
		float   voltage_    = bI.getIntExtra("voltage", 0);
		float   temp_       = bI.getIntExtra("temperature", 0);

		if(present_) {
			if (level_ >= 0 && scale_ > 0) {
				level = level_;
			}

			switch(plugged_){
			case BatteryManager.BATTERY_PLUGGED_AC:
				plugged = "AC";
				break;
			case BatteryManager.BATTERY_PLUGGED_USB:
				plugged = "USB";
				break;
			case BatteryManager.BATTERY_PLUGGED_WIRELESS:
				plugged = "Wireless";
				break;
			default:
				plugged = "Unknown";
				break;
			}
			
			switch(status_){
			case BatteryManager.BATTERY_STATUS_CHARGING:
				status = "Charging";
				charging = true;
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING:
				status = "Discharging";
				charging = false;
				break;
			case BatteryManager.BATTERY_STATUS_FULL:
				status = "Full";
				break;
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
				status = "Not charging";
				charging = false;
				break;
			case BatteryManager.BATTERY_STATUS_UNKNOWN:
				status = "Unknown";
				break;
			default:
				status = "Unknown";
				break;
			}
			
			switch(health_){
			case BatteryManager.BATTERY_HEALTH_COLD:
				health = "Cold";
				break;
			case BatteryManager.BATTERY_HEALTH_DEAD:
				health = "Dead";
				break;
			case BatteryManager.BATTERY_HEALTH_GOOD:
				health = "Good";
				break;
			case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
				health = "Over voltage";
				break;
			case BatteryManager.BATTERY_HEALTH_OVERHEAT:
				health = "Over heated";
				break;
			case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
				health = "Unknown";
				break;
			default:
				health = "Unknown";
				break;
			}
			
			voltage = (float) voltage_;
			temp = temp_;
			present = present_;
			
		}
	}
	
	public float getBatteryPercentage(Context context){
		Intent bI = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		boolean present_ = bI.getBooleanExtra("present", false);
		int scale_ = bI.getIntExtra("scale", -1);
		float level_ = bI.getIntExtra("level", -1);

		if(present_) {
			if (level_ >= 0 && scale_ > 0) {
				level = level_;
			}
			return level;
		}
		return 0.0f;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
	}
}
