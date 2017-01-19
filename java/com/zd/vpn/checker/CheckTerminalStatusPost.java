package com.zd.vpn.checker;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.zd.vpn.util.ReturnObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * 三码接口绑定提交接口
 * <p/>
 * Created by yf on 2014-12-26.
 */
public class CheckTerminalStatusPost {
    private Handler handler;
    private String ip;
    private String port;
    private String imei;
    private String sim;
    private String sn;
    private Context context;

    public CheckTerminalStatusPost(Context context, String ip, String port, String imei, String sim, String sn) {
        handler = new Handler(context.getMainLooper());
        this.context = context;
        this.ip = ip;
        this.port = port;
        this.imei = imei;
        this.sim = sim;
        this.sn = sn;
    }

    public CheckTerminalStatusPost() {
    }

    public interface OnThreeYardsPostListener {
        public void onThreeYardsPostOk(String msg);

        public void onThreeYardsPostErr(String msg);
    }

    public void postData( final OnThreeYardsPostListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                    String[][] params = new String[][]{
                            {"serial", sn},
                            {"simId", sim},
                            {"terminalId", imei}
                    };
                    String pathUrl = "http://" + ip + ":" + port + PropertiesUtils.CHECK_TERMINAL;
                    HttpClient client = new HttpClient();
                    client.getHttpConnectionManager().getParams().setConnectionTimeout(1 * 1000 * 60);
                    client.getHttpConnectionManager().getParams().setSoTimeout(1 * 1000 * 60);
                    PostMethod post = new PostMethod(pathUrl);
                    post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 1 * 1000 * 60);
                    post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                    for (String[] param : params) {
                        post.addParameter(param[0], param[1]);
                    }
                    int statusCode = 0;
                    try {
                        statusCode = client.executeMethod(post);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (statusCode == 200) {
                        //取出回应字串
                        String data = null;
                        try {
                            byte[] bytes = post.getResponseBody();
                            if(bytes!=null&&bytes.length>0)
                            data = new String(bytes, "utf-8");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        boolean flag = false;
                        String msg = "";
                        try {
                            JSONObject result = new JSONObject(data);//转换为JSONObject
                            flag = result.getBoolean("success");
                            msg = result.getString("msg");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (flag) {
                            final String finalCode = msg;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onThreeYardsPostOk(finalCode);
                                }
                            });
                        } else {
                            if (!"".equals(msg) && msg != null) {
                                final String finalData = msg;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onThreeYardsPostErr(finalData);
                                    }
                                });
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String msg = "客户端绑定信息校验失败";
                                        listener.onThreeYardsPostErr(msg);
                                    }
                                });
                            }
                        }
                    } else {
                        Log.v("vpn", "Error Response" + statusCode);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String msg = "客户端绑定信息校验失败";
                                listener.onThreeYardsPostErr(String.valueOf(msg));
                            }
                        });
                    }
                }
        }).start();
    }

    public ReturnObject postData() {
        String[][] params = new String[][]{
                {"serial", sn},
                {"simId", sim},
                {"terminalId", imei}
        };
        String pathUrl = "http://" + ip + ":" + port + PropertiesUtils.CHECK_TERMINAL;
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(1 * 1000 * 60);
        client.getHttpConnectionManager().getParams().setSoTimeout(1 * 1000 * 60);
        PostMethod post = new PostMethod(pathUrl);
        post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 1 * 1000 * 60);
        post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        for (String[] param : params) {
            post.addParameter(param[0], param[1]);
        }
        int statusCode = 0;
        try {
            statusCode = client.executeMethod(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (statusCode == 200) {
            //取出回应字串
            String data = null;
            try {
                byte[] bytes = post.getResponseBody();
                if(bytes!=null&&bytes.length>0)
                    data = new String(bytes, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            boolean flag = false;
            String msg = "";
            try {
                JSONObject result = new JSONObject(data);//转换为JSONObject
                flag = result.getBoolean("success");
                msg = result.getString("msg");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (flag) {
                return new ReturnObject(true, msg);
            } else {
                if (!"".equals(msg) && msg != null) {
                    return new ReturnObject(false, msg);
                } else {
                    return new ReturnObject(false, "客户端绑定信息校验失败");
                }
            }
        } else {
            Log.v("vpn", "Error Response" + statusCode);
            return new ReturnObject(false, "客户端绑定信息校验失败");
        }
    }
}
