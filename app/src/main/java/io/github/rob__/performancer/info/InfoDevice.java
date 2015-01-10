package io.github.rob__.performancer.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.rob__.performancer.R;

public class InfoDevice {

	SharedPreferences prefs;
	ArrayAdapter<String> adapter;
	List<String> info;

	SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a z (Z)");

	public void init(Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putBoolean("build updated", false);
	}

	public ArrayAdapter<String> populateListView(Context context) {
		adapter = null;
		info = new ArrayList<>();

		if(!prefs.getBoolean("build updated", false)){
			updateBuildPrefs();
			prefs.edit().putBoolean("build updated", true).apply();
		}

		info.add("Date: " + dateFormat.format(Calendar.getInstance().getTime()));
		info.add("Time: " + timeFormat.format(Calendar.getInstance().getTime()));

        // Commence beautiful code.
		if(prefs.getBoolean("advanced", false)){
			info.add("OS Name: "             + System.getProperty   ("os.name"));
			info.add("OS Version: "          + System.getProperty   ("os.version"));
			info.add("OS Architecture: "     + System.getProperty   ("os.arch"));
			info.add("Board: " 				 + prefs.getString      ("build board"				    , "Unknown"));
			info.add("Bootloader: " 		 + prefs.getString      ("build bootloader"			    , "Unknown"));
			info.add("Brand: "				 + prefs.getString      ("build brand"				    , "Unknown"));
			info.add("CPU ABI: " 			 + prefs.getString      ("build cpu_abi"				, "Unknown"));
			info.add("CPU ABI 2: " 			 + prefs.getString      ("build cdevice"				, "Unknown"));
			info.add("Device: " 			 + prefs.getString      ("build device"				    , "Unknown"));
			info.add("Display: " 			 + prefs.getString      ("build display"				, "Unknown"));
			info.add("Fingerprint: " 		 + prefs.getString      ("build fingerprint"			, "Unknown"));
			info.add("Hardware: " 			 + prefs.getString      ("build hardware"				, "Unknown"));
			info.add("Host: " 				 + prefs.getString      ("build host"					, "Unknown"));
			info.add("ID: " 				 + prefs.getString      ("build id"					    , "Unknown"));
			info.add("Manufacturer: "   	 + prefs.getString      ("build manufacturer"			, "Unknown"));
			info.add("Model: " 				 + prefs.getString      ("build model"				    , "Unknown"));
			info.add("Product: " 		 	 + prefs.getString      ("build product"				, "Unknown"));
			info.add("Radio: " 		 		 + prefs.getString      ("build radio"				    , "Unknown"));
			info.add("Serial: " 	 		 + prefs.getString      ("build serial"				    , "Unknown"));
			info.add("Tags: " 		 		 + prefs.getString      ("build tags"					, "Unknown" ));
			info.add("Time: "				 + String.valueOf       (Build.TIME                                 ));
			info.add("Type: " 				 + prefs.getString      ("build type"					, "Unknown" ));
			info.add("User: " 				 + prefs.getString      ("build user"					, "Unknown" ));
			info.add("Version Codename: " 	 + prefs.getString      ("build version codename"		, "Unknown" ));
			info.add("Version Incremental: " + prefs.getString      ("build version incremental"    , "Unknown" ));
			info.add("Version Release: " 	 + prefs.getString      ("build version release"		, "Unknown" ));
			info.add("Version SDK: " 		 + prefs.getInt	        ("build version sdk"			, -1	    ));
		} else {
			info.add("Brand: "				 + prefs.getString      ("build brand"				    , "Unknown" ));
			info.add("CPU ABI: " 			 + prefs.getString      ("build cpu_abi"				, "Unknown" ));
			info.add("Device: " 			 + prefs.getString      ("build device"				    , "Unknown" ));
			info.add("Display: " 			 + prefs.getString      ("build display"				, "Unknown" ));
			info.add("ID: " 				 + prefs.getString      ("build id"					    , "Unknown" ));
			info.add("Manufacturer: "   	 + prefs.getString      ("build manufacturer"			, "Unknown" ));
			info.add("Model: " 				 + prefs.getString      ("build model"				    , "Unknown" ));
			info.add("Version Release: " 	 + prefs.getString      ("build version release"		, "Unknown" ));
		}

		adapter = new ArrayAdapter<>(context, (prefs.getString("theme", "col").equals("col")) ? R.layout.listview_layout_colourful : R.layout.listview_layout_minimalistic, info);
    	return adapter;
	}

