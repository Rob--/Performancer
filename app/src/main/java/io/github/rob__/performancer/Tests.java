package io.github.rob__.performancer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Vector;

import io.github.rob__.performancer.info.InfoRAM;

public class Tests extends Activity {

	SharedPreferences prefs;
	
	ActionProcessButton btnTest1;
	ActionProcessButton btnTest2;
	ActionProcessButton btnTest3;
	ActionProcessButton btnTest4;
	TextView status;
	TextView status2;
	
	boolean cont = false; // checks if PiCont test should run
	boolean consuming = false; // If RAM is being consumed and activities switch, stop dat shizzle.
	boolean contRAM = false; // checks if consumeRamSlowly should run

	Tools tools = new Tools();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//prefs = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(prefs.getString("theme", "col").equals("col")){
            // Matching Action Bars
            //setTheme(R.style.AppThemeColourful);
			setContentView(R.layout.activity_tests_colourful);
		} else {
            //setTheme(R.style.AppThemeMinimalistic);
			setContentView(R.layout.activity_tests_minimalistic);
		}
		
		tools.addMenu(getApplicationContext(), this);
		tools.populateMenu((ListView) findViewById(R.id.menuItems), this, this, "Tests");
		tools.toggle();
		
		status = (TextView) findViewById(R.id.tvTestStatus);
		status2 = (TextView) findViewById(R.id.tvTestStatus2);
		
		btnTest1 = (ActionProcessButton) findViewById(R.id.btnTest1);
		btnTest1.setMode(ActionProcessButton.Mode.ENDLESS);
		
		btnTest2 = (ActionProcessButton) findViewById(R.id.btnTest2);
		btnTest2.setMode(ActionProcessButton.Mode.ENDLESS);
		
		btnTest3 = (ActionProcessButton) findViewById(R.id.btnTest3);
		btnTest3.setMode(ActionProcessButton.Mode.ENDLESS);
		
		btnTest4 = (ActionProcessButton) findViewById(R.id.btnTest4);
		btnTest4.setMode(ActionProcessButton.Mode.ENDLESS);
		
		btnTest1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				buttonProcess(true, btnTest1);
				new Test1calculatePi().execute();
			}
		});
				
		btnTest2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				buttonProcess(true, btnTest2);
				if(!cont){
					cont = true;
					new Test2calculatePiContinuously().execute();
				} else {
					cont = false;
					buttonProcess(false, btnTest2);
				}
			}
		});
		
		btnTest3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				buttonProcess(true, btnTest3);
				new Test3consumeMem().execute();
			}
		});
		
		btnTest4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				buttonProcess(true, btnTest4);
				if(!contRAM){
					contRAM = true;
					new Test4consumeMemSlowly().execute();
				} else{
					contRAM = false;
					buttonProcess(false, btnTest4);
				}
			}
		});
		
	}
	
	public void buttonProcess(final boolean disable, final ActionProcessButton btn){
		new Thread(new Runnable() {
			public void run() {
				btn.post(new Runnable() {
					public void run() {
						if(disable){
							btn     .setProgress(1);
							btnTest1.setEnabled(false);
							btnTest2.setEnabled(false);
							btnTest3.setEnabled(false);
							btnTest4.setEnabled(false);
						} else {
							btn     .setProgress(0);
							btnTest1.setEnabled(true);
							btnTest2.setEnabled(true);
							btnTest3.setEnabled(true);
							btnTest4.setEnabled(true);
						}
						if(btn == btnTest2 || btn == btnTest4){
							btn.setEnabled(true);
						}
					}
				});
			}
		}).start();
	}
	
	@Override
	public void onStop(){
		consuming = false;
		super.onStop();
	}
	
	@Override
	public void onPause(){
		consuming = false;
		super.onStop();
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
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	int lastInt = 2;
	BigDecimal four = new BigDecimal(4);
	BigDecimal pi = new BigDecimal(3);
	
	boolean running = false;
	boolean add;
	int history = 0;
	int dec_place = 1;
	long start = 0;
	long end = 0;
	int iterations = 0;
	String temp_pi = "3.14";
	String actual_pi = "";
	String old_pi = "";
		
	class Test1calculatePi extends AsyncTask<Void, String, Void> {
	    @Override
	    protected Void doInBackground(Void... params) {
	    	add = true;
	    	start = System.currentTimeMillis();
	    	while(true){
	    		running = true;
	    		iterations++;
				if(add){
					pi = pi.add(four.divide(new BigDecimal((1.0 * lastInt * (lastInt + 1) * (lastInt + 2))), 200, RoundingMode.HALF_UP));
					add = !add;
				} else {
					pi = pi.subtract(four.divide(new BigDecimal((1.0 * lastInt * (lastInt + 1) * (lastInt + 2))), 200, RoundingMode.HALF_UP));
					add = !add;
				}
				
				if(temp_pi.replace("3.", "").substring(0, dec_place).equals(String.valueOf(pi).replace("3.", "").substring(0, dec_place))){
					history++;
					if(history == 20){
						actual_pi = "3." + temp_pi.replace("3.", "").substring(0, dec_place);
						dec_place++;
						history = 0;
					}
				} else {
					history = 0;
				}
				
				temp_pi = String.valueOf(pi);			
				lastInt += 2;
				
				if(actual_pi.length() == 9 + 2){
					break;
				}
				
				publishProgress(String.valueOf(pi).replace(actual_pi, ""), actual_pi);
	    	}
	    	end = System.currentTimeMillis();
	    	Tests.this.runOnUiThread(new Runnable(){
	    		@Override
	    		public void run(){
	    			tools.MsgBox("Calculated 8 decimal places of Pi within " + String.valueOf(tools.round((end - start) / 1000.0, 3)) + " seconds with " + String.valueOf(iterations - 1) + " iterations.", "CPU Test #1", Tests.this);
	    			buttonProcess(false, btnTest1);
	    			add = true;
	    			history = 0;
	    			dec_place = 1;
	    			start = 0;
	    			end = 0;
	    			iterations = 0;
	    			temp_pi = "3.14";
	    			actual_pi = "";
	    			old_pi = "";
	    			publishProgress("", "");
	    		}
	    	});
	    	
	    	return null;
	    }

	    @Override
	    protected void onProgressUpdate(String... progress) {
	    	status.setText(progress[0]);
	    	status2.setText(progress[1]);
	    }
	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	class Test2calculatePiContinuously extends AsyncTask<Void, String, Void> {
	    @Override
	    protected Void doInBackground(Void... params) {
	    	add = true;
	    	start = System.currentTimeMillis();
	    	while(cont){
	    		running = true;
	    		iterations++;
				if(add){
					pi = pi.add(four.divide(new BigDecimal((1.0 * lastInt * (lastInt + 1) * (lastInt + 2))), 200, RoundingMode.HALF_UP));
					add = !add;
				} else {
					pi = pi.subtract(four.divide(new BigDecimal((1.0 * lastInt * (lastInt + 1) * (lastInt + 2))), 200, RoundingMode.HALF_UP));
					add = !add;
				}
				
				if(temp_pi.replace("3.", "").substring(0, dec_place).equals(String.valueOf(pi).replace("3.", "").substring(0, dec_place))){
					history++;
					if(history == 20){
						actual_pi = "3." + temp_pi.replace("3.", "").substring(0, dec_place);
						dec_place++;
						history = 0;
					}
				} else {
					history = 0;
				}
				
				temp_pi = String.valueOf(pi);			
				
				long t = System.currentTimeMillis();
				while((System.currentTimeMillis() - t) < 10){}
				lastInt += 2;
				
				publishProgress(String.valueOf(pi).replace(actual_pi, ""), actual_pi);
	    	}
	    	end = System.currentTimeMillis();
	    	Tests.this.runOnUiThread(new Runnable(){
	    		@Override
	    		public void run(){
	    			tools.MsgBox("Calculated " + (String.valueOf(actual_pi).length() - 2) + " decimal places of Pi within " + String.valueOf(tools.round((end - start) / 1000.0, 3)) + " seconds with " + String.valueOf(iterations - 1) + " iterations.", "CPU Test #2", Tests.this);
	    			add = true;
	    			history = 0;
	    			dec_place = 1;
	    			start = 0;
	    			end = 0;
	    			iterations = 0;
	    			temp_pi = "3.14";
	    			actual_pi = "";
	    			old_pi = "";
	    			publishProgress("", "");
	    		}
	    	});
	    	
	    	return null;
	    }

	    @Override
	    protected void onProgressUpdate(String... progress) {
	    	status.setText(progress[0]);
	    	status2.setText(progress[1]);
	    }
	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	Vector<byte[]> v = new Vector<>();
	InfoRAM infoRAM = new InfoRAM();
	DecimalFormat formatter = new DecimalFormat("###,###,###,###,###,###");
	
	class Test3consumeMem extends AsyncTask<Void, String, Void> {
	    @Override
	    protected Void doInBackground(Void... params) {
	    	consuming = true;
	    	start = System.currentTimeMillis();
	    	while((Integer.parseInt(infoRAM.getMemFree()) > ((prefs.getLong("Memory Threshold MB", 125)) * 1024)) && consuming){
	    		try {
	    			byte[] d = new byte[(1024 * 1024) * 5];
	    			v.add(d);
	    		} catch(OutOfMemoryError e){
	    			v.clear();
	    			byte[] d = null;
	    			break;
	    		}
	    		publishProgress("Free Memory: " + ((prefs.getString("data unit", "mb").equals("kb")) ? String.valueOf(formatter.format(Integer.parseInt(infoRAM.getMemFree()))) + " kB" : String.valueOf(formatter.format(Integer.parseInt(infoRAM.getMemFree()) / 1024)) + " MB"), "Free Memory: " + String.valueOf(formatter.format(Integer.parseInt(infoRAM.getMemFree()) * 1024)) + " bytes");
	    	}
	    	end = System.currentTimeMillis();
	    		
	    	Tests.this.runOnUiThread(new Runnable(){
	    		@Override
	    		public void run(){
	    			tools.MsgBox("Took " + String.valueOf(tools.round((end - start) / 1000.0, 3)) + " seconds to consume RAM without entering critical state.", "RAM Test #1", Tests.this);

	    			byte[] d = null;
                    v.clear();
	    			publishProgress("", "");
	    			buttonProcess(false, btnTest3);
	    		}
	    	});
	    	
	    	return null;
	    }

	    @Override
	    protected void onProgressUpdate(String... progress) {
	    	status.setText(progress[1]);
	    	status2.setText(progress[0]);
	    }
	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	class Test4consumeMemSlowly extends AsyncTask<Void, String, Void> {
	    @Override
	    protected Void doInBackground(Void... params) {
	    	consuming = true;
	    	start = System.currentTimeMillis();
	    	while((Integer.parseInt(infoRAM.getMemFree()) > ((prefs.getLong("Memory Threshold MB", 125)) * 1024)) && consuming && contRAM){
	    		try {
	    			byte[] d = new byte[(1024 * 1024)];
	    			v.add(d);
	    		} catch(OutOfMemoryError e){
	    			v.clear();
	    			byte[] d = null;
	    			break;
	    		}
	    		publishProgress("Free Memory: " + ((prefs.getString("data unit", "mb").equals("kb")) ? String.valueOf(formatter.format(Integer.parseInt(infoRAM.getMemFree()))) + " kB" : String.valueOf(Integer.parseInt(infoRAM.getMemFree()) / 1024) + " MB"), "Free Memory: " + String.valueOf(formatter.format(Integer.parseInt(infoRAM.getMemFree()) * 1024)) + " bytes");
	    		long t = System.currentTimeMillis();
	    		while((System.currentTimeMillis() - t) < 75){}
	    	}
	    	end = System.currentTimeMillis();
	    		
	    	Tests.this.runOnUiThread(new Runnable(){
	    		@Override
	    		public void run(){
	    			tools.MsgBox("Took " + String.valueOf(tools.round((end - start) / 1000.0, 3)) + " seconds to consume RAM without entering critical state.", "RAM Test #2", Tests.this);

	    			byte[] d = null;
	    			v.clear();
	    			publishProgress("", "");
	    			buttonProcess(false, btnTest4);
	    		}
	    	});
	    	
	    	return null;
	    }

	    @Override
	    protected void onProgressUpdate(String... progress) {
	    	status.setText(progress[1]);
	    	status2.setText(progress[0]);
	    }
	}
}
