package com.zd.vpn.util;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * 获取手机相关信息，如imei和sim序列号等
 *
 * Created by yf on 2014-12-26.
 */
public class TelInfoUtil {
    String tel;
    String imei;
    String imsi;
    String sim;

    public TelInfoUtil(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
         imei = tm.getDeviceId();       //取出IMEI
         tel = tm.getLine1Number();     //取出MSISDN，很可能为空
         sim =tm.getSimSerialNumber();  //取出SIM
         imsi =tm.getSubscriberId();     //取出IMSI
    }

    public String getTel() {
        return tel;
    }

    public String getImei() {
        return imei;
    }

    public String getImsi() {
        return imsi;
    }

    public String getSim() {
        return sim;
    }
}
