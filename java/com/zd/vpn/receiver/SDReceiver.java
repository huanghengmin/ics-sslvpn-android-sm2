package com.zd.vpn.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.zd.vpn.core.OpenVPNManagement;
import com.zd.vpn.core.ProfileManager;

public class SDReceiver  extends BroadcastReceiver {

    private OpenVPNManagement mManagement;

    public SDReceiver(OpenVPNManagement mManagement) {
        super();
        this.mManagement = mManagement;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {
            if (mManagement != null) {
                ProfileManager.setConntectedVpnProfileDisconnected(context);
                mManagement.stopVPN(false);
            }
        } else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
            if (mManagement != null) {
                ProfileManager.setConntectedVpnProfileDisconnected(context);
                mManagement.stopVPN(false);
            }
        } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            if (mManagement != null) {
                ProfileManager.setConntectedVpnProfileDisconnected(context);
                mManagement.stopVPN(false);
            }
        } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
            if (mManagement != null) {
                ProfileManager.setConntectedVpnProfileDisconnected(context);
                mManagement.stopVPN(false);
            }
        } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
            if (mManagement != null) {
                ProfileManager.setConntectedVpnProfileDisconnected(context);
                mManagement.stopVPN(false);
            }
        }
    }
}
