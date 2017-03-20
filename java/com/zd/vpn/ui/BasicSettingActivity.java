package com.zd.vpn.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.zd.vpn.R;
import com.zd.vpn.checker.StrategyHandler;
import com.zd.vpn.service.CheckUtils;
import com.zd.vpn.service.StrategyService;
import com.zd.vpn.util.TFJniUtils;
import com.zd.vpn.util.ToastUtils;

public class BasicSettingActivity extends Activity implements OnClickListener{
    private TextView mServerAddress = null;
    private TextView mServerPort = null;
    private ToggleButton mTcpUdp = null;
    private EditText mKeyPassword = null;
    private EditText mPoliPort = null;
    private EditText mCertContainerName = null;
    private Button basicSettingsSubmitBut = null;
    private Button basicSettingsBackBut = null;
    private SharedPreferences shPreferences = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zd_basic_settings);
        mServerAddress = (TextView) this.findViewById(R.id.address);
        mServerPort = (TextView) this.findViewById(R.id.port);
        mTcpUdp = (ToggleButton) this.findViewById(R.id.tcpudp);
        mKeyPassword = (EditText) this.findViewById(R.id.ping);
        mPoliPort = (EditText) this.findViewById(R.id.poli_port);
        mCertContainerName =  (EditText) this.findViewById(R.id.cert_container_name);

        this.basicSettingsBackBut = (Button) this.findViewById(R.id.basic_settings__back_but);
        this.basicSettingsSubmitBut = (Button) this.findViewById(R.id.basic_settings__submit_but);

        this.basicSettingsBackBut.setOnClickListener(this);
        this.basicSettingsSubmitBut.setOnClickListener(this);

        shPreferences = this.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
        mServerAddress.setText(shPreferences.getString("vpn.ip", "172.16.10.213"));
//        mServerAddress.setText(shPreferences.getString("vpn.ip", "222.46.20.174"));
        mServerPort.setText(shPreferences.getString("vpn.port", "1194"));
        mTcpUdp.setChecked(shPreferences.getBoolean("vpn.tcpUdp", false));
        mKeyPassword.setText(shPreferences.getString("vpn.pin", "111111"));
        mPoliPort.setText(shPreferences.getString("vpn.poliPort", "80"));
//        mPoliPort.setText(shPreferences.getString("vpn.poliPort", "12080"));
        mCertContainerName.setText(shPreferences.getString("vpn.certContainerName", "KingTrustVPN"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.basic_settings__back_but:
                this.finish();
                break;
            case R.id.basic_settings__submit_but:
                // 判断网络
                /*if (!NetUtil.isNetworkConnected(this)) {
                    Toast.makeText(this, "请先设置网络连接！", Toast.LENGTH_LONG).show();
                    Intent intent =  new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                    return;
                }*/
                String serverAddress = mServerAddress.getText().toString();
                String serverPort = mServerPort.getText().toString();
                String pinCode = mKeyPassword.getText().toString();
                String certContainerName =mCertContainerName.getText().toString();
                if (serverAddress == null || serverAddress.isEmpty()|| serverPort == null || serverPort.isEmpty()|| pinCode == null || pinCode.isEmpty()|| certContainerName == null || certContainerName.isEmpty()) {
                    Toast.makeText(this, R.string.info_incomplete_text, Toast.LENGTH_SHORT).show();
                }


                savePreferences(this);
//                ToastUtils.show(BasicSettingActivity.this, "保存配置成功");
                StrategyHandler strategyHandler =  new StrategyHandler(this,mServerAddress.getText().toString(),mPoliPort.getText().toString());
                strategyHandler.check(new StrategyHandler.OnStrategyUpdateListener() {
                    @Override
                    public void onUpdateOk(String msg) {
                        ToastUtils.show(getApplicationContext(), "保存配置成功");
                        boolean b = CheckUtils.isServiceWorked(BasicSettingActivity.this, "com.zd.vpn.service.StrategyService");
                        if (!b) {
                            Intent service = new Intent(BasicSettingActivity.this, StrategyService.class);
                            startService(service);
                        }
                    }

                    @Override
                    public void onUpdateErr(String msg) {
                        ToastUtils.show(getApplicationContext(), "配置下载异常");
                    }
                });
                this.finish();
                break;
            default:
                break;
        }
    }


    private void savePreferences(Context context) {
        Editor editor = shPreferences.edit();
        editor.putString("vpn.ip", mServerAddress.getText().toString());
        editor.putString("vpn.port", mServerPort.getText().toString());
        editor.putString("vpn.pin", mKeyPassword.getText().toString());
        editor.putBoolean("vpn.tcpUdp", mTcpUdp.isChecked());
        editor.putString("vpn.poliPort", mPoliPort.getText().toString());
        editor.putString("vpn.certContainerName", mCertContainerName.getText().toString());
        editor.commit();
//        SecuTFHelper helper = new SecuTFHelper();
//        helper.loadCrt(getApplication());

        TFJniUtils tfJniUtils = new TFJniUtils();
        tfJniUtils.loadCrt(context);
    }
}
