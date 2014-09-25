package io.github.rob__.performancer.storage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.rob__.performancer.R;
import io.github.rob__.performancer.Tools;
import io.github.rob__.performancer.storage.info.InfoPics;

public class PicsFragment extends Fragment {

	SharedPreferences prefs;

	final Handler handler = new Handler();
	InfoPics infoPics = new InfoPics();
	Tools tools = new Tools();

	Parcelable lvState;
	ListView lvPics;

	View v;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//prefs = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

		if(prefs.getString("theme", "col").equals("col")){
			// Matching Action Bars
			//setTheme(R.style.AppThemeColourful);
			v = inflater.inflate(R.layout.fragment_pics_colourful, container, false);
		} else {
			//setTheme(R.style.AppThemeMinimalistic);
			v = inflater.inflate(R.layout.fragment_pics_minimalistic, container, false);
		}

		infoPics.init(v.getContext());
		// No sliding menu - thanks to TabActivity
		// we can attach it directly

		lvPics = (ListView) v.findViewById(R.id.lvPics);

		handler.post(update);
		lvPics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object[] data = infoPics.sortImageData(v.getContext(), position);
				tools.MsgBox(Html.fromHtml(
								"<small>Name:</small><br>" +
										data[0] + "<br><br>" +
								"<small>Date Taken:</small><br>" +
										DateFormat.getDateTimeInstance().format(new Date(Long.parseLong((String) data[3]))) + "<br><br>" +
								(
									(!((Double.parseDouble((String) data[3])) < (Double.parseDouble((String) data[4])))) ? "" :
											"<small>Date Modified:</small><br>" + DateFormat.getDateTimeInstance().format(new Date(Long.parseLong((String) data[4]))) + "<br><br>"
								) +
								"<small>Resolution:</small><br>" +
										data[1] + "x" + data[2] + " (" + String.valueOf((Double.parseDouble((String) data[1]) * Double.parseDouble((String) data[2])) / 1024000.0) + " MP)<br><br>" +
								(
									(data[5] == null && data[6] == null) ? "" : "<small>Longitude, latitude:</small><br>" +	data[5] + ", " + data[6] + "<br><br>"
								) +
								(
									(getLocation((String) data[5], (String) data[6], v.getContext()) == null) ? "" : "<small>Location:</small><br>" + getLocation((String) data[5], (String) data[6], v.getContext()) + "<br><br>"
								) +
								"<small>Size:</small><br>" +
										infoPics.validateValue(Double.parseDouble((String) data[7]), false) + "<br><br>" +
								"<small>Directory:</small><br>" +
										data[8]
							),
							Html.fromHtml(
								(String) data[0]
							),
							v.getContext()
				);
			}
		});

		lvPics.setLongClickable(true);
		lvPics.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				List<String> paths = infoPics.getImageData(MediaStore.Images.Media.DATA, v.getContext());
				String path = paths.get(position);
				paths.clear();

				startActivity(new Intent().setAction(Intent.ACTION_VIEW).setDataAndType(Uri.parse(Uri.fromFile(new File(path)).toString()), "image/*"));
				return true;
			}
		});
		return v;
	}

	public String getLocation(String latitude, String longitude, Context context){
		try {
			List<Address> adds = new Geocoder(context, Locale.getDefault()).getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
			return (adds.get(0).getAddressLine(0) + ", " + adds.get(0).getAddressLine(1) + ", " + adds.get(0).getAddressLine(2) + ", " + adds.get(0).getCountryName()).replace(" Ln ", " Lane ").replace(" Ln,", " Lane,").replace(" N,", " North,").replace(" E,", " East,").replace(" S,", " South,").replace(" W,", " West,");
		} catch(IOException e){
			e.printStackTrace();
			return null;
		} catch(NullPointerException e){
			e.printStackTrace();
			return null;
		}
	}

	Runnable update = new Runnable(){
		@Override
		public void run(){
			lvState = lvPics.onSaveInstanceState();
			lvPics.setAdapter(infoPics.populateListView(v.getContext()));
			lvPics.onRestoreInstanceState(lvState);
		}
	};
	public static PicsFragment newInstance() {
		return new PicsFragment();
	}
}
