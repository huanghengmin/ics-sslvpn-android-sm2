package com.zd.vpn.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.zd.vpn.util.Sender;
//import com.zd.vpn.util.TFInfo;

import com.zd.vpn.LaunchVPN;
import com.zd.vpn.VpnProfile;
import com.zd.vpn.activities.ConfigConverter;
import com.zd.vpn.activities.FileSelect;
import com.zd.vpn.core.ProfileManager;
import com.zd.vpn.util.ConfigUtil;
import com.zd.vpn.util.TelInfoUtil;

public class VPNStart extends Fragment {

    final static int RESULT_VPN_DELETED = Activity.RESULT_FIRST_USER;

    private static final int START_VPN_CONFIG = 92;
    private static final int SELECT_PROFILE = 43;
    private static final int IMPORT_PROFILE = 231;

    private ArrayAdapter<VpnProfile> mArrayadapter;

    protected VpnProfile mEditProfile = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private ProfileManager getPM() {
        return ProfileManager.getInstance(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_VPN_DELETED) {
            if (mArrayadapter != null && mEditProfile != null)
                mArrayadapter.remove(mEditProfile);
        }

        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == START_VPN_CONFIG) {
            String configuredVPN = data.getStringExtra(VpnProfile.EXTRA_PROFILEUUID);
            VpnProfile profile = ProfileManager.get(this.getActivity(), configuredVPN);
            getPM().saveProfile(getActivity(), profile);
            // Name could be modified, reset List adapter
            // setListAdapter();

        } else if (requestCode == SELECT_PROFILE) {
            String filedata = data.getStringExtra(FileSelect.RESULT_DATA);
            Intent startImport = new Intent(getActivity(), ConfigConverter.class);
            startImport.setAction(ConfigConverter.IMPORT_PROFILE);
            Uri uri = new Uri.Builder().path(filedata).scheme("file").build();
            startImport.setData(uri);
            startActivityForResult(startImport, IMPORT_PROFILE);
        } else if (requestCode == IMPORT_PROFILE) {
            String profileUUID = data .getStringExtra(VpnProfile.EXTRA_PROFILEUUID);
            mArrayadapter.add(ProfileManager.get(this.getActivity(), profileUUID));
        }

    }

    /*
     * private void startVPN(VpnProfile profile) {
     *
     * getPM().saveProfile(getActivity(), profile);
     *
     * Intent intent = new Intent(getActivity(),LaunchVPN.class);
     * intent.putExtra(LaunchVPN.EXTRA_KEY, profile.getUUID().toString());
     * intent.setAction(Intent.ACTION_MAIN); startActivity(intent);
     *
     * getActivity().finish(); }
     */

}
