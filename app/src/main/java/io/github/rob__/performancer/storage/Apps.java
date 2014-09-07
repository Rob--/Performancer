package io.github.rob__.performancer.storage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.communication.IOnItemFocusChangedListener;
import org.eazegraph.lib.models.PieModel;

import java.util.Random;

import io.github.rob__.performancer.R;
import io.github.rob__.performancer.storage.info.InfoApps;

public class Apps extends Activity {

	SharedPreferences prefs;

	TransitionDrawable transition;

	RelativeLayout rlApps;
	TextView lblUsageApps;
	PieChart pcApps;
	Button btnDetailsApps;
	ListView lvApps;

	Object[] apps;
	String[] names;
	String[] packages;
	long[] sizes;
	long[] itemSizes;   // when adding slices we're filtering out items
					    // this will catch items that aren't filtered
					    // so we can correctly update the values for the items.
	int counter;        // counter for itemSizes.
	double maxSize;        // used to keep all PieChart item in view.
	InfoApps infoApps = new InfoApps();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    //prefs = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
	    prefs = PreferenceManager.getDefaultSharedPreferences(this);

	    if(prefs.getString("theme", "col").equals("col")){
		    // Matching Action Bars
		    //setTheme(R.style.AppThemeColourful);
		    setContentView(R.layout.activity_apps_colourful);
		    rlApps   = (RelativeLayout) findViewById(R.id.rlApps);
		    transition  = (TransitionDrawable) rlApps.getBackground();
	    } else {
		    //setTheme(R.style.AppThemeMinimalistic);
		    setContentView(R.layout.activity_apps_minimalistic);
	    }

	    infoApps.init(this);
	    // No sliding menu - thanks to TabActivity
	    // we can attach it directly

	    // Loads all views.
	    rlApps          = (RelativeLayout)  findViewById(R.id.rlApps        );
	    lblUsageApps    = (TextView)        findViewById(R.id.lblUsageApps  );
	    pcApps          = (PieChart)        findViewById(R.id.pcApps        );
	    btnDetailsApps  = (Button)          findViewById(R.id.btnDetailsApps);
	    lvApps          = (ListView)        findViewById(R.id.lvApps        );
	    lvApps          .setVisibility(View.GONE);

	    btnDetailsApps.setOnClickListener(new View.OnClickListener(){
		    @Override
	        public void onClick(View v){
			    runOnUiThread(new Runnable() {
				    @Override
				    public void run() {

				    }
			    });
		    }
	    });
	    initiatePieChart();
    }

	public void initiatePieChart(){
		pcApps.clearChart();
		pcApps.setInnerPaddingColor(prefs.getString("theme", "col").equals("col") ? getResources().getColor(R.color.navy_blue_light) : Color.parseColor("#ffffff")); // due to the bright colours the background must be "see through"

		// Add pie chart app data.
		apps        = infoApps.returnAppNamesAndSizes(this);
		names       = (String[])    apps[0];
		packages    = (String[])    apps[1];
		sizes       = (long[])      apps[2];
		itemSizes   = new long[sizes.length];
		counter     = 0;
		maxSize     = 0;
		for(long s : sizes){
			if(maxSize < s){
				maxSize = s;
			}
		}
		for(int i = 0; i < names.length; i++){
			Log.d("WOW!", "Name: " + names[i] + "\t Size: " + sizes[i] + "\t Package: " + packages[i]);
			if(!packages[i].contains("com.android") && !packages[i].contains("com.google") && !packages[i].equals("android")){
				if (!names[i].contains("Google") && !names[i].contains("com.") && !names[i].contains("org.") && !names[i].contains("net.") && !names[i].contains("co.uk") && !names[i].contains("uk.co") && !names[i].contains("io.")) {
					pcApps.addPieSlice(new PieModel((prefs.getBoolean("advanced", false)) ? packages[i] : names[i], ((float) (sizes[i] < (maxSize / 20) ? sizes[i] + maxSize / 20 : sizes[i])), returnRandomColour()));
					itemSizes[counter++] = sizes[i];
				}
			}
		}

		pcApps.setUseCustomInnerValue       (true);
		pcApps.setAutoCenterInSlice         (true);
		pcApps.setCurrentItem               (2);
		pcApps.setOnItemFocusChangedListener(new IOnItemFocusChangedListener() {
			@Override
			public void onItemFocusChanged(int i) {
				pcApps.setInnerValueString(infoApps.validateValue(itemSizes[i], true));
			}
		});
		pcApps.startAnimation();
	}

	Random r = new Random();
	public int returnRandomColour() {
		if(prefs.getString("theme", "col").equals("col")) {
			return Color.HSVToColor(new float[] { (float) Math.floor(Math.random() * 360), 0.7f, 0.9f});
		} else {
			return Color.HSVToColor(new float[] { 0f, 0f, 0.2f + (r.nextInt(80) / 100f)});
			//return Color.rgb(rgb, rgb, rgb);
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.apps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
