package io.github.rob__.performancer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Tools {
	SlidingMenu menu;
	SharedPreferences prefs;

	/**
	 * Add a sliding menu to activity.
	 */
	public void addMenu(Context context, Activity activity){
		menu = new SlidingMenu(context);
		menu.setMode(SlidingMenu.LEFT);
		menu.setBackgroundColor(Color.parseColor("#1A1A1A"));
	    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	    menu.setShadowWidthRes(R.dimen.shadow_width);
	    menu.setShadowDrawable(R.drawable.shadow);
	    menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
	    menu.setFadeDegree(1f);
	    menu.setBehindScrollScale(0.25f);
	    menu.setMenu(R.layout.menu_content);
	    menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
	}

	/**
	 * Populate the sliding menu.
	 */
	public void populateMenu(final ListView items, final Context context, final Activity activity, final String activityName){
		items.setAdapter(new ArrayAdapter<>(context, R.layout.menu_item_layout, new String[] {"RAM", "CPU", "BATTERY", "STORAGE", "DEVICE", "SENSORS", "TESTS"}));
		items.setClickable(true);
		items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				String i = String.valueOf(items.getItemAtPosition(position));
				switch(i){
				case "RAM":
					if(activityName.equals("RAM")){
						toggle();
					} else {
						activity.finish();
						activity.startActivity(new Intent(context, RAM.class));
						activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					}
					break;
				  
				case "CPU":
					if(activityName.equals("CPU")){
						toggle();
					} else {
						activity.finish();
						activity.startActivity(new Intent(context, CPU.class));
						activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					}
					break;
				  
				case "BATTERY":
					if(activityName.equals("Battery")){
						toggle();
					} else {
						activity.finish();
						activity.startActivity(new Intent(context, Battery.class));
						activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					}
					break;
				  
			  	case "SENSORS":
			  		if(activityName.equals("Sensors")){
			  			toggle();
			  		} else {
			  			activity.finish();
			  			activity.startActivity(new Intent(context, Sensors.class));
			  			activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			  		}
			  		break;
				 
			  	case "DEVICE":
			  		if(activityName.equals("Device")){
			  			toggle();
			  		} else {
			  			activity.finish();
			  			activity.startActivity(new Intent(context, Device.class));
			  			activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			  		}
			  		break;
			  		
			  	case "TESTS":
			  		if(activityName.equals("Tests")){
			  			toggle();
			  		} else {
			  			activity.finish();
			  			activity.startActivity(new Intent(context, Tests.class));
			  			activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			  		}
				    break;

				case "STORAGE":
					if(activityName.equals("Storage")){
						toggle();
					} else {
						activity.finish();
						activity.startActivity(new Intent(context, Storage.class));
						activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					}
					break;
				}
			}
		});
	}

	/**
	 * Toggles sliding menu.
	 */
	public void toggle(){
		menu.toggle();
	}

	/**
	 * Fade in.
	 */
	public AlphaAnimation fadeIn(){
		AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(1000);
		fadeIn.setRepeatCount(0);
		fadeIn.setRepeatMode(Animation.REVERSE);
		return fadeIn;
	}
	
	/**
	 * Action Bar on item selected.
	 */
	public boolean actionBarItemSelected(MenuItem item, Context context){
		///prefs = context.getSharedPreferences("io.github.rob__.performancer", Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int id = item.getItemId();
		if(id == R.id.action_settings){
			context.startActivity(new Intent(context, Settings.class));
		}

		if(id == R.id.action_refresh_delay){
			DataDialog dd = new DataDialog();
			dd.chooseSettings(context);
		}

		if(id == R.id.action_about){
			String versionName = null;
			try {
				versionName = context.getApplicationContext().getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			Spanned title_credits   = Html.fromHtml(
				"Performancer - v" + versionName + "<br><small><tt>by Adroit</tt></small>");

			Spanned msg_credits     = Html.fromHtml(
				"<small>A simple application to monitor the performance and usage of a device.</small><br><br>" +
				"Android Open Source Project"   + "<br><i><small>by <a href='https://source.android.com/'>"                                                 + "Google"                          + "</a> </small></i><br><br>" +
				"Definition Documentation"      + "<br><i><small>by <a href='http://www.centos.org/docs/5/html/5.2/Deployment_Guide/s2-proc-meminfo.html'>" + "Centos"                          + "</a> </small></i><br><br>" +
				"Sensory Information"           + "<br><i><small>by <a href='http://developer.android.com/guide/topics/sensors/sensors_overview.html'>"     + "Android Developer Docs"          + "</a> </small></i><br><br>" +
				"Sensory Fusion"                + "<br><i><small>by <a href='https://www.youtube.com/watch?v=C7JQ7Rpwn2k'>"                                 + "GoogleTechTalks"                 + "</a> </small></i><br><br>" +
				"Circular Progress Bar"         + "<br><i><small>by <a href='https://github.com/passsy/'>"                                                  + "Pascal Welsch (passsy)"          + "</a> </small></i><br><br>" +
				"Sliding Menu"                  + "<br><i><small>by <a href='https://github.com/jfeinstein10/'>"                                            + "Jeremy Feinstein (jfeinstein10)" + "</a> </small></i><br><br>" +
				"Process Button"                + "<br><i><small>by <a href='https://github.com/dmytrodanylyk/'>"                                           + "Dymtro Danylyk (dmytrodanylyk)"  + "</a> </small></i><br><br>" +
				"Roboto Text View"              + "<br><i><small>by <a href='https://github.com/johnkil'>"                                                  + "Evgeny Shishkin (johnkil)"       + "</a> </small></i><br><br>" +
				"EazeGraph"                     + "<br><i><small>by <a href='https://github.com/blackfizz'>"                                                + "Paul Cech (blackfizz)"           + "</a> </small></i><br><br>" +
				"Nine Old Androids"             + "<br><i><small>by <a href='https://github.com/JakeWharton'>"                                              + "Jake Wharton (JakeWharton)"      + "</a> </small></i><br><br>" +
				"Pager Sliding Tab Strip"       + "<br><i><small>by <a href='https://github.com/astuetz'>"                                                  + "Andreas St\u00FCtz (astuetz)"    + "</a> </small></i>"
			);
		    Spanned title_sensors = Html.fromHtml(
				"Sensory Information<br><small><tt>from Android Developer Docs</tt></small>");

			Spanned msg_sensors = Html.fromHtml(
				"<small>" +
				"The information provided by sensors is generally raw output from the sensor."                                                                                  + "<br><br>" +
				"Some sensors use sensor fusion to provide a more accurate output (combining two sensors' outputs)."                                                            + "<br><br>" +
				"Sensory output along all axes (x/y/z) will oscillate the approximate value due to local interference."                                                         + "<br><br>" +
				"For example the magnetometer detects the magnetic field - but there's lots of interference from the device itself and generally other magnetised hardware."    + "<br><br>" +
				"Sensory output from sensors like the gyroscope may seem incorrect, this is because the gyroscope doesn't account for gravity."                                 + "<br><br>" +
				"A thoroughly detailed explanation on sensors and sensor fusion can be found below:"                                                                            + "<br><br>" +
				"<a href=\"https://www.youtube.com/watch?v=C7JQ7Rpwn2k\">Sensor Fusion on Android Devices: A Revolution in Motion Processing</a>" +
				"</small>"
			);
			
			// ContextThemeWrapper - wraps the context in a theme to avoid creating a new "view"
			// which borders the dialog.
			int theme = (prefs.getString("theme", "col").equals("col")) ? android.R.style.Theme_Holo_Dialog : android.R.style.Theme_Holo_Light_Dialog;
			final AlertDialog d_sensors = new AlertDialog.Builder(new ContextThemeWrapper(context, theme))
				.setPositiveButton(android.R.string.ok, null)
				.setTitle(title_sensors)
				.setMessage(msg_sensors)
				.create();
			
			final AlertDialog d_credits = new AlertDialog.Builder(new ContextThemeWrapper(context, theme))
		        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						d_sensors.show();
						((TextView) d_sensors.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
					}
				})
		        .setTitle(title_credits)
		        .setMessage(msg_credits)
		        .create();
			
			d_credits.show();

			((TextView) d_credits.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
		}
		return true;
	}
	
	/**
	 * Displays alert dialog.
	 */
	public void MsgBox(String text, String title, Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		AlertDialog.Builder a  = new AlertDialog.Builder(new ContextThemeWrapper(context, (prefs.getString("theme", "col").equals("col")) ? android.R.style.Theme_Holo_Dialog : android.R.style.Theme_Holo_Light_Dialog));
		a.setTitle(title);
		a.setMessage(text);
		a.setCancelable(true);
		a.setPositiveButton("Okay", null);
		a.create().show();
	}

	/**
	 * Displays HTML compatible alert dialog.
	 */
	public void MsgBox(Spanned text, Spanned title, Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		AlertDialog.Builder a  = new AlertDialog.Builder(new ContextThemeWrapper(context, (prefs.getString("theme", "col").equals("col")) ? android.R.style.Theme_Holo_Dialog : android.R.style.Theme_Holo_Light_Dialog));
		a.setTitle(title);
		a.setMessage(text);
		a.setCancelable(true);
		a.setPositiveButton("Okay", null);
		a.create().show();
	}

	/**
	 * Rounds a number.
	 */
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
	}
}
