package io.github.rob__.performancer.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import io.github.rob__.performancer.R;

public class InfoSensors implements LocationListener {
	
	SharedPreferences prefs;
	SensorManager sm;
	SensorEventListener sl;
	
	ArrayAdapter<String> adapter;
	List<String> info;
	boolean success;
	
	// Accelerator
	double[] xyzA = { 0.0, 0.0, 0.0 };
	
	// Linear Accelerator
	double[] xyzLA = { 0.0, 0.0, 0.0 };
	
    // Magentic Field
    double mag = 0;
    double expMag = 0;
    GeomagneticField gf;
    String status = "";
    double longitude = 0;
    double latitude = 0;
    double altitude = 0;
    Criteria c = new Criteria();
    LocationManager lm;
	LocationListener ll;
	Location l;
	String provider;
    
	// Orientation Angle
    float[] gravity;
    float[] geomagnetic;
    double[] zxyO = {0.0, 0.0, 0.0 };
    
    // Gyroscope
    double[] xyzG = { 0.0, 0.0, 0.0, 0.0 };
	
    // Light
    double light;
    
    // Pressure
    double pressure;
    double p_altitude;
    
    // Proximity
    double proximity;
    boolean b_proximity;
    
    // Gravity
    double[] xyzGy = { 0.0, 0.0, 0.0 };
    
    // Relative Humidity
    double humidity;
    
    // Ambient Temperature
    double ambient_temp;
    
    // Step Detector/Counter
    int step_c;
    double step_d;
    
    @Override
    public void onLocationChanged(Location l) {
    	longitude = l.getLongitude();
    	latitude = l.getLatitude();
    	altitude = l.getAltitude();
    }
    
    @Override public void onProviderDisabled(String provider) {}
    @Override public void onProviderEnabled (String provider) {}
    
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    
	public void init(final Context context){
		if(isFeatureAvailable(context, PackageManager.FEATURE_LOCATION) && isFeatureAvailable(context, PackageManager.FEATURE_LOCATION_GPS)) {
			lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		}
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		sl = new SensorEventListener() {
			@Override public void onAccuracyChanged(Sensor arg0, int arg1) {}
		    @Override
		    public void onSensorChanged(SensorEvent event) {
		    	if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
		    		gravity = event.values;
		    		xyzA[0] = round(event.values[0], 3);
		  		  	xyzA[1] = round(event.values[1], 3);
		  		  	xyzA[2] = round(event.values[2], 3);
		  		  	if(!prefs.contains("accelerometer name")){ prefs.edit().putString("accelerometer name", String.valueOf(event.sensor.getName())).apply(); }
		    	}

		    	if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
		    		geomagnetic = event.values;
		    		mag = round((float) Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2)), 3);

