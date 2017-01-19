package com.zd.vpn.util;


import android.content.Context;

import com.zd.vpn.checker.PropertiesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 策略文件工具类
 * Created by yf on 2014-12-28.
 */
public class StrategyUtil {

    private JSONObject jsonObj = null;

    public StrategyUtil(Context paramContext) {
        loadData(paramContext);
    }

    private void loadData(Context paramContext) {
        File config = new File(paramContext.getFilesDir().getAbsolutePath() + PropertiesUtils.STRATEGY_PATH);
        String json = "";
        try {
            InputStream ins = new FileInputStream(config);
            BufferedReader in = new BufferedReader(new InputStreamReader(ins));
            String str = "";
            while (str != null) {
                json += str;
                str = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObj_ = new JSONObject(json);
            JSONArray jsonArray = jsonObj_.getJSONArray("root");
            if (jsonArray != null && jsonArray.length() > 0) {
                jsonObj = (JSONObject) jsonArray.get(0);
            }

        } catch (JSONException e) {
            return;
        }
    }

    public boolean getThreeyards() {
        try {
            if (jsonObj != null) {
                String ss = jsonObj.getString("threeyards");
                if (ss != null && "1".equals(ss)) {
                    return true;
                }
            } else {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }

    public boolean getWifi() {
        try {
            if (jsonObj != null) {
                String ss = jsonObj.getString("wifi");
                if (ss != null && "1".equals(ss)) {
                    return true;
                }
            } else {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }

    public boolean getBluetooth() {
        try {
            if (jsonObj != null) {
                String ss = jsonObj.getString("bluetooth");
                if (ss != null && "1".equals(ss)) {
                    return true;
                }
            } else {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }

    public int getStrategyInterval() {
        try {
            if (jsonObj != null) {
                String ss = jsonObj.getString("strategy_interval");
                if (ss != null && !"".equals(ss)) {
                    int sInt = Integer.parseInt(ss);
                    return sInt;
                }
            } else {
                return -1;
            }
        } catch (JSONException e) {
            return -1;
        }
        return -1;
    }
}
