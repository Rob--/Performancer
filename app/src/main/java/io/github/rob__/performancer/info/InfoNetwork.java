
package io.github.rob__.performancer.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.ArrayAdapter;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.github.rob__.performancer.R;

public class InfoNetwork extends PhoneStateListener{

	WifiManager wm;
	WifiInfo wi;
	List<WifiConfiguration> configs;
	List<ScanResult> results;
	String ssid;
	NetworkInfo.State w_;

	TelephonyManager tm;
	ConnectivityManager cm;
	NetworkInfo.State m_;
	String type;
	long tsBytes;
	long trBytes;
	long mrBytes;
	long msBytes;
	String ss = "";

	DecimalFormat d5 = new DecimalFormat("###,###,###.#####");
	DecimalFormat d2 = new DecimalFormat("###,###,###.##");

	ArrayAdapter<String>    adapter;
	List<String>            info;

	SharedPreferences prefs;
	public void init(Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(isFeatureAvailable(context, PackageManager.FEATURE_WIFI)) {
			wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			wi = wm.getConnectionInfo();
		}
		if(isPermissionAvailable(context, "android.permission.READ_PHONE_STATE")){
			tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		}
		if(isPermissionAvailable(context, "android.permission.ACCESS_NETWORK_STATE")){
			cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			tm.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		}
	}

	public ArrayAdapter<String> populateListView(Context context){
		adapter = null;
		info = new ArrayList<>();

		/*
			WiFi
		 */
		if(isFeatureAvailable(context, PackageManager.FEATURE_WIFI)) {
			if(wi.getSSID() != null) {
				ssid = (wi.getSSID().equals("<unknown ssid>") ? "" : wi.getSSID());
			}
			w_ = cm.getNetworkInfo(1).getState();
			info.add("WiFi State: " + validateNetworkState(w_) + (((!ssid.equals("") && !validateNetworkState(w_).equals("Disconnected"))) ? " (" + prefs.getString(ssid, ssid).replace("\"", "") + ")" : ""));
			if(!(wi.getIpAddress() < 5)) {
				info.add("IP Address: " + formatIP(wi.getIpAddress()));
			}
			if(wi.getLinkSpeed() != -1) {
				info.add("Link Speed: " + String.valueOf(wi.getLinkSpeed()) + " mbps");
			}
			configs = wm.getConfiguredNetworks();
			if(configs != null) {
				info.add("Saved networks:");
				for (WifiConfiguration c : configs) {
					info.add("    " + c.toString().split("\"")[1]);
				}
			}
			results = wm.getScanResults();
			List<String> added = new ArrayList<>();
			if(results.size() != 0) {
				info.add("Nearby networks:");
				for (ScanResult result : results) {
					if(!added.contains(result.SSID)) {
						info.add("    " + String.valueOf(wm.calculateSignalLevel(result.level, 100)) + "% > " + result.SSID);
						added.add(result.SSID);
					}
					/* the added list stops duplicates. */
						 /* yes, this is necessary, for some */
							/* reason getScanResults() returns dupes. */
				}
			}
		}

		/*
			Networks
		 */
		if(isFeatureAvailable(context, PackageManager.FEATURE_TELEPHONY)){
			info.add("--");
			if(tm.getLine1Number().equals("")) {
				info.add("Number: " + tm.getLine1Number());
			}
			switch(tm.getNetworkType()){
				case TelephonyManager.NETWORK_TYPE_1xRTT:
					type = "1xRTT";
					break;
				case TelephonyManager.NETWORK_TYPE_CDMA:
					type = "CDMA";
					break;
				case TelephonyManager.NETWORK_TYPE_EDGE:
					type = "EDGE";
					break;
				case TelephonyManager.NETWORK_TYPE_EHRPD:
					type = "EHRPD";
					break;
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
					type = "EVDO 0";
					break;
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
					type = "EVDO A";
					break;
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
					type = "EVDO B";
					break;
				case TelephonyManager.NETWORK_TYPE_GPRS:
					type = "GPRS";
					break;
				case TelephonyManager.NETWORK_TYPE_HSDPA:
					type = "HSDPA";
					break;
				case TelephonyManager.NETWORK_TYPE_HSPA:
					type = "HSPA";
					break;
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					type = "HSPAP";
					break;
				case TelephonyManager.NETWORK_TYPE_HSUPA:
					type = "HSUPA";
					break;
				case TelephonyManager.NETWORK_TYPE_IDEN:
					type = "IDEN";
					break;
				case TelephonyManager.NETWORK_TYPE_LTE:
					type = "LTE";
					break;
				case TelephonyManager.NETWORK_TYPE_UMTS:
					type = "UMTS";
					break;
			}
			if(!tm.getNetworkOperatorName().toString().equals("")) {
				info.add("Network: " + tm.getNetworkOperatorName() + " (" + type + ")");
			}
		}
		if(isPermissionAvailable(context, "android.permission.ACCESS_NETWORK_STATE")){
			m_ = cm.getNetworkInfo(0).getState();
			String s = validateNetworkState(m_);
			info.add("Mobile State: " + s);
			if(!s.equals("Disconnected") || !s.equals("Disconnecting")){
				if(!tm.getLine1Number().equals("")) {
					info.add("Roaming: " + String.valueOf(tm.isNetworkRoaming()).toUpperCase());
				}
			}
		}
		if(!(Double.parseDouble(ss) > 100)){
			info.add("Strength: " + ss + "%");
		}

		trBytes = TrafficStats.getTotalRxBytes();
		tsBytes = TrafficStats.getTotalTxBytes();
		mrBytes = TrafficStats.getMobileRxBytes();
		msBytes = TrafficStats.getMobileTxBytes();

		// Only check one.
		// If one works, they all do.
		if(trBytes != TrafficStats.UNSUPPORTED){
			info.add("--");
			if(!prefs.getBoolean("advanced", false)) {
				info.add("WiFi:");
				info.add("    Sent: "        + validateValue((tsBytes - msBytes), true));
				info.add("    Recieved: "    + validateValue((trBytes - mrBytes), true));
				info.add("Mobile:");
				info.add("    Sent: "      + validateValue(msBytes, true));
				info.add("    Recieved: "  + validateValue(mrBytes, true));
			} else {
				info.add("WiFi:");
				info.add("    Sent: "        + d2.format((tsBytes - msBytes) / 1024.0) + " KB");
				info.add("    Recieved: "    + d2.format((trBytes - mrBytes) / 1024.0) + " KB");
				info.add("Mobile:");
				info.add("    Sent: "      + d2.format(msBytes / 1024.0) + " KB");
				info.add("    Recieved: "  + d2.format(mrBytes / 1024.0) + " KB");
			}
		}

		adapter = new ArrayAdapter<>(context, (prefs.getString("theme", "col").equals("col")) ? R.layout.listview_layout_colourful : R.layout.listview_layout_minimalistic, info);
		return adapter;
	}