				    // Requires locations.
				    // Checks if location can be accessed.
				    // True ? Check metal detection : pass;
				    if(isFeatureAvailable(context, PackageManager.FEATURE_LOCATION) && isFeatureAvailable(context, PackageManager.FEATURE_LOCATION_GPS)) {
					    provider = lm.getBestProvider(c, false);
					    l = lm.getLastKnownLocation(provider);
					    if (l != null) {
						    longitude = l.getLongitude();
						    latitude = l.getLatitude();
						    altitude = l.getAltitude();

						    gf = new GeomagneticField((float) l.getLatitude(), (float) l.getLongitude(), (float) l.getAltitude(), 1000);
						    expMag = round((float) Math.sqrt(gf.getX() + gf.getY() + gf.getZ()), 3);
						    if (mag > (expMag * 0.5)) {
							    status = "True";
						    } else if (mag > (expMag * 0.3)) {
							    status = "Likely";
						    } else {
							    status = "False";
						    }
					    }
				    }
		    		if(!prefs.contains("magnetic field name")){ prefs.edit().putString("magnetic field name", String.valueOf(event.sensor.getName())).apply(); }
		    	}
		    	
		    	if((xyzA != null) && (mag != 0)){
		    		 float R[] = new float[9];
		    	     float I[] = new float[9];
		    	     try{
		    	    	 success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
		    	     } catch(Exception e){
		    	    	 e.printStackTrace();
		    	    	 // Sometimes getRotationMatrix is null.
		    	     }
		    	     if (success) {
		    	    	 float orientation[] = new float[3];
		    	    	 SensorManager.getOrientation(R, orientation);
		    	    	 zxyO[0] = orientation[0];
		    	    	 zxyO[1] = orientation[1];
		    	    	 zxyO[2] = orientation[2];
				     }
		    	}
		    	
		    	if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
		    		xyzG[0] = event.values[0];
		    		xyzG[1] = event.values[1];
		    		xyzG[2] = event.values[2];
		    		xyzG[3] = Math.sqrt(Math.pow(xyzG[0], 2) + Math.pow(xyzG[1], 2) + Math.pow(xyzG[2], 2));
		    		if(!prefs.contains("gyroscope name")){ prefs.edit().putString("gyroscope name",  String.valueOf(event.sensor.getName())).apply(); }
		    	}
		    	
		    	if(event.sensor.getType() == Sensor.TYPE_LIGHT){
		    		light = event.values[0];
		    		if(!prefs.contains("light name")){ prefs.edit().putString("light name",  String.valueOf(event.sensor.getName())).apply(); }
		    	}
		    	
		    	if(event.sensor.getType() == Sensor.TYPE_PRESSURE){
		    		pressure = event.values[0];
		    		p_altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, (float) pressure);
		    		if(!prefs.contains("pressure name")){ prefs.edit().putString("pressure name", String.valueOf(event.sensor.getName())).apply(); }
		    	}
		    	
		    	if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
		    		proximity = event.values[0];
		    		b_proximity = (proximity == 0) ? true : false;
		    		if(!prefs.contains("proximity name")){ prefs.edit().putString("proximity name", String.valueOf(event.sensor.getName())).apply(); }
		    	}
		    	
		    	if(event.sensor.getType() == Sensor.TYPE_GRAVITY){
		    		xyzGy[0] = event.values[0];
		    		xyzGy[1] = event.values[1];
		    		xyzGy[2] = event.values[2];
		    		if(!prefs.contains("gravity name")){ prefs.edit().putString("gravity name", String.valueOf(event.sensor.getName())).apply(); }
		    	}
		    	
		    	if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
		    		xyzLA[0] = event.values[0];
		    		xyzLA[1] = event.values[1];
		    		xyzLA[2] = event.values[2];
		    		if(!prefs.contains("linear accelerator name")){ prefs.edit().putString("linear accelerator name", String.valueOf(event.sensor.getName())).apply(); }
		    	}
		    	
		    	if(event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY){
		    		humidity = event.values[0];
		    		if(!prefs.contains("relative humidity name")){ prefs.edit().putString("relative humidity name", String.valueOf(event.sensor.getName())).apply(); }
		    	}
		    	
		    	if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
		    		ambient_temp = event.values[0];
		    		if(!prefs.contains("ambient temperature name")){ prefs.edit().putString("ambient temperature name", String.valueOf(event.sensor.getName())).apply(); }
		    	}
		    	
		    	if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
		    		step_c = (int) event.values[0];
		    	}
		    }
		};
		
		if(hasAccelerometer())		 { 	sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER		), SensorManager.SENSOR_DELAY_UI); }
		if(hasMagneticField())		 { 	sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD		), SensorManager.SENSOR_DELAY_UI); }
		if(hasGyroscope())	 		 {	sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE			), SensorManager.SENSOR_DELAY_UI); }
		if(hasLight())				 {	sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_LIGHT				), SensorManager.SENSOR_DELAY_UI); }
		if(hasPressure())			 {	sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_PRESSURE			), SensorManager.SENSOR_DELAY_UI); }
		if(hasProximity())			 {	sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_PROXIMITY			), SensorManager.SENSOR_DELAY_UI); }
		if(hasGravity())			 {	sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_GRAVITY				), SensorManager.SENSOR_DELAY_UI); }
		if(hasLinearAccelerometer()) {	sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION	), SensorManager.SENSOR_DELAY_UI); }
		if(hasRelativeHumidity())	 {	sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY	), SensorManager.SENSOR_DELAY_UI); }
		if(hasAmbientTemperature())	 {	sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE ), SensorManager.SENSOR_DELAY_UI); }	
		if(hasStepCounter())		 { 	sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER		), SensorManager.SENSOR_DELAY_UI); }
		
		// Settings Menu - CHECK LIST for SENSORS.
		checkSensors(context);
	}
	
	public boolean hasAccelerometer()		{ /* Constant = 1  */ return (prefs.getBoolean("accelerometer", 	   false)) ? true : false; }
	public boolean hasMagneticField()		{ /* Constant = 2  */ return (prefs.getBoolean("magnetic field", 	   false)) ? true : false; }
