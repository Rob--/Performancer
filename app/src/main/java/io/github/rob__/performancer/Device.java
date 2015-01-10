package io.github.rob__.performancer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import io.github.rob__.performancer.info.InfoDevice;

public class Device extends Activity {

	SharedPreferences prefs;
	
	final Handler handler = new Handler();
	Parcelable state;
	
	ListView lvDevice;
	
	InfoDevice infoDevice = new InfoDevice();
	Tools tools = new Tools();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//prefs = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(prefs.getString("theme", "col").equals("col")){
            // Matching Action Bars
            //setTheme(R.style.AppThemeColourful);
			setContentView(R.layout.activity_device_colourful);
		} else {
            //setTheme(R.style.AppThemeMinimalistic);
			setContentView(R.layout.activity_device_minimalistic);
		}
		
		infoDevice  .init(this);
		tools       .addMenu(getApplicationContext(), this);
		tools       .populateMenu((ListView) findViewById(R.id.menuItems), this, this, "Device");
		tools       .toggle();
		
		//Loads all the layout items for use.
		lvDevice = (ListView) findViewById(R.id.lvDevice);
		
		updateStart();
		lvDevice.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	String t = lvDevice.getItemAtPosition(position).toString();

		    	if(t.split(":")[0].contains("Board")){
		    		tools.MsgBox("The name of the underlying board.", "Board", Device.this);
		    	} else if(t.split(":")[0].contains("Bootloader")){
		    		tools.MsgBox("The system bootloader version number.", "Bootloader", Device.this);
		    	} else if(t.split(":")[0].contains("Brand")){
		    		tools.MsgBox("The consumer-visible brand with which the product/hardware will be associated, if any.", "Brand", Device.this);
		    	} else if(t.split(":")[0].contains("CPU ABI") && !t.split(":")[0].contains("2")){
		    		tools.MsgBox("The name of the instruction set (CPU type + ABI convention) of native code.", "CPU ABI", Device.this);
		    	} else if(t.split(":")[0].contains("CPU ABI 2")){
		    		tools.MsgBox("The name of the instruction set (CPU type + ABI convention) of native code.", "CPU ABI2", Device.this);
		    	} else if(t.split(":")[0].contains("Device")){
		    		tools.MsgBox("The name of the instruction set (CPU type + ABI convention) of native code.", "Device", Device.this);
		    	} else if(t.split(":")[0].contains("Display")){
		    		tools.MsgBox("A build ID string meant for displaying to the user.", "Display", Device.this);
		    	} else if(t.split(":")[0].contains("Fingerprint")){
		    		tools.MsgBox("A string that uniquely identifies this build. Do not attempt to parse this value.", "Fingerprint", Device.this);
		    	} else if(t.split(":")[0].contains("Hardware")){
		    		tools.MsgBox("The name of the hardware (from the kernel command line or /proc).", "Hardware", Device.this);
		    	} else if(t.split(":")[0].contains("Host")){
		    		tools.MsgBox("No documentation found.", "Host", Device.this);
		    	} else if(t.split(":")[0].contains("ID")){
		    		tools.MsgBox("Either a changelist number, or a label like \"M4-rc20\".", "ID", Device.this);
		    	} else if(t.split(":")[0].contains("Manufacturer")){
		    		tools.MsgBox("The manufacturer of the product/hardware.", "Manufacturer", Device.this);
		    	} else if(t.split(":")[0].contains("Model")){
		    		tools.MsgBox("The end-user-visible name for the end product.", "Model", Device.this);
		    	} else if(t.split(":")[0].contains("Product")){
		    		tools.MsgBox("The name of the overall product.", "Product", Device.this);
		    	} else if(t.split(":")[0].contains("Radio")){
		    		tools.MsgBox("The version string for the radio firmware. Null (if, for instance, the radio is not currently on).", "Radio", Device.this);
		    	} else if(t.split(":")[0].contains("Serial")){
		    		tools.MsgBox("A hardware serial number, if available. Alphanumeric only, case-insensitive.", "Serial", Device.this);
		    	} else if(t.split(":")[0].contains("Tags")){
		    		tools.MsgBox("Comma-separated tags describing the build, like \"unsigned,debug\".", "Tags", Device.this);
		    	} else if(t.split(":")[0].contains("Time")){
		    		tools.MsgBox("No documentation found.", "Time", Device.this);
		    	} else if(t.split(":")[0].contains("Type")){
		    		tools.MsgBox("The type of build, like \"user\" or \"eng\".", "Type", Device.this);
		    	} else if(t.split(":")[0].contains("User")){
		    		tools.MsgBox("No documentation found.", "User", Device.this);
		    	} else if(t.split(":")[0].contains("Version Codename")){
		    		tools.MsgBox("The current development codename, or the string \"REL\" if this is a release build.", "Codename", Device.this);
		    	} else if(t.split(":")[0].contains("Incremental")){
		    		tools.MsgBox("The internal value used by the underlying source control to represent this build. E.g., a perforce changelist number or a git hash.", "Incremental", Device.this);
		    	} else if(t.split(":")[0].contains("Release")){
		    		tools.MsgBox("The user-visible version string. E.g., \"1.0\" or \"3.4b5\".", "Release", Device.this);
		    	} else if(t.split(":")[0].contains("SDK")){
		    		tools.MsgBox("The user-visible SDK version of the framework.", "SDK", Device.this);
		    	} else if(t.contains("Connected to") || t.contains("Not connected")){
				    tools.MsgBox("The WiFi network the device is connected to.", "WiFi Network", Device.this);
			    } else if(t.split(":")[0].contains("IP Address")){
				    tools.MsgBox("The local IP of the device.", "IP Address", Device.this);
			    } else if(t.split(":")[0].contains("Saved WiFi Networks")){
				    tools.MsgBox("A list of saves WiFi networks.", "Saved Networks", Device.this);
			    } else if(t.split(":")[0].contains("Nearby WiFi Networks")){
				    tools.MsgBox("A list of nearby WiFi networks.", "Nearby Networks", Device.this);
			    } else if(t.split(":")[0].contains("Strength")){
				    tools.MsgBox("The strength and name of the nearby network.", "WiFi Network", Device.this);
			    }
		    }
		});
	}
	
	Runnable update = new Runnable() { 
        @Override 
        public void run() {
        	state = lvDevice    .onSaveInstanceState();
        	lvDevice            .setAdapter(infoDevice.populateListView(Device.this));
        	lvDevice            .onRestoreInstanceState(state);
        	handler             .postDelayed(this, prefs.getInt("delay", 500));
        }
    };
    
    public void updateStart(){
    	handler.post(update);
    }
    
    public void updateStop(){
    	handler.removeCallbacks(update);
    }
    
    @Override
    public void onStop(){
    	updateStop();
    	super.onStop();
    }
    
    @Override
    public void onStart(){
    	updateStart();
    	super.onStart();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		tools.actionBarItemSelected(item, this);
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    getWindow().setFormat(PixelFormat.RGBA_8888);
	}
}
