package com.zd.vpn.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class ShowCertificationFragment extends PreferenceFragment {
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Intent intent = new Intent(getActivity(), ShowCertActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		startActivity(intent);
		this.getActivity().finish();
	}

}