//	public boolean hasOrientation()			{ /* Constant = 3  */ return (prefs.getBoolean("orientation", 		   false)) ? true : false; }
	public boolean hasGyroscope()			{ /* Constant = 4  */ return (prefs.getBoolean("gyroscope", 		   false)) ? true : false; }
	public boolean hasLight()				{ /* Constant = 5  */ return (prefs.getBoolean("light",				   false)) ? true : false; }
	public boolean hasPressure()			{ /* Constant = 6  */ return (prefs.getBoolean("pressure", 			   false)) ? true : false; }
	public boolean hasTemperature()			{ /* Constant = 7  */ return (prefs.getBoolean("temperature", 		   false)) ? true : false; }
	public boolean hasProximity()			{ /* Constant = 8  */ return (prefs.getBoolean("proximity", 		   false)) ? true : false; }
	public boolean hasGravity()				{ /* Constant = 9  */ return (prefs.getBoolean("gravity", 			   false)) ? true : false; }
	public boolean hasLinearAccelerometer() { /* Constant = 10 */ return (prefs.getBoolean("linear accelerometer", false)) ? true : false; }
//	public boolean hasRotationVector()		{ /* Constant = 11 */ return (prefs.getBoolean("rotation vector", 	   false)) ? true : false; }
	public boolean hasRelativeHumidity()	{ /* Constant = 12 */ return (prefs.getBoolean("relative humidity",    false)) ? true : false; }
	public boolean hasAmbientTemperature()	{ /* Constant = 13 */ return (prefs.getBoolean("ambient temperature",  false)) ? true : false; }
//	public boolean hasStepDetector()		{ /* Constant = 18 */ return (prefs.getBoolean("step detector", 	   false)) ? true : false; }
	public boolean hasStepCounter()			{ /* Constant = 19 */ return (prefs.getBoolean("step counter",		   false)) ? true : false; }
	
