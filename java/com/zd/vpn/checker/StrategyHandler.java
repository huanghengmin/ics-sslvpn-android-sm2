package com.zd.vpn.checker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.zd.vpn.R;
import com.zd.vpn.receiver.BluetoothReceiver;
import com.zd.vpn.receiver.WifiReceiver;
import com.zd.vpn.util.FileUtil;
import com.zd.vpn.util.PostRequest;
import com.zd.vpn.util.ReturnObject;
import com.zd.vpn.util.StrategyUtil;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class StrategyHandler {
    private String ip;
    private String strategyPort;
    private Handler handler;
    private Context context;


    public StrategyHandler(Context context, String ip, String strategyPort) {
        handler = new Handler(context.getMainLooper());
        this.context = context;
        this.ip = ip;
        this.strategyPort = strategyPort;
    }

    public interface OnStrategyUpdateListener {
        public void onUpdateOk(String msg);

        public void onUpdateErr(String msg);
    }

    public void check(final OnStrategyUpdateListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String save_path = context.getFilesDir().getAbsolutePath();
                String down_strategy_url = "http://" + ip + ":" + strategyPort + PropertiesUtils.DOWN_STRATEGY;
                //取出回应字串
                try {
                    byte[] strategy_data = PostRequest.getData(down_strategy_url);
                    if (strategy_data != null)
                        FileUtil.copy(strategy_data, save_path + PropertiesUtils.STRATEGY_PATH);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onUpdateOk("保存策略成功");
                        }
                    });
                }catch (Exception e){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onUpdateErr("下载策略异常");
                        }
                    });
                }
            }
        }).start();
    }

}



