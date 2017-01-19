package com.zd.vpn.alarm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.util.Log;

import java.util.Date;

public class AlarmSchedulingService extends IntentService {

    public AlarmSchedulingService() {
        super("SchedulingService");
    }

    public static final String TAG = "Scheduling Service";

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sPreferences = getApplicationContext().getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
        String ip = sPreferences.getString("vpn.ip",null);
        String port = sPreferences.getString("vpn.port",null);
        if (ip != null) {
            boolean result = ConnectUtils.connect(ip, Integer.parseInt(port));
            if (!result) {
                Log.e(TAG,"connect vpn server fail "+new Date());
            }
        }
        AlarmReceiver.completeWakefulIntent(intent);
    }
}
