// http://stackoverflow.com/questions/18413309/how-to-implement-a-viewpager-with-different-fragments-layouts
// http://stackoverflow.com/users/1590502/philipp-jahoda
//

package io.github.rob__.performancer;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.astuetz.PagerSlidingTabStrip;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import io.github.rob__.performancer.storage.AppsFragment;
import io.github.rob__.performancer.storage.PicsFragment;
import io.github.rob__.performancer.storage.StorageFragment;
import io.github.rob__.performancer.storage.VidsFragment;

public class Storage extends FragmentActivity {

	Tools tools = new Tools();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storage);

		tools   .addMenu(getApplicationContext(), Storage.this);
		tools   .populateMenu((ListView) findViewById(R.id.menuItems), this, this, "Storage");
		tools   .menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		tools   .toggle();

		((ViewPager)            findViewById(R.id.vpStorage )).setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
		((PagerSlidingTabStrip) findViewById(R.id.psts      )).setViewPager((ViewPager) findViewById(R.id.vpStorage));

		PagerSlidingTabStrip psts = (PagerSlidingTabStrip) findViewById(R.id.psts);

		if(PreferenceManager.getDefaultSharedPreferences(Storage.this).getString("theme", "col").equals("col")){
			psts.setBackgroundColor(getResources().getColor(R.color.navy_blue_light));
			psts.setIndicatorColor(getResources().getColor(R.color.blue_light));
			psts.setDividerColor(getResources().getColor(R.color.navy_blue));
			psts.setTextColor(Color.parseColor("#ffffff"));
		}
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {
		Fragment[] fragments   = new Fragment[]{
				StorageFragment .newInstance(),
				AppsFragment    .newInstance(),
				PicsFragment    .newInstance(),
				VidsFragment    .newInstance()
		};

		String[]    titles      = new String[]                      { "Storage", "Applications", "Pictures", "Videos" };
		public ViewPagerAdapter(FragmentManager fm)                 { super(fm); }
		@Override public Fragment getItem(int pos)                  { return fragments[pos]; }
		@Override public int getCount()                             { return titles.length; }
		@Override public CharSequence getPageTitle(int position)    { return titles[position]; }

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