	/**
	 * Mobile/WiFi status.
	 */
	public String validateNetworkState(NetworkInfo.State s){
		if(s == NetworkInfo.State.CONNECTED){
			return "Connected";
		} else if(s == NetworkInfo.State.CONNECTING){
			return "Connecting";
		} else if(s == NetworkInfo.State.DISCONNECTED){
			return "Disconnected";
		} else if(s == NetworkInfo.State.DISCONNECTING){
			return "Disconnecting";
		} else if(s == NetworkInfo.State.SUSPENDED){
			return "Suspended";
		} else if(s == NetworkInfo.State.UNKNOWN){
			return "Unknown";
		} else {
			return "Unknown";
		}
	}

	/**
	 * Checks for feature.
	 */
	public boolean isFeatureAvailable(Context context, String feature){
		return (context.getPackageManager().hasSystemFeature(feature));
	}

	/**
	 * Checks for permission.
	 */
	public boolean isPermissionAvailable(Context context, String permission){
		return (context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
	}

	/**
	 * Formats IP Address
	 * http://stackoverflow.com/users/2721824/digital-rounin
	 * http://stackoverflow.com/questions/16730711/get-my-wifi-ip-address-android
	 */
	public String formatIP(int ip){
		if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
			ip = Integer.reverseBytes(ip);
		}
		try {
			return InetAddress.getByAddress(BigInteger.valueOf(ip).toByteArray()).getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	/**
	 * @param value - value to validate
	 * @param t - use two decimal places? true -> 2 dp; false -> 5 dp
	 * @return Validated value (string).
	 */
	public String validateValue(long value, boolean t){
		value /= 1024.0;
		if(value < 1024.0){
			return t ? d2.format(value)                 + " KB" : d5.format(value)                  + " KB";
		} else if(value < (1024.0 * 1024.0)){
			return t ? d2.format(value / 1024.0)          + " MB" : d5.format(value / 1024.0)           + " MB";
		} else {
			return t ? d2.format(value / (1024.0 * 1024.0)) + " GB" : d5.format(value / (1024.0 * 1024.0))  + " GB";
		}
	}

	@Override
	public void onSignalStrengthsChanged(SignalStrength _ss) {
		super.onSignalStrengthsChanged(_ss);
		ss = d2.format((_ss.getGsmSignalStrength() / 31.0) * 100);
	}
}
