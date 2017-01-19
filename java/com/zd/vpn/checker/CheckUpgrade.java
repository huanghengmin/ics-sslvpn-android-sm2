package com.zd.vpn.checker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.zd.vpn.R;
import com.zd.vpn.util.FileUtil;
import com.zd.vpn.util.ReturnObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CheckUpgrade {
    private String ip;
    private String strategyPort;
    private Handler handler;
    private Context context;

    public CheckUpgrade(Context context, String ip, String strategyPort) {
        handler = new Handler(context.getMainLooper());
        this.context = context;
        this.ip = ip;
        this.strategyPort = strategyPort;
    }

    public interface OnCheckListener {
        public void onCheckOk(String msg);

        public void onCheckErr(String msg);
    }

    public void check(final OnCheckListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String pathUrl = "http://" + ip + ":" + strategyPort + PropertiesUtils.CHECK_UPGRADE;
                HttpClient client = new HttpClient();
                client.getHttpConnectionManager().getParams().setConnectionTimeout(1 * 1000 * 60);
                client.getHttpConnectionManager().getParams().setSoTimeout(1 * 1000 * 60);
                PostMethod post = new PostMethod(pathUrl);
                post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 1 * 1000 * 60);
                post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                post.addParameter("os", "android");
                post.addParameter("version", context.getString(R.string.version));
                int statusCode = 0;
                try {
                    statusCode = client.executeMethod(post);
                } catch (IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCheckErr("检测版本出错");
                        }
                    });
                }
                if (statusCode == 200) {
                    //取出回应字串
                    try {
                        byte[] bytes = post.getResponseBody();
                        String data = null;
                        if(bytes!=null&&bytes.length>0) {
                            data = new String(bytes, "utf-8");
                            JSONObject result = new JSONObject(data);
                            boolean flag = result.getBoolean("flag");
                            if (flag) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onCheckOk("检测到有新版本，需要更新");
                                    }
                                });
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onCheckErr("未检测到有新版本，无需更新");
                                    }
                                });
                            }
                        }else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onCheckErr("返回数据为空");
                                }
                            });
                        }
                    } catch (Exception e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onCheckErr("检测版本返回数据出错");
                            }
                        });
                    }
                }else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCheckErr("检测版本返回状态码出错");
                        }
                    });
                }
            }
        }).start();
    }

    public void upgrade(final OnCheckListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String pathUrl = "http://" + ip + ":" + strategyPort + PropertiesUtils.ACTION_UPGRADE;
                HttpClient client = new HttpClient();
                client.getHttpConnectionManager().getParams().setConnectionTimeout(1 * 1000 * 60);
                client.getHttpConnectionManager().getParams().setSoTimeout(1 * 1000 * 60);
                PostMethod post = new PostMethod(pathUrl);
                post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 1 * 1000 * 60);
                post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                post.addParameter("os", "android");
                int statusCode = 0;
                try {
                    statusCode = client.executeMethod(post);
                } catch (IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCheckErr("请求服务器异常");
                        }
                    });
                }
                if (statusCode == 200) {
                    //取出回应字串
                    InputStream data = null;
                    try {
                        data = post.getResponseBodyAsStream();
                        File f = new File(Environment.getExternalStorageDirectory(), PropertiesUtils.APK_FILE);
                        File parentFile = f.getParentFile();
                        if(!parentFile.exists()){
                            parentFile.mkdirs();
                        }
                        FileUtil.save(data, f);
                        if (f.exists())
                            InstallApk(context, f);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onCheckErr("版本更新成功");
                            }
                        });
                    } catch (IOException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onCheckErr("请求服务器数据异常");
                            }
                        });
                    }
                }else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCheckErr("请求服务器返回状态码异常");
                        }
                    });
                }
            }
        }).start();
    }

    public ReturnObject check(){
        String pathUrl = "http://" + ip + ":" + strategyPort + PropertiesUtils.CHECK_UPGRADE;
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(1 * 1000 * 60);
        client.getHttpConnectionManager().getParams().setSoTimeout(1 * 1000 * 60);
        PostMethod post = new PostMethod(pathUrl);
        post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 1 * 1000 * 60);
        post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        post.addParameter("os", "android");
        post.addParameter("version", context.getString(R.string.version));
        int statusCode = 0;
        try {
            statusCode = client.executeMethod(post);
        } catch (IOException e) {
            ReturnObject entity = new ReturnObject();
            entity.setFlag(false);
            entity.setMsg("检测版本出错");
            return entity;
        }
        if (statusCode == 200) {
            //取出回应字串
            try {
                byte[] bytes = post.getResponseBody();
                String data = null;
                if(bytes!=null&&bytes.length>0) {
                    data = new String(bytes, "utf-8");
                    JSONObject result = new JSONObject(data);
                    boolean flag = result.getBoolean("flag");
                    if (flag) {
                        ReturnObject entity = new ReturnObject();
                        entity.setFlag(true);
                        entity.setMsg("检测到有新版本，需要更新");
                        return entity;
                    } else {
                        ReturnObject entity = new ReturnObject();
                        entity.setFlag(false);
                        entity.setMsg("未检测到有新版本，无需更新");
                        return entity;
                    }
                }else {
                    ReturnObject entity = new ReturnObject();
                    entity.setFlag(false);
                    entity.setMsg("检测版本返回数据为空");
                    return entity;
                }
            } catch (Exception e) {
                ReturnObject entity = new ReturnObject();
                entity.setFlag(false);
                entity.setMsg("检测版本返回数据出错");
                return entity;
            }
        }else {
            ReturnObject entity = new ReturnObject();
            entity.setFlag(false);
            entity.setMsg("检测版本返回状态码出错");
            return entity;
        }
    }

    public boolean upgrade(){
        String pathUrl = "http://" + ip + ":" + strategyPort + PropertiesUtils.ACTION_UPGRADE;
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(1 * 1000 * 60);
        client.getHttpConnectionManager().getParams().setSoTimeout(1 * 1000 * 60);
        PostMethod post = new PostMethod(pathUrl);
        post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 1 * 1000 * 60);
        post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        post.addParameter("os", "android");
        int statusCode = 0;
        try {
            statusCode = client.executeMethod(post);
        } catch (IOException e) {
            return false;
        }
        if (statusCode == 200) {
            //取出回应字串
            InputStream data = null;
            try {
                data = post.getResponseBodyAsStream();
                File f = new File(Environment.getExternalStorageDirectory(), PropertiesUtils.APK_FILE);
                File parentFile = f.getParentFile();
                if(!parentFile.exists()){
                    parentFile.mkdirs();
                }
                FileUtil.save(data, f);
                if (f.exists())
                    InstallApk(context, f);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
       return  false;
    }

    private void InstallApk(Context context,File file) {
        Log.e("InstallApk", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}



