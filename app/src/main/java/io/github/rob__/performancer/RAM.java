package io.github.rob__.performancer;

import android.app.Activity;
import android.app.ActivityManager;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.passsy.holocircularprogressbar.HoloCircularProgressBar;
import io.github.rob__.performancer.info.InfoRAM;

public class RAM extends Activity {
	
	SharedPreferences prefs;
	
	boolean detailsExpanded = false;
	
	final Handler handler = new Handler();
	Parcelable lvState;
	
	TextView lblUsageRAM;
	HoloCircularProgressBar cpbRAM;
	Button btnDetailsRAM;
	ListView lvRAM;
	RelativeLayout rlRAM;
	TransitionDrawable transition;
		
	InfoRAM infoRAM = new InfoRAM();
	Tools tools = new Tools();
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//prefs = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(prefs.getString("theme", "col").equals("col")){
			// Matching Action Bars
            //setTheme(R.style.AppThemeColourful);
			setContentView(R.layout.activity_ram_colourful);
			rlRAM       = (RelativeLayout) findViewById(R.id.rlRAM);
			transition  = (TransitionDrawable) rlRAM.getBackground();
		} else {
			//setTheme(R.style.AppThemeMinimalistic);
			setContentView(R.layout.activity_ram_minimalistic);
		}

		infoRAM .init(this, (ActivityManager) getSystemService(ACTIVITY_SERVICE));
		tools   .addMenu(getApplicationContext(), this);
		tools   .populateMenu((ListView) findViewById(R.id.menuItems), this, this, "RAM");
		tools   .toggle();
		
		//Loads all the layout items for use.
		cpbRAM          = (HoloCircularProgressBar) findViewById(R.id.cpbRAM);
		lblUsageRAM     = (TextView) findViewById(R.id.lblUsageRAM);
		lvRAM           = (ListView) findViewById(R.id.lvRAM);
		lvRAM           .setVisibility(View.GONE);
		btnDetailsRAM   = (Button) findViewById(R.id.btnDetailsRAM);

		// Usually here we'd run through out initial
		// percentage updates. But the RandomAccessFile
		// encounters errors (presumably from loading
		// up the app and reprinting the RAF). (Bad
		// File Number error).

		update1Start();
		btnDetailsRAM.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				animate();
			}
		});
				
		lvRAM.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	String[] t = lvRAM.getItemAtPosition(position).toString().split(":");
		    	switch (t[0]){
		    	case "Total Memory":
		    		tools.MsgBox("Total amount of physical RAM.", "Total Memory", RAM.this);
		    		break;
		    	case "Free Memory":
		    		tools.MsgBox("The amount of physical RAM left unused.", "Free Memory", RAM.this);
		    		break;
		    	case "Cached":
		    		tools.MsgBox("The amount of physical RAM used as cache memory.", "Cached", RAM.this);
		    		break;
		    	case "Active":
		    		tools.MsgBox("The total amount of buffer or page cache memory, that is in active use. It's memory that has been recently used and is usually not reclaimed for other purposes.", "Active", RAM.this);
		    		break;
		    	case "Inactive":
		    		tools.MsgBox("The total amount of buffer or page cache memory that is free and available. It's memory that has not been recently used and can be reclaimed for other purposes.", "Inactive", RAM.this);
		    		break;
		    	case "Kernel":
		    		tools.MsgBox("The used amount of memory that is directly mapped into kernel space (used by the OS).", "Kernel", RAM.this);
		    		break;
		    	case "Non-kernel":
		    		tools.MsgBox("The used amount of memory that is not directly mapped into kernel space (used by the OS).", "Non-kernel", RAM.this);
		    		break;
		    	case "Mem Threshold":
		    		tools.MsgBox("The threshold (of free memory) at which we consider memory to be low and start killing background services and other non-extraneous processes.", "Threshold", RAM.this);
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
				p = infoRAM.getMemPercentage();
			}
		};
		t.start();
	}
	
    Runnable update1 = new Runnable() { 
        @Override 
        public void run() {
        	updateP();
        	lblUsageRAM .setText(String.valueOf(p) + "%");
        	cpbRAM      .setProgress((Float.valueOf(String.valueOf(p / 100))));
            handler     .postDelayed(this, prefs.getInt("delay", 500));
        }
    };
    
    Runnable update2 = new Runnable() {
    	@Override
    	public void run() {
		    lvState = lvRAM .onSaveInstanceState();
    		lvRAM           .setAdapter(infoRAM.populateListView(RAM.this));
		    lvRAM           .onRestoreInstanceState(lvState);
    		handler         .postDelayed(this, prefs.getInt("delay", 500));
    	}
    };
    
    public void update1Start() { handler.post(update1);  }
    public void update1Stop () { handler.removeCallbacks(update1); }
    public void update2Start() { handler.post(update2);  }
    public void update2Stop (){
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
            		update1Stop();
            		update2Start();
            		btnDetailsRAM   .setText(R.string.details_c);
            		btnDetailsRAM   .startAnimation(tools.fadeIn());
            		cpbRAM          .animate().setDuration(500).alpha(0f).setListener(null);
            		lblUsageRAM     .animate().setDuration(500).alpha(0f).setListener(null);
            		lvRAM           .setVisibility(View.VISIBLE);
            		lvRAM           .animate().setDuration(500).alpha(1f).setListener(null);
            		if(prefs.getString("theme", "col").equals("col")){
            			transition.startTransition(2000);
            		}
            		detailsExpanded = !detailsExpanded;
            	} else {
            		update1Start();
            		update2Stop();
            		btnDetailsRAM   .setText(R.string.details_e);
            		btnDetailsRAM   .startAnimation(tools.fadeIn());
            		cpbRAM          .animate().setDuration(500).alpha(1f).setListener(null);
            		lblUsageRAM     .animate().setDuration(500).alpha(1f).setListener(null);
            		lvRAM           .animate().setDuration(500).alpha(0f).setListener(null);
            		lvRAM           .setVisibility(View.GONE);
            		if(prefs.getString("theme", "col").equals("col")){
            			transition.reverseTransition(2000);
            		}
            		detailsExpanded = !detailsExpanded;
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
		tools.actionBarItemSelected(item, this);
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    getWindow().setFormat(PixelFormat.RGBA_8888);
	}
}
