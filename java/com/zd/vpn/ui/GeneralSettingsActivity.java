package com.zd.vpn.ui;

import android.app.Activity;
import android.os.Bundle;
import com.zd.vpn.fragments.GeneralSettings;


/**
 * 查看证书
 */
public class GeneralSettingsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralSettings())
                .commit();

    }


}
