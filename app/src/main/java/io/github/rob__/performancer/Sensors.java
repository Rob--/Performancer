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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import io.github.rob__.performancer.info.InfoSensors;

public class Sensors extends Activity{
	
	SharedPreferences prefs;

	final Handler handler = new Handler();
	ListView lvSensors;
	Parcelable state;
	
	InfoSensors infoSensors = new InfoSensors();
	Tools tools = new Tools();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//prefs = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(prefs.getString("theme", "col").equals("col")){
            // Matching Action Bars
            //setTheme(R.style.AppThemeColourful);
			setContentView(R.layout.activity_sensors_colourful);
		} else {
            //setTheme(R.style.AppThemeMinimalistic);
			setContentView(R.layout.activity_sensors_minimalistic);
		}
		
		infoSensors .init(this);
		tools       .addMenu(Sensors.this, this);
		tools       .populateMenu((ListView) findViewById(R.id.menuItems), this, this, "Sensors");
		tools       .toggle();
		
		//Loads all the layout items for use.
		lvSensors = (ListView) findViewById(R.id.lvSensors);
		
		handler.postDelayed(update, 1);
		lvSensors.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	String t = lvSensors.getItemAtPosition(position).toString();

			    // u200B                == Zero-Width
			    // Linear Acceleration  == \u200B * 0
			    // Acceleration         == \u200B * 1
			    // Gravity              == \u200B * 2

		    	// Acceleration
		    	if(t.contains("Accel") && !t.contains("Linear")){
		    		tools.MsgBox("Measures (non-linear) acceleration force along the x/y/z axis (including gravity).", prefs.getString("accelerometer name", "Accelerometer"), Sensors.this);
		    	} else if(t.contains("m/s") && !t.contains("\u200B")){
		    		tools.MsgBox("The measure of (non-linear) acceleration force along the x/y/z axis (including gravity).", prefs.getString("accelerometer name", "Accelerometer"), Sensors.this);
		    		
		    	// Linear Accelerator
		    	} else if(t.contains("Linear") && !t.contains("Gravity")){
		    		tools.MsgBox("Measures the acceleration force in m/s\u00B2 that is applied to a device on all three physical axes (x, y, and z), excluding the force of gravity.\n\nWhen the device is stationary this value will match the acceleration values.", prefs.getString("linear acceleration name", "Linear Acceleration"), Sensors.this);
		    	} else if(t.contains("m/s") && t.contains("\u200B") && !t.contains("\u200B\u200B")){
		    		tools.MsgBox("The measure of the acceleration force in m/s\u00B2 that is applied to a device on all three physical axes (x, y, and z), excluding the force of gravity.\n\nWhen the device is stationary this value will match the acceleration values.", prefs.getString("linear acceleration name", "Linear Acceleration"), Sensors.this);
		    	
		    	// Gravity
		    	} else if(t.contains("Gravity")){
		    		tools.MsgBox("Measures the force of gravity in m/s\u00B2 that is applied to a device on all three physical axes (x, y, z).", prefs.getString("gravity name", "Gravity"), Sensors.this);
		    	} else if(t.contains("m/s\u00B2") && t.contains("\u200B\u200B")){
		    		tools.MsgBox("The measure of the force of gravity in m/s\u00B2 that is applied to the x/y/z axes.", prefs.getString("gravity name", "Gravity"), Sensors.this);
		    	
		    	// Magnet
		    	} else if(t.contains("Magnet")){
		    		tools.MsgBox("Measures the ambient geomagnetic field for all three physical axes (x, y, z) in \u00B5T.", prefs.getString("magnet field name", "Magnetometer"), Sensors.this);
		    		tools.MsgBox("For information about the expected geomagnetic field and metal detection, location services need to be enabled for this application.", "More info", Sensors.this);
		    	} else if(t.contains("Magnitude")){
		    		tools.MsgBox("The value is in micro-Tesla (uT). It measures the ambient magnetic field in the X, Y and Z axis.", "Magnitude", Sensors.this);
		    	} else if(t.contains("Exp Mag")){
		    		tools.MsgBox("The expected ambient magnetic field for your (last known) location.", "Exp Mag", Sensors.this);
		    	} else if(t.contains("Metal")){
		    		tools.MsgBox("Used magnitude in comparison with the expected magnitude to identify a disruption in the magnetic field and determine if a metal or live current is near.", "Magnetic Field", Sensors.this);
		    		
		    	// Orient
		    	} else if(t.contains("Orient")){
		    		tools.MsgBox("Measures the orientation in degrees of the device by taking into the values of the accelerator and magnetometer.", "Orientation", Sensors.this);
		    	} else if(t.contains("    ") && !(t.contains("/s")) && t.contains("\u00B0")){
		    		tools.MsgBox("Device's angle in degrees along the x/y/z axis.", "Orientation", Sensors.this);
		    	
		    	// Gyroscope
		    	} else if(t.contains("Gyroscope")){
		    		tools.MsgBox("Measures a device's rate of rotation in degrees around each of the three physical axes (x, y, and z).", prefs.getString("gyroscope name", "Gyroscope"), Sensors.this);
		    	} else if(t.contains("    ") && t.contains("/s")){
		    		tools.MsgBox("Device's rate of rotation along the x/y/z axes (per second).", prefs.getString("gyroscope name", "Gyroscope"), Sensors.this);
		    	
		    	// Light
		    	} else if(t.contains("Light")){
		    		tools.MsgBox("Measures the ambient light level (illumination) in lx (lux).", prefs.getString("light name", "Light"), Sensors.this);
		    	} else if(t.contains("Lux")){
		    		tools.MsgBox("The SI unit of illuminance, equal to one lumen per square metre. Used to measure the ambient light level.", "Lux", Sensors.this);
		    	
		    	// Proximity
		    	} else if(t.contains("Proximity")){
		    		tools.MsgBox("Uses the proximity sensor to detect whether the device is at close proximity to an object. The limit is in CM, if passed the device is in range of an object adjacent to the sensor.", prefs.getString("proximity name", "Proximity"), Sensors.this);
		    	} else if(t.contains("Limit")){
		    		tools.MsgBox("The range in which the proximity detects an object.", "Proximity Range", Sensors.this);
		    	
		    	// Pressure
		    	} else if(t.contains("Pressure")){
		    		tools.MsgBox("Measures the ambient air pressure in hPa.", prefs.getString("pressure name", "Pressure"), Sensors.this);
		    	} else if(t.contains("hPa")){
		    		tools.MsgBox("The ambient air pressure in hPa.", "Hectopascal", Sensors.this);
		    	} else if(t.contains("Altitude")){
		    		tools.MsgBox("The estimated altitude calculated from the ambient air pressure. This is not always accurate due to weather conditions affecting air pressure.", "Altitude", Sensors.this);
		    	
		    	// Humidity
		    	} else if(t.contains("Humidity")){
		    		tools.MsgBox("Measures the relative ambient humidity in percent (%).", prefs.getString("relative humidity name", "Humidity"), Sensors.this);
		    	} else if(t.contains("\u200B\u200B")){
		    		tools.MsgBox("The relative ambient humidity in percent (%).", "Humidity", Sensors.this);
		    		
		    	// Ambient Temperature
		    	} else if(t.contains("Temperature")){
		    		tools.MsgBox("Measures the ambient room temperature in degrees Celsius (\u00B0C).", prefs.getString("ambient temperature name", "Ambient Temperature"), Sensors.this);
		    	} else if(t.contains("\u00B0C")){
		    		tools.MsgBox("The ambient room temperature in degrees Celsius (\u00B0C).", "Ambient Temperature", Sensors.this);
		    	
		    	// Pedometer
		    	} else if(t.contains("Pedometer")){
		    		tools.MsgBox("The number of steps taken (calculated with the accelerometer). This count resets every time the device reboots.", "Step Counter", Sensors.this);
		    	} else if(t.contains("steps")){
		    		tools.MsgBox("The number of steps taken (calculated with the accelerometer). This count resets every time the device reboots.", "Step Counter", Sensors.this);
		    	}
		    }
		});
	}
	
	Runnable update = new Runnable() { 
        @Override 
        public void run() {
        	state = lvSensors   .onSaveInstanceState();
        	lvSensors           .setAdapter(infoSensors.populateListView(Sensors.this));
        	lvSensors           .onRestoreInstanceState(state);
        	handler             .postDelayed(this, prefs.getInt("delay", 500));
        }
    };
    
    public void updateStart(){
    	handler.postDelayed(update, 1);
    }
    
    public void updateStop(){
    	handler.removeCallbacks(update);
    	infoSensors.stopEverything();
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
	    Window window = getWindow();
	    window.setFormat(PixelFormat.RGBA_8888);
	}
}
