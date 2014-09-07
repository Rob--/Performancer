package io.github.rob__.performancer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import io.github.rob__.performancer.storage.*;

public class Storage extends FragmentActivity {

	ViewPager vpStorage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storage);

		vpStorage = (ViewPager) findViewById(R.id.vpStorage);
		vpStorage.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			switch(pos) {
				case 0: return Storage  .newInstance();
				case 1: return Apps     .newInstance();
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}
