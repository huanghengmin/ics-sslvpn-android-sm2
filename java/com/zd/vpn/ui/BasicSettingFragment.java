package com.zd.vpn.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;


public class BasicSettingFragment extends Fragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent intent = new Intent(getActivity(), BasicSettingActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        startActivity(intent);
        this.getActivity().finish();
    }


}
