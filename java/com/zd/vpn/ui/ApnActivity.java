package com.zd.vpn.ui;



import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zd.vpn.R;
import com.zd.vpn.util.APNManager;
import com.zd.vpn.util.ToastUtils;


public class ApnActivity extends Activity implements View.OnClickListener{

    private EditText apn;
    private Button start;
    private Button close;
    private String currId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zd_layout_apn);
        apn =  (EditText)findViewById(R.id.apn);
        start =  (Button)findViewById(R.id.start);
        close =  (Button)findViewById(R.id.close);
        start.setOnClickListener(this);
        close.setOnClickListener(this);
//        currId = APNManager.getCurApnId(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                if(apn.getText()!=null) {
                    ContentResolver resolver = getContentResolver();
                    currId = APNManager.getCurApnId(this);
                    APNManager.convertVPN(resolver, apn.getText().toString());
                }else {
                    ToastUtils.showLong(this,"请配置APN名称");
                }
                break;
            case R.id.close:
                if(apn.getText()!=null) {
                    ContentResolver resolver = getContentResolver();
                    APNManager.convertVPN(resolver, currId);
                }else {
                    ToastUtils.showLong(this, "请配置APN名称");
                }

                break;
        }
    }
}
