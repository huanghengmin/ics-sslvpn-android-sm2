
package com.zd.vpn.checker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.zd.vpn.util.ConfigFileUtil;
import com.zd.vpn.util.FileUtil;
import com.zd.vpn.util.PostRequest;
import com.zd.vpn.util.ReturnObject;
import com.zd.vpn.util.TFJniUtils;
import com.zd.vpn.util.md5.MD5Utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONObject;
import org.spongycastle.util.encoders.Base64;

public class CheckStatusSignCRLValidity {
    private Handler handler;

    public CheckStatusSignCRLValidity(Context context) {
        handler = new Handler(context.getMainLooper());
    }

    public CheckStatusSignCRLValidity() {
    }

    public interface CheckListener {

        public void onCheckOk(String msg);

        public void onCheckErr(String msg);

    }

    public void check(
            final Context paramContext,
            final String ip,
            final String poli_port,
            final String mServerPort,
            final CheckListener listener) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                SharedPreferences sPreferences = paramContext.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
                boolean read = sPreferences.getBoolean("vpn.read", false);
                if (!read) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCheckErr("请先进行基本配置");
                        }
                    });
                } else {
                    String save_path = paramContext.getFilesDir().getAbsolutePath();
                    String down_config_url = "http://" + ip + ":" + poli_port + PropertiesUtils.DOWN_CONFIG;
//                    String down_strategy_url = "http://" + ip + ":" + poli_port + PropertiesUtils.DOWN_STRATEGY;
//                    SecuTFHelper helper = new SecuTFHelper();
//                    X509Certificate x509Certificate = helper.getCrt(paramContext);
                    TFJniUtils tfJniUtils = new TFJniUtils();
//                    X509Certificate x509Certificate = tfJniUtils.getCrt(paramContext);
                    String cert = tfJniUtils.getCrt(paramContext);
                    if (cert != null) {
                        String config_file_md5 = "";
                        String ca_file_md5 = "";
                        String cert_file_md5 = "";
                        String key_file_md5 = "";
                        try {
                            File config_file = new File(save_path + PropertiesUtils.CONFIG_PATH);
                            File ca_file = new File(save_path + PropertiesUtils.CA_PATH);
                            File cert_file = new File(save_path + PropertiesUtils.CERT_PATH);
                            File key_file = new File(save_path + PropertiesUtils.KEY_PATH);
                            if (config_file.exists() && config_file.length() > 0 && ca_file.exists() && ca_file.length() > 0 && cert_file.exists() && cert_file.length() > 0 && key_file.exists() && key_file.length() > 0) {
                                ca_file_md5 = MD5Utils.getMd5ByFile(ca_file);
                                cert_file_md5 = MD5Utils.getMd5ByFile(cert_file);
                                key_file_md5 = MD5Utils.getMd5ByFile(key_file);
                            }
                            HttpClient client = new HttpClient();
                            client.getHttpConnectionManager().getParams().setConnectionTimeout(1 * 1000 * 60);
                            client.getHttpConnectionManager().getParams().setSoTimeout(1 * 1000 * 60);
                            PostMethod post = new PostMethod(down_config_url);
                            post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 1 * 1000 * 60);
                            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                            post.addParameter("osType", "Android");
                            post.addParameter("certificate", /*new String(Base64.encode(x509Certificate.getEncoded()))*/cert);
                            post.addParameter("ca_file_md5", ca_file_md5);
                            post.addParameter("cert_file_md5", cert_file_md5);
                            post.addParameter("key_file_md5", key_file_md5);
                            int statusCode = 0;
                            try {
                                statusCode = client.executeMethod(post);
                            } catch (IOException e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onCheckOk("请求服务器出错！");
                                    }
                                });
                            }
                            if (statusCode == 200) {
                                //取出回应字串
                                byte[] bytes = post.getResponseBody();
                                String data = null;
                                if(bytes!=null&&bytes.length>0)
                                    data = new String(bytes, "utf-8");
                                JSONObject result = new JSONObject(data);
                                boolean flag = result.getBoolean("success");
                                if (flag) {
                                   /* byte[] strategy_data = PostRequest.getData(down_strategy_url);
                                    if (strategy_data != null)
                                        FileUtil.copy(strategy_data, save_path + PropertiesUtils.STRATEGY_PATH);*/
                                    boolean compare = result.getBoolean("compare");
                                    if (compare) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onCheckOk("策略检测正常！");
                                            }
                                        });
                                    } else {
                                        String key = result.getString("key");
                                        String key_md5 = result.getString("key_md5");
                                        String crt = result.getString("crt");
                                        String crt_md5 = result.getString("crt_md5");
                                        String ca = result.getString("ca");
                                        String ca_md5 = result.getString("ca_md5");
                                        String config = result.getString("config");
                                        String config_md5 = result.getString("config_md5");

                                        FileUtil.copy(Base64.decode(ca.getBytes()), save_path + PropertiesUtils.CA_PATH);
                                        FileUtil.copy(Base64.decode(config.getBytes()), save_path + PropertiesUtils.CONFIG_PATH);
                                        FileUtil.copy(Base64.decode(crt.getBytes()), save_path + PropertiesUtils.CERT_PATH);
                                        FileUtil.copy(Base64.decode(key.getBytes()), save_path + PropertiesUtils.KEY_PATH);

                                        config_file_md5 = MD5Utils.getMd5ByFile(config_file);
                                        ca_file_md5 = MD5Utils.getMd5ByFile(ca_file);
                                        cert_file_md5 = MD5Utils.getMd5ByFile(cert_file);
                                        key_file_md5 = MD5Utils.getMd5ByFile(key_file);

                                        if (config_file_md5.equals(config_md5) && ca_file_md5.equals(ca_md5) && cert_file_md5.equals(crt_md5) && key_file_md5.equals(key_md5)) {
                                            if (config_file.exists() && config_file.length() > 0) {
                                                byte[] data1 = PostRequest.readInstream(new FileInputStream(config_file));
                                                ConfigFileUtil.update(ip, mServerPort, data1, save_path + PropertiesUtils.CONFIG_PATH);
                                            }
                                        } else {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.onCheckErr("下载策略异常！");
                                                }
                                            });
                                        }

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onCheckOk("下载策略完成！");
                                            }
                                        });
                                    }
                                } else {
                                    final String msg = result.getString("msg");
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onCheckErr(msg);
                                        }
                                    });
                                }
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onCheckErr("检测版本返回状态码出错");
                                    }
                                });
                            }
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onCheckOk("请求服务器出错！");
                                }
                            });
                        }
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onCheckErr("证书信息读取失败！");
                            }
                        });
                    }
                }
            }
        }).start();
    }


    public ReturnObject check(
            final Context paramContext,
            final String ip,
            final String poli_port,
            final String mServerPort) {

        SharedPreferences sPreferences = paramContext.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
        boolean read = sPreferences.getBoolean("vpn.read", false);
        if (!read) {
            String msg = "请先进行基本配置";
            return new ReturnObject(false, msg);
        } else {
            String save_path = paramContext.getFilesDir().getAbsolutePath();
            String down_config_url = "http://" + ip + ":" + poli_port + PropertiesUtils.DOWN_CONFIG;
//            String down_strategy_url = "http://" + ip + ":" + poli_port + PropertiesUtils.DOWN_STRATEGY;
//            SecuTFHelper helper = new SecuTFHelper();
//            X509Certificate x509Certificate = helper.getCrt(paramContext);

            TFJniUtils tfJniUtils = new TFJniUtils();
//            X509Certificate x509Certificate = tfJniUtils.getCrt(paramContext);
            String cert = tfJniUtils.getCrt(paramContext);

            if (cert != null) {
                String config_file_md5 = "";
                String ca_file_md5 = "";
                String cert_file_md5 = "";
                String key_file_md5 = "";
                try {
                    File config_file = new File(save_path + PropertiesUtils.CONFIG_PATH);
                    File ca_file = new File(save_path + PropertiesUtils.CA_PATH);
                    File cert_file = new File(save_path + PropertiesUtils.CERT_PATH);
                    File key_file = new File(save_path + PropertiesUtils.KEY_PATH);
                    if (config_file.exists() && config_file.length() > 0 && ca_file.exists() && ca_file.length() > 0 && cert_file.exists() && cert_file.length() > 0 && key_file.exists() && key_file.length() > 0) {
                        ca_file_md5 = MD5Utils.getMd5ByFile(ca_file);
                        cert_file_md5 = MD5Utils.getMd5ByFile(cert_file);
                        key_file_md5 = MD5Utils.getMd5ByFile(key_file);
                    }
                    HttpClient client = new HttpClient();
                    client.getHttpConnectionManager().getParams().setConnectionTimeout(1 * 1000 * 60);
                    client.getHttpConnectionManager().getParams().setSoTimeout(1 * 1000 * 60);
                    PostMethod post = new PostMethod(down_config_url);
                    post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 1 * 1000 * 60);
                    post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                    post.addParameter("osType", "Android");
                    post.addParameter("certificate", cert/*new String(Base64.encode(x509Certificate.getEncoded()))*/);
                    post.addParameter("ca_file_md5", ca_file_md5);
                    post.addParameter("cert_file_md5", cert_file_md5);
                    post.addParameter("key_file_md5", key_file_md5);
                    int statusCode = 0;
                    try {
                        statusCode = client.executeMethod(post);
                    } catch (IOException e) {
                        return new ReturnObject(false, "请求服务器出错！");
                    }
                    if (statusCode == 200) {
                        //取出回应字串
                        byte[] bytes = post.getResponseBody();
                        String data = null;
                        if(bytes!=null&&bytes.length>0)
                            data = new String(bytes, "utf-8");
                        JSONObject result = new JSONObject(data);
                        boolean flag = result.getBoolean("success");
                        if (flag) {
                       /*     byte[] strategy_data = PostRequest.getData(down_strategy_url);
                            if (strategy_data != null)
                                FileUtil.copy(strategy_data, save_path + PropertiesUtils.STRATEGY_PATH);*/
                            boolean compare = result.getBoolean("compare");
                            if (compare) {
                                return new ReturnObject(true, "策略检测正常！");
                            } else {
                                String key = result.getString("key");
                                String key_md5 = result.getString("key_md5");
                                String crt = result.getString("crt");
                                String crt_md5 = result.getString("crt_md5");
                                String ca = result.getString("ca");
                                String ca_md5 = result.getString("ca_md5");
                                String config = result.getString("config");
                                String config_md5 = result.getString("config_md5");

                                FileUtil.copy(Base64.decode(ca.getBytes()), save_path + PropertiesUtils.CA_PATH);
                                FileUtil.copy(Base64.decode(config.getBytes()), save_path + PropertiesUtils.CONFIG_PATH);
                                FileUtil.copy(Base64.decode(crt.getBytes()), save_path + PropertiesUtils.CERT_PATH);
                                FileUtil.copy(Base64.decode(key.getBytes()), save_path + PropertiesUtils.KEY_PATH);

                                config_file_md5 = MD5Utils.getMd5ByFile(config_file);
                                ca_file_md5 = MD5Utils.getMd5ByFile(ca_file);
                                cert_file_md5 = MD5Utils.getMd5ByFile(cert_file);
                                key_file_md5 = MD5Utils.getMd5ByFile(key_file);

                                if (config_file_md5.equals(config_md5) && ca_file_md5.equals(ca_md5) && cert_file_md5.equals(crt_md5) && key_file_md5.equals(key_md5)) {
                                    if (config_file.exists() && config_file.length() > 0) {
                                        byte[] data1 = PostRequest.readInstream(new FileInputStream(config_file));
                                        ConfigFileUtil.update(ip, mServerPort, data1, save_path + PropertiesUtils.CONFIG_PATH);
                                    }
                                } else {
                                    return new ReturnObject(false, "下载策略异常！");
                                }
                                return new ReturnObject(true, "下载策略完成！");
                            }
                        } else {
                            String msg = result.getString("msg");
                            return new ReturnObject(false, msg);
                        }
                    } else {
                        return new ReturnObject(false, "检测版本返回状态码出错");
                    }
                } catch (Exception e) {
                    return new ReturnObject(false, "请求服务器出错");
                }
            } else {
                return new ReturnObject(false, "证书信息读取失败");
            }
        }
    }
}
