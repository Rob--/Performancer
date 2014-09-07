package io.github.rob__.performancer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class DataDialog {
	SharedPreferences prefs;
	
	public void chooseSettings(final Context context) {
		//prefs = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		final int dialog_theme = (prefs.getString("theme", "col").equals("col")) ? android.R.style.Theme_Holo_Dialog : android.R.style.Theme_Holo_Light_Dialog;

		Dialog sDialog = new Dialog(new ContextThemeWrapper(context, dialog_theme));
		sDialog.setContentView  (R.layout.seekdialog_layout);
		sDialog.setTitle        ("Set refresh delay.");
		sDialog.setCancelable   (true);
		sDialog.show            ();

		final SeekBar seekbar = (SeekBar) sDialog.findViewById(R.id.sbSeekDialog);
		seekbar.setMax(2000);
		seekbar.setProgress(prefs.getInt("delay", 500));
		final TextView ms = (TextView) sDialog.findViewById(R.id.tvSeekDialog);
		ms.setText(String.valueOf(seekbar.getProgress()) + " ms");

		OnSeekBarChangeListener sbListener = new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
				if( seekbar.getProgress()   < 50    )                                       { seekbar.setProgress(50    ); }
				if((seekbar.getProgress()   > 450   )   && (seekbar.getProgress() < 550 ))  { seekbar.setProgress(500   ); }
				if((seekbar.getProgress()   > 950   )   && (seekbar.getProgress() < 1050))  { seekbar.setProgress(1000  ); }
				if((seekbar.getProgress()   > 1450  )   && (seekbar.getProgress() < 1550))  { seekbar.setProgress(1500  ); }
				if( seekbar.getProgress()   > 1950  )                                       { seekbar.setProgress(2000  ); }

				ms.setText(String.valueOf(seekbar.getProgress()) + " ms");
				prefs.edit().putInt("delay", seekbar.getProgress()).apply();
			}

			@Override public void onStartTrackingTouch(SeekBar arg0) {}
			@Override public void onStopTrackingTouch(SeekBar arg0) {}
		};
		seekbar.setOnSeekBarChangeListener(sbListener);

	}
	
	public void MsgBox(String text, String title, Context context, int dialog_theme){
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(new ContextThemeWrapper(context, dialog_theme));
		dlgAlert.setTitle           (title);
		dlgAlert.setMessage         (text);
		dlgAlert.setCancelable      (true);
		dlgAlert.setPositiveButton  ("Okay", null);
		dlgAlert.create             ()
				.show               ();
	}
}