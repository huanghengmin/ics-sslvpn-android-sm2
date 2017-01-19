/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package com.zd.vpn.activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4n.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.zd.vpn.fragments.AboutFragment;
import com.zd.vpn.fragments.FaqFragment;
import com.zd.vpn.fragments.GeneralSettings;
import com.zd.vpn.fragments.LogFragment;
import com.zd.vpn.fragments.SendDumpFragment;
import com.zd.vpn.fragments.VPNProfileList;
import com.zd.vpn.views.ScreenSlidePagerAdapter;
import com.zd.vpn.views.SlidingTabLayout;
import com.zd.vpn.views.TabBarView;


public class MainActivity extends BaseActivity {

    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;

    protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(com.zd.vpn.R.layout.main_activity);


        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(com.zd.vpn.R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager(), this);

        /* Toolbar and slider should have the same elevation */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            disableToolbarElevation();
        }



        mPagerAdapter.addTab(com.zd.vpn.R.string.vpn_list_title, VPNProfileList.class);

        mPagerAdapter.addTab(com.zd.vpn.R.string.generalsettings, GeneralSettings.class);
        mPagerAdapter.addTab(com.zd.vpn.R.string.faq, FaqFragment.class);

        if(SendDumpFragment.getLastestDump(this)!=null) {
            mPagerAdapter.addTab(com.zd.vpn.R.string.crashdump, SendDumpFragment.class);
        }

        if (isDirectToTV())
            mPagerAdapter.addTab(com.zd.vpn.R.string.openvpn_log, LogFragment.class);

        mPagerAdapter.addTab(com.zd.vpn.R.string.about, AboutFragment.class);
        mPager.setAdapter(mPagerAdapter);

        TabBarView tabs = (TabBarView) findViewById(com.zd.vpn.R.id.sliding_tabs);
        tabs.setViewPager(mPager);

       // requestDozeDisable();
	}

    @TargetApi(Build.VERSION_CODES.M)
    private void requestDozeDisable() {
        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(packageName))
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        else {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
        }
        startActivity(intent);
    }

    private static final String FEATURE_TELEVISION = "android.hardware.type.television";
    private static final String FEATURE_LEANBACK = "android.software.leanback";

    private boolean isDirectToTV() {
        return(getPackageManager().hasSystemFeature(FEATURE_TELEVISION)
                || getPackageManager().hasSystemFeature(FEATURE_LEANBACK));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void disableToolbarElevation() {
        ActionBar toolbar = getActionBar();
        toolbar.setElevation(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.zd.vpn.R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== com.zd.vpn.R.id.show_log){
            Intent showLog = new Intent(this, LogWindow.class);
            startActivity(showLog);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		System.out.println(data);


	}


}
