package io.github.rob__.performancer.storage;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.communication.IOnItemFocusChangedListener;
import org.eazegraph.lib.models.PieModel;

import io.github.rob__.performancer.R;
import io.github.rob__.performancer.Tools;
import io.github.rob__.performancer.storage.info.InfoStorage;

public class StorageFragment extends android.support.v4.app.Fragment {

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

	Tools tools = new Tools();
	InfoStorage infoStorage = new InfoStorage();

	View v;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//prefs = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

		if(prefs.getString("theme", "col").equals("col")){
			// Matching Action Bars
			//setTheme(R.style.AppThemeColourful);
			v = inflater.inflate(R.layout.fragment_storage_colourful, container, false);
			rlStorage   = (RelativeLayout) v.findViewById(R.id.rlStorage);
			transition  = (TransitionDrawable) rlStorage.getBackground();
		} else {
			//setTheme(R.style.AppThemeMinimalistic);
			v = inflater.inflate(R.layout.fragment_storage_minimalistic, container, false);
		}

		infoStorage.init(v.getContext());
		// No sliding menu - thanks to TabActivity
		// we can attach it directly

		// Loads all views.
		lblUsageStorage     = (TextView)    v.findViewById(R.id.lblUsageStorage);
		pcStorage           = (PieChart)    v.findViewById(R.id.pcStorage);
		btnDetailsStorage   = (Button)      v.findViewById(R.id.btnDetailsStorage);
		lvStorage           = (ListView)    v.findViewById(R.id.lvStorage);
		lvStorage           .setVisibility(View.GONE);

		initiatePieChart();
		btnDetailsStorage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				animate();
			}
		});

		return v;
	}

	Runnable update = new Runnable() {
		@Override
		public void run() {
			lvState = lvStorage .onSaveInstanceState();
			lvStorage           .setAdapter(infoStorage.populateListView(getActivity().getApplicationContext()));
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
		getActivity().runOnUiThread(new Runnable() {
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
		pcStorage.setInnerPaddingColor(prefs.getString("theme", "col").equals("col") ?  getResources().getColor(R.color.navy_blue) : Color.parseColor("#00000000"));
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

	public static StorageFragment newInstance() {
		return new StorageFragment();
	}
}