//	Constant 7 - temperature is deprecated.
//  Constant 3 - The orientation sensor method is deprecated and therefore a new getOrientation() method is used.
//	Constant 3 - Orientation uses accelerometer and magnetic field to gain values.
//	Constant 11 - Rotation Vector is a next to useless sensor.
//	Constant 18 - too much work required to detect step. nanoseconds -> millisecond and calculations are intensive.
	
	private void checkSensors(Context context){
		prefs.edit().putBoolean("accelerometer", 		check(1)).apply();
		prefs.edit().putBoolean("magnetic field", 		check(2)).apply();
//		prefs.edit().putBoolean("orientation", 			check(3)).apply();
		prefs.edit().putBoolean("gyroscope",	 		check(4)).apply();
		prefs.edit().putBoolean("light", 				check(5)).apply();
		prefs.edit().putBoolean("pressure", 			check(6)).apply();
		prefs.edit().putBoolean("temperature",          check(7)).apply();
		prefs.edit().putBoolean("proximity", 			check(8)).apply();
		prefs.edit().putBoolean("gravity", 				check(9)).apply();
		prefs.edit().putBoolean("linear accelerometer", check(10)).apply();
//		prefs.edit().putBoolean("rotation vector", 		check(11)).apply();
		prefs.edit().putBoolean("relative humidity", 	check(12)).apply();
		prefs.edit().putBoolean("ambient temperature", 	check(13)).apply();
//		prefs.edit().putBoolean("step detector", 		check(18)).apply();
		prefs.edit().putBoolean("step counter",			check(19)).apply();
	}
	
	private boolean check(int sensor){ return (sm.getDefaultSensor(sensor) != null) ? true : false; }
	
	/*
	 * 
	 * 
	 */
	
	public ArrayAdapter<String> populateListView(Context context){
		adapter = null;
		info = new ArrayList<String>();
		
		if(hasAccelerometer()){
			info.add("Accelerator:");
			info.add("    X: " + String.valueOf(round(xyzA[0], 3) + " m/s\u00B2"));
			info.add("    Y: " + String.valueOf(round(xyzA[1], 3) + " m/s\u00B2"));
			info.add("    Z: " + String.valueOf(round(xyzA[2], 3) + " m/s\u00B2"));
		}
		
		if(hasLinearAccelerometer()){
			info.add("Linear Accelerometer:");
			info.add("    X: " + String.valueOf(round(xyzLA[0], 3) + " m/s\u00B2\u200B"));
			info.add("    Y: " + String.valueOf(round(xyzLA[1], 3) + " m/s\u00B2\u200B"));
			info.add("    Z: " + String.valueOf(round(xyzLA[2], 3) + " m/s\u00B2\u200B"));
			// u200B is an invisible character used specifically to differentiate between accel and linear accel
		}
		
		if(hasGravity()){
			info.add("Gravity:");
			info.add("    X: " + String.valueOf(round(xyzGy[0], 3)) + " m/s\u00B2");
			info.add("    Y: " + String.valueOf(round(xyzGy[1], 3)) + " m/s\u00B2");
			info.add("    Z: " + String.valueOf(round(xyzGy[2], 3)) + " m/s\u00B2");
		}
		
		if(hasAccelerometer() && hasMagneticField()){
			info.add("Orientation:");
			info.add("    X: " + String.valueOf(round((zxyO[1] * 180) / Math.PI, 3) + " \u00B0"));
			info.add("    Y: " + String.valueOf(round((zxyO[2] * 180) / Math.PI, 3) + " \u00B0"));
			info.add("    Z: " + String.valueOf(round((zxyO[0] * 180) / Math.PI, 3) + " \u00B0"));
			// (degress * 180) / pi = radians -> degrees
		}
		
		if(hasGyroscope()){
			info.add("Gyroscope:");
			info.add("    X: " + String.valueOf(round((xyzG[0] * 180) / Math.PI, 3) + " \u00B0/s"));
			info.add("    Y: " + String.valueOf(round((xyzG[1] * 180) / Math.PI, 3) + " \u00B0/s"));
			info.add("    Z: " + String.valueOf(round((xyzG[2] * 180) / Math.PI, 3) + " \u00B0/s"));
			// (degrees * 180) / pi = radians -> degrees
		}
		
		if(hasMagneticField()){
			info.add("Magnetometer:");
			info.add("    Magnitude: " + String.valueOf(mag) + " \u00B5T");
			if(l != null){
				info.add("    Magnetic Field: " + String.valueOf(expMag) + " \u00B5T");
				info.add("    Metal near: " + status);
			}
		}
		
		if(hasLight()){
			info.add("Light:");
			info.add("    Lux: " + String.valueOf(light));
		}
		
		if(hasProximity()){
			info.add("Proximity:");
			info.add("    Limit (CM): " + String.valueOf(round(proximity, 3)) + " - " + String.valueOf(b_proximity).toUpperCase());
		}
		
		if(hasPressure()){
			info.add("Air Pressure:");
			info.add("    hPa: " + String.valueOf(round(pressure, 3)));
			info.add("    Altitude: " + String.valueOf(round(p_altitude, 3)));
		}
		
		if(hasRelativeHumidity()){
			info.add("Relative Humidity:");
			info.add("    " + String.valueOf(humidity) + "%\u200B\u200B");
		}
		
		if(hasAmbientTemperature()){
			info.add("Ambient Temperature:");
			info.add("    " + String.valueOf(ambient_temp) + "\u00B0C");
		}
		
		if(hasStepCounter()){
			info.add("Pedometer:");
			if(hasStepCounter()){
				info.add("    " + String.valueOf(step_c) + " steps");
			}
		}
		
		adapter = new ArrayAdapter<String>(context, (prefs.getString("theme", "col").equals("col")) ? R.layout.listview_layout_colourful : R.layout.listview_layout_minimalistic, info);
    	return adapter;
	}
	
	public void stopEverything(){
		if(hasAccelerometer())		 { 	sm.unregisterListener(sl, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER			)); }
		if(hasMagneticField())		 { 	sm.unregisterListener(sl, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD		)); }
		if(hasGyroscope())	 		 {	sm.unregisterListener(sl, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE				)); }
		if(hasLight())				 {	sm.unregisterListener(sl, sm.getDefaultSensor(Sensor.TYPE_LIGHT					)); }
		if(hasPressure())			 {	sm.unregisterListener(sl, sm.getDefaultSensor(Sensor.TYPE_PRESSURE				)); }
		if(hasProximity())			 {	sm.unregisterListener(sl, sm.getDefaultSensor(Sensor.TYPE_PROXIMITY				)); }
		if(hasGravity())			 {	sm.unregisterListener(sl, sm.getDefaultSensor(Sensor.TYPE_GRAVITY				)); }
		if(hasLinearAccelerometer()) {	sm.unregisterListener(sl, sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION	)); }
		if(hasRelativeHumidity())	 {	sm.unregisterListener(sl, sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY		)); }
		if(hasAmbientTemperature())	 {	sm.unregisterListener(sl, sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE 	)); }	
		if(hasStepCounter())		 { 	sm.unregisterListener(sl, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER			)); }
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * Check if a feature is available.
	 * http://stackoverflow.com/users/220102/tim-goss
	 * http://stackoverflow.com/questions/5263068/how-to-get-android-device-features-using-package-manager/5264328#5264328
	 */
	public boolean isFeatureAvailable(Context context, String feature) {
		if(context.getPackageManager().hasSystemFeature(feature)){
			return true;
		}
		return false;
	}
}