	public void updateBuildPrefs(){
		if(!prefs.contains("build board"))		 	  	{ prefs.edit().putString("build board"					, Build.BOARD				).apply(); }
		if(!prefs.contains("build bootloader"))	  	  	{ prefs.edit().putString("build bootloader"				, Build.BOOTLOADER			).apply(); }
		if(!prefs.contains("build brand"))		  	  	{ prefs.edit().putString("build brand"					, Build.BRAND				).apply(); }
		if(!prefs.contains("build cpu_abi"))	  	  	{ prefs.edit().putString("build cpu_abi"				, Build.CPU_ABI				).apply(); }
		if(!prefs.contains("build cpu_abi2"))	  	  	{ prefs.edit().putString("build cpu_abi2"				, Build.CPU_ABI2			).apply(); }
		if(!prefs.contains("build device"))		  	  	{ prefs.edit().putString("build device"					, Build.DEVICE				).apply(); }
		if(!prefs.contains("build display"))	  	  	{ prefs.edit().putString("build display"				, Build.DISPLAY				).apply(); }
		if(!prefs.contains("build fingerprint"))  	  	{ prefs.edit().putString("build fingerprint"			, Build.FINGERPRINT			).apply(); }
		if(!prefs.contains("build hardware"))	  	  	{ prefs.edit().putString("build hardware"				, Build.HARDWARE			).apply(); }
		if(!prefs.contains("build host"))		  	  	{ prefs.edit().putString("build host"					, Build.HOST				).apply(); }
		if(!prefs.contains("build id"))			  	  	{ prefs.edit().putString("build id"						, Build.ID					).apply(); }
		if(!prefs.contains("build manufacturer")) 	  	{ prefs.edit().putString("build manufacturer"			, Build.MANUFACTURER		).apply(); }
		if(!prefs.contains("build model"))		  	  	{ prefs.edit().putString("build model"					, Build.MODEL				).apply(); }
		if(!prefs.contains("build product"))	 	  	{ prefs.edit().putString("build product"				, Build.PRODUCT				).apply(); }
		if(!prefs.contains("build radio"))		  	  	{ prefs.edit().putString("build radio"					, Build.getRadioVersion()	).apply(); }
		if(!prefs.contains("build serial"))		  	  	{ prefs.edit().putString("build serial"					, Build.SERIAL				).apply(); }
		if(!prefs.contains("build tags"))		  	  	{ prefs.edit().putString("build tags"					, Build.TAGS				).apply(); }
		if(!prefs.contains("build type"))		  	  	{ prefs.edit().putString("build type"					, Build.TYPE				).apply(); }
		if(!prefs.contains("build user"))			  	{ prefs.edit().putString("build user"					, Build.USER				).apply(); }
		if(!prefs.contains("build version codename")) 	{ prefs.edit().putString("build version codename"		, Build.VERSION.CODENAME	).apply(); }
		if(!prefs.contains("build version incremental")){ prefs.edit().putString("build version incremental"	, Build.VERSION.INCREMENTAL	).apply(); }
		if(!prefs.contains("build version release"))	{ prefs.edit().putString("build version release"		, Build.VERSION.RELEASE		).apply(); }
		if(!prefs.contains("build version sdk"))		{ prefs.edit().putInt	("build version sdk"			, Build.VERSION.SDK_INT		).apply(); }
		
		// Updated
		prefs.edit().putBoolean("build updated", true).apply();
	}
}
