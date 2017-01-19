package com.zd.vpn.ui;



import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zd.vpn.R;

public class AboutFragment extends Fragment  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	View v= inflater.inflate(R.layout.zd_about, container, false);
    	
    	return v;
    }

}
