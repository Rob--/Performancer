package io.github.rob__.performancer.storage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
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

import java.text.DecimalFormat;

import io.github.rob__.performancer.R;
import io.github.rob__.performancer.Tools;
import io.github.rob__.performancer.storage.info.InfoStorage;


public class Storage extends Activity {

	SharedPreferences prefs;

	boolean detailsExpanded = false;

	final Handler handler = new Handler();
	Parcelable lvState;

	RelativeLayout rlStorage;
	TextView lblUsageStorage;
	Button btnDetailsStorage;
	PieChart pcStorage;
	ListView lvStorage;
	TransitionDrawable transition;

	DecimalFormat d = new DecimalFormat("###,###,###.###");

	Tools tools = new Tools();
	InfoStorage infoStorage = new InfoStorage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    //prefs = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
	    prefs = PreferenceManager.getDefaultSharedPreferences(this);

	    if(prefs.getString("theme", "col").equals("col")){
		    // Matching Action Bars
		    //setTheme(R.style.AppThemeColourful);
		    setContentView(R.layout.activity_storage_colourful);
		    rlStorage   = (RelativeLayout) findViewById(R.id.rlStorage);
		    transition  = (TransitionDrawable) rlStorage.getBackground();
	    } else {
		    //setTheme(R.style.AppThemeMinimalistic);
		    setContentView(R.layout.activity_storage_minimalistic);
	    }

	    infoStorage .init(this);
	    // No sliding menu - thanks to TabActivity
	    // we can attach it directly

	    // Loads all views.
	    lblUsageStorage     = (TextView)    findViewById(R.id.lblUsageStorage   );
	    pcStorage           = (PieChart)    findViewById(R.id.pcStorage         );
	    btnDetailsStorage   = (Button)      findViewById(R.id.btnDetailsStorage );
	    lvStorage           = (ListView)    findViewById(R.id.lvStorage         );
	    lvStorage           .setVisibility(View.GONE);

	    initiatePieChart();
	    btnDetailsStorage.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    animate();
		    }
	    });
    }

	Runnable update = new Runnable() {
		@Override
		public void run() {
			lvState = lvStorage .onSaveInstanceState();
			lvStorage           .setAdapter(infoStorage.populateListView(Storage.this));
			lvStorage           .onRestoreInstanceState(lvState);
		}
	};

	public void updateStart(){ handler.postDelayed(update, 1);  }
	public void updateStop (){ handler.removeCallbacks(update); }

	@Override
	public void onStop(){
		updateStop();
		super.onStop();
	}

	public void animate() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (!detailsExpanded) {
					updateStart();
					btnDetailsStorage.setText(R.string.details_c);
					btnDetailsStorage.startAnimation(tools.fadeIn());
					pcStorage.animate().setDuration(500).alpha(0f).setListener(null);
					lblUsageStorage.animate().setDuration(500).alpha(0f).setListener(null);
					lvStorage.setVisibility(View.VISIBLE);
					lvStorage.animate().setDuration(500).alpha(1f).setListener(null);
					if (prefs.getString("theme", "col").equals("col")) {
						transition.startTransition(2000);
					}
					detailsExpanded = !detailsExpanded;
				} else {
					updateStop();
					btnDetailsStorage.setText(R.string.details_e);
					btnDetailsStorage.startAnimation(tools.fadeIn());
					pcStorage.animate().setDuration(500).alpha(1f).setListener(null);
					lblUsageStorage.animate().setDuration(500).alpha(1f).setListener(null);
					lvStorage.animate().setDuration(500).alpha(0f).setListener(null);
					lvStorage.setVisibility(View.GONE);
					if (prefs.getString("theme", "col").equals("col")) {
						transition.reverseTransition(2000);
					}
					detailsExpanded = !detailsExpanded;
				}
			}
		});
	}

	public void initiatePieChart(){
		pcStorage.clearChart();
		pcStorage.addPieSlice(new PieModel("Available Space", (float) infoStorage.getAvailInternal(), (prefs.getString("theme", "col").equals("col") ? Color.parseColor("#3498db") : Color.parseColor("#bdc3c7"))));
		pcStorage.addPieSlice(new PieModel("Used Space"     , (float) infoStorage.getUsedInternal() , (prefs.getString("theme", "col").equals("col") ? Color.parseColor("#2ecc71") : Color.parseColor("#b0b0b0"))));
		pcStorage.setInnerPaddingColor(Color.parseColor((prefs.getString("theme", "col").equals("col") ? "#00000000" : "#ffffff")));
		pcStorage.setUseCustomInnerValue        (true);
		pcStorage.setAutoCenterInSlice          (false);
		pcStorage.setCurrentItem                (1);
		pcStorage.setOnItemFocusChangedListener (new IOnItemFocusChangedListener() {
			@Override
			public void onItemFocusChanged(int i) {
				if(pcStorage.getCurrentItem() == 0){
					pcStorage.setInnerValueString(infoStorage.validateValue(infoStorage.getAvailInternal()  , true));
				} else {
					pcStorage.setInnerValueString(infoStorage.validateValue(infoStorage.getUsedInternal()   , true));
				}
			}
		});
		pcStorage.startAnimation();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.storage, menu);
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
