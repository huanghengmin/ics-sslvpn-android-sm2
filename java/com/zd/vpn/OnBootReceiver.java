/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package com.zd.vpn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.zd.vpn.core.ProfileManager;
import com.zd.vpn.service.CheckUtils;
import com.zd.vpn.service.StrategyService;


public class OnBootReceiver extends BroadcastReceiver {

	// Debug: am broadcast -a android.intent.action.BOOT_COMPLETED
	@Override
	public void onReceive(Context context, Intent intent) {

		final String action = intent.getAction();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		boolean useStartOnBoot = prefs.getBoolean("restartvpnonboot", true);
		if (!useStartOnBoot)
			return;




		if(Intent.ACTION_BOOT_COMPLETED.equals(action) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {

			boolean b = CheckUtils.isServiceWorked(context, "com.zd.vpn.service.StrategyService");
			if (!b) {
				Intent service = new Intent(context, StrategyService.class);
				context.startService(service);
			}

			VpnProfile bootProfile = ProfileManager.getAlwaysOnVPN(context);
			if(bootProfile != null) {
				launchVPN(bootProfile, context);
			}		
		}
	}

	void launchVPN(VpnProfile profile, Context context) {
		Intent startVpnIntent = new Intent(Intent.ACTION_MAIN);
		startVpnIntent.setClass(context, LaunchVPN.class);
		startVpnIntent.putExtra(LaunchVPN.EXTRA_KEY,profile.getUUIDString());
		startVpnIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startVpnIntent.putExtra(LaunchVPN.EXTRA_HIDELOG, true);

		context.startActivity(startVpnIntent);
	}
}
