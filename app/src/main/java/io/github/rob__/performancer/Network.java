package io.github.rob__.performancer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import io.github.rob__.performancer.info.InfoNetwork;


public class Network extends Activity {

	SharedPreferences prefs;

	ListView lvNetwork;

	Parcelable state;
	Handler handler = new Handler();
	AlertDialog adi;
	String assid;

	Tools tools = new Tools();
	InfoNetwork infoNetwork = new InfoNetwork();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//prefs = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(prefs.getString("theme", "col").equals("col")){
			// Matching Action Bars
			//setTheme(R.style.AppThemeColourful);
			setContentView(R.layout.activity_network_colourful);
		} else {
			//setTheme(R.style.AppThemeMinimalistic);
			setContentView(R.layout.activity_network_minimalistic);
		}

		infoNetwork.init(this);
		tools       .addMenu(getApplicationContext(), this);
		tools       .populateMenu((ListView) findViewById(R.id.menuItems), this, this, "Network");
		tools       .toggle();

		//Loads all the layout items for use.
		lvNetwork = (ListView) findViewById(R.id.lvNetwork);

		updateStart();
		lvNetwork.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String t = lvNetwork.getItemAtPosition(position).toString().split(":")[0];
				String t_ = lvNetwork.getItemAtPosition(position).toString();

				if(t.contains("WiFi State")){
					tools.MsgBox("The current state of the device's WiFi connection.", "WiFi Network", Network.this);
				} else if(t.contains("IP Address") && !t.contains("\u200B")){
					tools.MsgBox("The IP address assigned from the router to the device.", "IP Address", Network.this);
				} else if(t.contains("Link Speed") && !t.contains("\u200B")){
					tools.MsgBox("The maximum speed of the established WiFi connection.", "Link Speed", Network.this);
				} else if(t.contains("Saved networks") && !t.contains("\u200B")){
					tools.MsgBox("A list of saved WiFi networks on the device.", "Saved Networks", Network.this);
				} else if(t.contains("Nearby networks") && !t.contains("\u200B")){
					tools.MsgBox("A list of WiFi networks found in the area.", "Nearby Networks", Network.this);
				} else if(t_.contains("% > ") && !t.contains("\u200B")){
					tools.MsgBox(
							Html.fromHtml("A WiFi connection found nearby.<br><br><small>Name:</small><br>" + t_.split("% > ")[1] + "<br><br><small>Strength:</small><br>" + t_.split("% > ")[0].replace(" ", "") + "%"),
							Html.fromHtml(t_.split("% > ")[1]),
							Network.this
					);
				} else if(t.contains("Number") && !t.contains("\u200B")){
					tools.MsgBox("The mobile number of the connected SIM.", "Mobile Number", Network.this);
				} else if(t.contains("Network") && !t.contains("\u200B")) {
					tools.MsgBox(
							Html.fromHtml("The connected carrier and network type.<br><br><small>Carrier:</small><br>" + t_.split("Network: ")[1].split(" ")[0] + "<br><br><small>Network Type:</small><br>" + t_.split("Network: ")[1].split(" ")[1].replace("(", "").replace(")", "")),
							Html.fromHtml("Mobile"),
							Network.this
					);
				} else if(t.contains("Mobile State") && !t.contains("\u200B")){
					tools.MsgBox("The current state of the device's mobile connection.", "Network State", Network.this);
				} else if(t.contains("Roaming") && !t.contains("\u200B")){
					tools.MsgBox("An indication to whether the device is roaming.", "Roaming", Network.this);
				} else if(t.contains("Strength") && !t.contains("\u200B")){
					tools.MsgBox("The strength of the connected carrier.", "Strength", Network.this);
				} else if((lvNetwork.getCount() - position) == 1){
					tools.MsgBox("The amount of data recieved from the mobile network (since boot).", "Mobile", Network.this);
				} else if((lvNetwork.getCount() - position) == 2){
					tools.MsgBox("The amount of data sent to the mobile network (since boot).", "Mobile", Network.this);
				} else if((lvNetwork.getCount() - position) == 4){
					tools.MsgBox("The amount of data recieved from WiFi networks (since boot).", "WiFi", Network.this);
				} else if((lvNetwork.getCount() - position) == 5){
					tools.MsgBox("The amount of data sent to WiFi networks (since boot).", "WiFi", Network.this);
				}
			}
		});
	}

	Runnable update = new Runnable() {
		@Override
		public void run() {
			state = lvNetwork  .onSaveInstanceState();
			lvNetwork.setAdapter(infoNetwork.populateListView(Network.this));
			lvNetwork          .onRestoreInstanceState(state);
			handler            .postDelayed(this, prefs.getInt("delay", 1000) + 500);
	                                                    /* we don't need it updating too often             */
	                                                        /*   might use some extra battery but we're updating */
	                                                            /*        the wifi, so it's worth it. Right?           */
		}
	};

	public void updateStart(){
		handler.post(update);
	}

	public void updateStop(){
		handler.removeCallbacks(update);
	}

	@Override
	public void onStop(){
		updateStop();
		super.onStop();
	}

	@Override
	public void onStart(){
		updateStart();
		super.onStart();
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
