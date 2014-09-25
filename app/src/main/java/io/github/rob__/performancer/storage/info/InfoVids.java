package io.github.rob__.performancer.storage.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.github.rob__.performancer.R;

public class InfoVids {

	ArrayAdapter<String>    adapter;
	List<String>            info;

	SharedPreferences prefs;
	public void init(Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public List<String> getVideoData(String MediaStoreData, Context context){
		Cursor c = context.getContentResolver().query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStoreData },
				MediaStore.Video.Media.BUCKET_ID + " = ?",
				new String[] { String.valueOf((Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera").toLowerCase().hashCode()) },
				null);

		List<String> info = new ArrayList<>(c.getCount());
		if (c.moveToFirst()) {
			while(c.moveToNext()){
				info.add(c.getString(c.getColumnIndexOrThrow(MediaStoreData)));
			}
		}
		c.close();
		return info;
	}

	/**
	 * @return Object[]. Display name, width, height, date taken, date modified, latitude, longitude, size.
	 */
	public Object[] sortVideoData(Context context, int index){
		return new Object[]{
			getVideoData(MediaStore.Video.Media.DISPLAY_NAME    , context).get(index),
			getVideoData(MediaStore.Video.Media.DATE_TAKEN      , context).get(index),
			getVideoData(MediaStore.Video.Media.DATE_MODIFIED   , context).get(index),
			getVideoData(MediaStore.Video.Media.LATITUDE        , context).get(index),
			getVideoData(MediaStore.Video.Media.LONGITUDE       , context).get(index),
			getVideoData(MediaStore.Video.Media.SIZE            , context).get(index),
			getVideoData(MediaStore.Video.Media.DATA            , context).get(index),
			getVideoData(MediaStore.Video.Media.RESOLUTION      , context).get(index),
			getVideoData(MediaStore.Video.Media.DURATION        , context).get(index)
		};
	}

	public ArrayAdapter<String> populateListView(Context context){
		adapter = null;
		info = new ArrayList<>();

		for(String name : getVideoData(MediaStore.Video.Media.DISPLAY_NAME, context)){
			info.add(name);
		}

		adapter = new ArrayAdapter<>(context, (prefs.getString("theme", "col").equals("col")) ? R.layout.listview_layout_colourful : R.layout.listview_layout_minimalistic, info);
		return adapter;
	}

	DecimalFormat d5 = new DecimalFormat("###,###,###.#####");
	DecimalFormat d2 = new DecimalFormat("###,###,###.##");

	/**
	 * @param value - value to validate
	 * @param t - use two decimal places? true -> 2 dp; false -> 5 dp
	 * @return Validated value (string).
	 */
	public String validateValue(double value, boolean t){
		value /= 1024;
		if(value < 1024){
			return t ? d2.format(value)                 + " KB" : d5.format(value)                  + " KB";
		} else if(value < (1024 * 1024)){
			return t ? d2.format(value / 1024)          + " MB" : d5.format(value / 1024)           + " MB";
		} else {
			return t ? d2.format(value / (1024 * 1024)) + " GB" : d5.format(value / (1024 * 1024))  + " GB";
		}
	}
}
