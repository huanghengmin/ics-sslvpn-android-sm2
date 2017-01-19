package com.zd.vpn.util;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class APNManager {
    private static String TAG = "APNManager";
    private static final Uri APN_TABLE_URI = Uri.parse("content://telephony/carriers");// 所有的APN配配置信息位置
    private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");// 当前的APN
    private static String[] projection = {"_id", "apn", "type", "current", "proxy", "port"};

    //获取当前apn id
    public static String getCurApnId(Context context) {
        ContentResolver resolver = context.getContentResolver();
//        String[] projection = new String[]{"_id"};
        try {
            Cursor cur = resolver.query(PREFERRED_APN_URI, projection, null, null,
                    null);
            String apnId = null;
            if (cur != null && cur.moveToFirst()) {
                apnId = cur.getString(cur.getColumnIndex("_id"));
            }
            Log.i("xml", "getCurApnId:" + apnId);
            return apnId;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //获取当前apn 信息
    public static APN getCurApnInfo(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cur = resolver.query(PREFERRED_APN_URI, projection, null, null,
                null);
        APN apn = new APN();
        if (cur != null && cur.moveToFirst()) {
            apn.id = cur.getString(cur.getColumnIndex("_id"));
            apn.apn = cur.getString(cur.getColumnIndex("apn"));
            apn.type = cur.getString(cur.getColumnIndex("type"));

        }
        return apn;
    }

    //设置apn id
    public static void setCurApnId(Context context, String id) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("apn_id", id);
        resolver.update(PREFERRED_APN_URI, values, null, null);
        Log.d("xml", "setApn");
    }

    //获取apn id
    public static String getAPNIdByName(ContentResolver resolver, String apnName) {
        Cursor cursor = null;
//        String[] projection = new String[]{"_id"};
        cursor = resolver.query(APN_TABLE_URI, projection, " apn = ? and current = 1", new String[]{apnName.toLowerCase()}, null);
        String apnId = null;
        if (cursor != null && cursor.moveToFirst()) {
            apnId = cursor.getString(cursor.getColumnIndex("_id"));
        }
        cursor.close();
        if (apnId != null) {
            return apnId;
        } else {
            return null;
        }
    }

    //获取所有APN
    public static ArrayList<APN> getAPNList(final Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cr = contentResolver.query(APN_TABLE_URI, projection, null, null, null);
        ArrayList<APN> apnList = new ArrayList<APN>();
        if (cr != null && cr.moveToFirst()) {
            do {
                Log.d(TAG,
                        cr.getString(cr.getColumnIndex("_id")) + ";"
                                + cr.getString(cr.getColumnIndex("apn")) + ";"
                                + cr.getString(cr.getColumnIndex("type")) + ";"
                                + cr.getString(cr.getColumnIndex("current")) + ";"
                                + cr.getString(cr.getColumnIndex("proxy")));
                APN apn = new APN();
                apn.id = cr.getString(cr.getColumnIndex("_id"));
                apn.apn = cr.getString(cr.getColumnIndex("apn"));
                apn.type = cr.getString(cr.getColumnIndex("type"));
                apnList.add(apn);
            } while (cr.moveToNext());

            cr.close();
        }
        return apnList;
    }

    //获取可用的APN
    public static ArrayList<APN> getAvailableAPNList(final Context context) {
        // current不为空表示可以使用的APN
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cr = contentResolver.query(APN_TABLE_URI, projection,
                "current is not null", null, null);
        ArrayList<APN> apnList = new ArrayList<APN>();
        if (cr != null && cr.moveToFirst()) {
            do {
                Log.d(TAG,
                        cr.getString(cr.getColumnIndex("_id")) + ";"
                                + cr.getString(cr.getColumnIndex("apn")) + ";"
                                + cr.getString(cr.getColumnIndex("type")) + ";"
                                + cr.getString(cr.getColumnIndex("current")) + ";"
                                + cr.getString(cr.getColumnIndex("proxy")));
                APN apn = new APN();
                apn.id = cr.getString(cr.getColumnIndex("_id"));
                apn.apn = cr.getString(cr.getColumnIndex("apn"));
                apn.type = cr.getString(cr.getColumnIndex("type"));
                apnList.add(apn);
            } while (cr.moveToNext());

            cr.close();
        }
        return apnList;
    }

    //切换apn
    public static boolean convertVPN(ContentResolver resolver, String apnName) {
        String apnId = getAPNIdByName(resolver, apnName);
        if (apnId != null) {
            ContentValues values = new ContentValues();
            values.put("apn_id", apnId);
            resolver.update(PREFERRED_APN_URI, values, null, null);
            return true;
        } else {
            return false;
        }
    }

    //自定义APN包装类
    static class APN {
        String id;
        String apn;
        String type;

        public String toString() {
            return "id=" + id + ",apn=" + apn + ";type=" + type;
        }
    }
}