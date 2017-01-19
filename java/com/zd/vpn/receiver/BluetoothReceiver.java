package com.zd.vpn.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zd.vpn.util.StrategyUtil;

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action  = intent.getAction();
        if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            StrategyUtil strategyUtil = new StrategyUtil(context);
            if (strategyUtil.getBluetooth()) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                boolean flag = bluetoothAdapter.isEnabled();
                if (flag) {
                    bluetoothAdapter.disable();
                }
            }
        }
    }
}
