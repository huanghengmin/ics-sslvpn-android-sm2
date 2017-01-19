package com.zd.vpn.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.zd.vpn.activities.VPNPreferences;
import com.zd.vpn.util.ConfigUtil;


public class Config extends Fragment  {
	private static final int START_VPN_CONFIG = 92;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		editVPN();
	}

    private void editVPN() {
    	
    	ConfigUtil configUtil = new ConfigUtil(getActivity());
		String uuid ="";
//		if(configUtil.getUuid() == null){
			uuid = configUtil.config(getActivity());
		/*}
		else{
			uuid = configUtil.getUuid();
		} */
    	Intent vprefintent = new Intent(getActivity(),VPNPreferences.class);
    	vprefintent.putExtra(getActivity().getPackageName() + ".profileUUID", uuid);
    	startActivity(vprefintent);
    	getActivity().finish();
	}

}
