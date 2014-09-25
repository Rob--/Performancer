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
import io.github.rob__.performancer.info.InfoCPU;

public class CPU extends Activity {
	SharedPreferences prefs;
	
	boolean detailsExpanded = false;
	RelativeLayout rlCPU;
	TransitionDrawable transition;
	final Handler handler = new Handler();
	Parcelable state;
	
	TextView lblUsageCPU;
	HoloCircularProgressBar cpbCPU;
	Button btnDetailsCPU;
	ListView lvCPU;
		
	InfoCPU infoCPU = new InfoCPU();
	Tools tools = new Tools();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//prefs = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(prefs.getString("theme", "col").equals("col")){
            // Matching Action Bars
            //setTheme(R.style.AppThemeColourful);
			setContentView(R.layout.activity_cpu_colourful);
			rlCPU       = (RelativeLayout) findViewById(R.id.rlCPU);
			transition  = (TransitionDrawable) rlCPU.getBackground();
		} else {
            //setTheme(R.style.AppThemeMinimalistic);
			setContentView(R.layout.activity_cpu_minimalistic);
		}
		
		infoCPU .init(this);
		tools   .addMenu(getApplicationContext(), this);
		tools   .populateMenu((ListView) findViewById(R.id.menuItems), this, this, "CPU");
		tools   .toggle();
		
		//Loads all the layout items for use.
		cpbCPU      = (HoloCircularProgressBar) findViewById(R.id.cpbCPU);
		lblUsageCPU = (TextView) findViewById(R.id.lblUsageCPU);
		lvCPU       = (ListView) findViewById(R.id.lvCPU);
		lvCPU       .setVisibility(View.GONE);

		// Initial loading
		updateP();
		lblUsageCPU .setText("CPU: " + String.valueOf(p) + "%");
		cpbCPU      .setProgress(Float.valueOf(String.valueOf(p / 100)));

		update1Start();
		btnDetailsCPU = (Button) findViewById(R.id.btnDetailsCPU);
		btnDetailsCPU.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				animate();
			}
		});
					
		lvCPU.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	String[] t = lvCPU.getItemAtPosition(position).toString().split(":");
		    	switch (t[0]){
		    	case "Cores":
		    		tools.MsgBox("The number of cores the device contains.", "Cores", CPU.this);
		    		break;
		    	case "Max Clock Speed":
		    		tools.MsgBox("The maximum number of cycles a core can handle a second. 1,000 MHz = 1,000,000,000 cycles per second.", "Clock Speed", CPU.this);
		    		break;
		    	case "Min Clock Speed":
		    		tools.MsgBox("The minimum number of cycles a core can handle a second. 1,000 MHz = 1,000,000,000 cycles per second.", "Clock Speed", CPU.this);
		    		break;
		    	case "Architecture":
		    		tools.MsgBox("The architecture of the processor.", "Architecture", CPU.this);
		    		break;
		    	case "Model":
		    		tools.MsgBox("The processor model.", "Processor", CPU.this);
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
				p = infoCPU.getCpuPercentage();
			}
		};
		t.start();
	}
	
	Runnable update1 = new Runnable() { 
        @Override 
        public void run() {
        	updateP();
            lblUsageCPU .setText(String.valueOf(p).replace(".0", "") + "%");
    		cpbCPU      .setProgress(Float.valueOf(String.valueOf(p / 100)));
        	handler     .postDelayed(this, prefs.getInt("delay", 500) + 1000);
        }
    };
    
    Runnable update2 = new Runnable() { 
        @Override 
        public void run() {
        	state = lvCPU   .onSaveInstanceState();
        	lvCPU           .setAdapter(infoCPU.populateListView(CPU.this));
        	lvCPU           .onRestoreInstanceState(state);
        	handler         .postDelayed(this, prefs.getInt("delay", 500) + 1000);
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
            		update1Stop();
            		update2Start();
            		btnDetailsCPU   .setText(R.string.details_c);
            		btnDetailsCPU   .startAnimation(tools.fadeIn());
            		cpbCPU          .animate().setDuration(500).alpha(0f).setListener(null);
            		lblUsageCPU     .animate().setDuration(500).alpha(0f).setListener(null);
            		lvCPU           .setVisibility(View.VISIBLE);
            		lvCPU           .animate().setDuration(500).alpha(1f).setListener(null);
		            if(prefs.getString("theme", "col").equals("col")){
            			transition.startTransition(2000);
            		}
            		detailsExpanded = true;
            	} else {
            		update1Start();
            		update2Stop();
            		btnDetailsCPU   .setText(R.string.details_e);
            		btnDetailsCPU   .startAnimation(tools.fadeIn());
            		cpbCPU          .animate().setDuration(500).alpha(1f).setListener(null);
            		lblUsageCPU     .animate().setDuration(500).alpha(1f).setListener(null);
            		lvCPU           .animate().setDuration(500).alpha(0f).setListener(null);
            		lvCPU           .setVisibility(View.GONE);
		            if(prefs.getString("theme", "col").equals("col")){
            			transition.reverseTransition(2000);
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
