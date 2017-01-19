package com.zd.vpn.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.zd.vpn.receiver.BluetoothReceiver;
import com.zd.vpn.receiver.WifiReceiver;

import java.util.Timer;

public class StrategyService extends Service {
    private Timer timer = new Timer();
    private WifiReceiver wifiReceiver = new WifiReceiver();
    private BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    synchronized void registerWifiReceiver() {
        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        wifiReceiver = new WifiReceiver();
        //注册receiver
        registerReceiver(wifiReceiver, wifiFilter);
    }

    synchronized void registerBluetoothReceiver() {
        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        bluetoothReceiver = new BluetoothReceiver();
        //注册receiver
        registerReceiver(bluetoothReceiver, bluetoothFilter);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerWifiReceiver();
        registerBluetoothReceiver();
        timer.schedule(new StrategyWork(this), 0, 1000 * 60 * 2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if(wifiReceiver!=null){
            unregisterReceiver(wifiReceiver);
        }
        if(bluetoothReceiver!=null){
            unregisterReceiver(bluetoothReceiver);
        }
    }
}