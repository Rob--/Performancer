package io.github.rob__.performancer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.passsy.holocircularprogressbar.HoloCircularProgressBar;
import io.github.rob__.performancer.info.InfoBattery;

public class Battery extends Activity {
	SharedPreferences prefs;
	
	boolean detailsExpanded = false;
	RelativeLayout rlBattery;
	TransitionDrawable transition;
	
	final Handler handler = new Handler();
	Parcelable state;
	
	TextView lblUsageBattery;
	HoloCircularProgressBar cpbBattery;
	Button btnDetailsBattery;
	ListView lvBattery;
		
	InfoBattery infoBattery = new InfoBattery();
	Tools tools = new Tools();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//prefs = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(prefs.getString("theme", "col").equals("col")){
            // Matching Action Bars
            //setTheme(R.style.AppThemeColourful);
			setContentView(R.layout.activity_battery_colourful);
			rlBattery   = (RelativeLayout)      findViewById(R.id.rlBattery);
			transition  = (TransitionDrawable)  rlBattery.getBackground();
		} else {
            //setTheme(R.style.AppThemeMinimalistic);
			setContentView(R.layout.activity_battery_minimalistic);
		}
		
		infoBattery .init(this);
		tools       .addMenu(getApplicationContext(), this);
		tools       .populateMenu((ListView) findViewById(R.id.menuItems), this, this, "Battery");
		tools       .toggle();
		
		//Loads all the layout items for use.
		cpbBattery      = (HoloCircularProgressBar) findViewById(R.id.cpbBattery);
		lblUsageBattery = (TextView) findViewById(R.id.lblUsageBattery);
		lvBattery       = (ListView) findViewById(R.id.lvBattery);
		lvBattery       .setVisibility(View.GONE);

		// Initial loading
		updateP();
		lblUsageBattery .setText(String.valueOf(Math.round(p)) + "%");
		cpbBattery      .setProgress(Float.valueOf(String.valueOf(p / 100)));

		update1Start();
		btnDetailsBattery = (Button) findViewById(R.id.btnDetailsBattery);
		btnDetailsBattery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				animate();
			}
		});
		
		lvBattery.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	String[] t = lvBattery.getItemAtPosition(position).toString().split(":");
		    	switch (t[0]){
		    	case "Health":
		    		tools.MsgBox("The health status of the battery.", "Health", Battery.this);
		    		break;
		    	case "Tech":
		    		tools.MsgBox("The type of battery.", "Technology", Battery.this);
		    		break;
		    	case "Plug Type":
		    		tools.MsgBox("The type of input used to charge the battery.", "Plug Type", Battery.this);
		    		break;
		    	case "Status":
		    		tools.MsgBox("The status of the battery.", "Status", Battery.this);
		    		break;
		    	case "Voltage":
		    		tools.MsgBox("The current battery voltage level.", "Voltage", Battery.this);
		    		break;
		    	case "Temp":
		    		tools.MsgBox("The temperature of the battery.", "Temperature", Battery.this);
		    		break;
		    	case "Battery Present":
		    		tools.MsgBox("Indication to whether a battery is present.", "Battery Present", Battery.this);
		    		break;
		    	default:
		    		break;
		    	}
		    }
		});
		
	}
	
	double p = 0;
	public void updateP(){
		Thread t = new Thread(){
			@Override
			public void run(){
				p = infoBattery.getBatteryPercentage(getApplicationContext());
			}
		};
		t.start();
	}
	
	Runnable update1 = new Runnable() { 
        @Override 
        public void run() {
        	updateP();
            lblUsageBattery .setText(String.valueOf(Math.round(p)) + "%");
    		cpbBattery      .setProgress(Float.valueOf(String.valueOf(p / 100)));
        	handler         .postDelayed(this, prefs.getInt("delay", 500) + 5000);
        }
    };
    
    Runnable update2 = new Runnable() { 
        @Override 
        public void run() {
        	state = lvBattery   .onSaveInstanceState();
        	lvBattery           .setAdapter(infoBattery.populateListView(Battery.this));
        	lvBattery           .onRestoreInstanceState(state);
        	handler             .postDelayed(this, prefs.getInt("delay", 500) + 5000);
        }
    };
    
    public void update1Start(){
    	handler.postDelayed(update1, 1);
    }
    
    public void update1Stop(){
    	handler.removeCallbacks(update1);
    }
    
    public void update2Start(){
    	handler.postDelayed(update2, 1);
    }
    
    public void update2Stop(){
    	handler.removeCallbacks(update2);
    }
    
    @Override
    public void onStop(){
    	update1Stop();
    	update2Stop();
    	super.onStop();
    }
    
    public void animate(){
		runOnUiThread(new Runnable() {
            public void run() {
            	if(!detailsExpanded){
            		infoBattery     .updateBatteryInformation(getApplicationContext());
            		update1Stop();
            		update2Start();
            		btnDetailsBattery   .setText(R.string.details_c);
            		btnDetailsBattery   .startAnimation(tools.fadeIn());
            		cpbBattery          .animate().setDuration(500).alpha(0f).setListener(null);
            		lblUsageBattery     .animate().setDuration(500).alpha(0f).setListener(null);
            		lvBattery           .setAdapter(infoBattery.populateListView(getApplicationContext()));
            		lvBattery           .setVisibility(View.VISIBLE);
            		lvBattery           .animate().setDuration(500).alpha(1f).setListener(null);
		            if(prefs.getString("theme", "col").equals("col")){
            			transition.startTransition(2000);
            		}
            		detailsExpanded = true;
            	} else {
            		update1Start();
            		update2Stop();
            		btnDetailsBattery   .setText(R.string.details_e);
            		btnDetailsBattery   .startAnimation(tools.fadeIn());
            		cpbBattery          .animate().setDuration(500).alpha(1f).setListener(null);
            		lblUsageBattery     .animate().setDuration(500).alpha(1f).setListener(null);
            		lvBattery           .animate().setDuration(500).alpha(0f).setListener(null);
            		lvBattery           .setVisibility(View.GONE);
		            if(prefs.getString("theme", "col").equals("col")){
            			transition  .reverseTransition(2000);
            		}	
            		detailsExpanded = false;
            	}
			}
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		tools.actionBarItemSelected(item, this, Battery.this);
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    Window window = getWindow();
	    window.setFormat(PixelFormat.RGBA_8888);
	}
}
