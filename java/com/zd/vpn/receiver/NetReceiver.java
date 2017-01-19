package com.zd.vpn.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;

import com.zd.vpn.LaunchVPN;
import com.zd.vpn.R;
import com.zd.vpn.checker.CheckStatusSignCRLValidity;
import com.zd.vpn.checker.CheckTerminalStatusPost;
import com.zd.vpn.checker.CheckUpgrade;
import com.zd.vpn.core.VpnStatus;
import com.zd.vpn.ui.MoreActivity;
import com.zd.vpn.util.ConfigUtil;
import com.zd.vpn.util.Sender;
import com.zd.vpn.util.StrategyUtil;
import com.zd.vpn.util.TelInfoUtil;

/**
 * Created by Administrator on 15-12-22.
 */
public class NetReceiver extends BroadcastReceiver implements VpnStatus.StateListener{
    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = 2;
    private boolean isConnected = true;

    private void sendNotification(String msg,Context context) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MoreActivity.class), 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(context.getString(R.string.netReceiver_title))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void cancelNotification(Context context) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            //网络已连接
            final SharedPreferences sPreferences = context.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
            boolean read = sPreferences.getBoolean("vpn.read", false);
            if (read) {
                if(!isConnected) {
                    // 下载证书文件
                    sendNotification("正在检测版本信息，请稍候...", context);
                    final String ip = sPreferences.getString("vpn.ip", "");
                    final String port = sPreferences.getString("vpn.port", "");
                    final String poliPort = sPreferences.getString("vpn.poliPort", "");
                    final CheckUpgrade checkUpgrade = new CheckUpgrade(context, ip, poliPort);
                    final Context ctx = context;
                    //检测版本更新
                    checkUpgrade.check(new CheckUpgrade.OnCheckListener() {
                        //需要更新
                        @Override
                        public void onCheckOk(String msg) {
                            //更新
                            checkUpgrade.upgrade(new CheckUpgrade.OnCheckListener() {
                                //更新成功
                                @Override
                                public void onCheckOk(String msg) {
                                    sendNotification(msg, ctx);
                                }

                                //更新失败
                                @Override
                                public void onCheckErr(String msg) {
                                    sendNotification(msg, ctx);
                                }
                            });
                        }

                        //无需更新
                        @Override
                        public void onCheckErr(String msg) {
                            sendNotification("正在检测签名信息，请稍候...", ctx);
                            CheckStatusSignCRLValidity check = new CheckStatusSignCRLValidity(ctx);
                            check.check(ctx, ip, poliPort, port, new CheckStatusSignCRLValidity.CheckListener() {
                                @Override
                                public void onCheckOk(String msg) {
                                    sendNotification("正在检测客户端绑定信息，请稍候...", ctx);
                                    StrategyUtil strategyUtil = new StrategyUtil(ctx);
                                    if (strategyUtil.getThreeyards()) {
                                        String serialnumber = sPreferences.getString("vpn.serialNumber", "");
                                        String ip = sPreferences.getString("vpn.ip", "");
                                        String port = sPreferences.getString("vpn.poliPort", "");
                                        TelInfoUtil telInfo = new TelInfoUtil(ctx);
                                        CheckTerminalStatusPost tPost = new CheckTerminalStatusPost(ctx, ip, port, telInfo.getImei(), telInfo.getSim(), serialnumber);
                                        tPost.postData(new CheckTerminalStatusPost.OnThreeYardsPostListener() {
                                            @Override
                                            public void onThreeYardsPostOk(String msg) {
                                                cancelNotification(ctx);
                                                ConfigUtil configUtil = new ConfigUtil(ctx);
                                                String uuid = configUtil.config(ctx);
                                                Intent intent = new Intent(ctx, LaunchVPN.class);
                                                intent.putExtra(LaunchVPN.EXTRA_KEY, uuid);
                                                intent.setAction(Intent.ACTION_MAIN);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                ctx.startActivity(intent);
                                            }

                                            @Override
                                            public void onThreeYardsPostErr(String msg) {
                                                if (msg != null && !"".equals(msg)) {
                                                    Sender sender = new Sender(ctx);
                                                    sender.sendTF(msg);
                                                }
                                            }
                                        });
                                    } else {
                                        cancelNotification(ctx);
                                        ConfigUtil configUtil = new ConfigUtil(ctx);
                                        String uuid = configUtil.config(ctx);
                                        Intent intent = new Intent(ctx, LaunchVPN.class);
                                        intent.putExtra(LaunchVPN.EXTRA_KEY, uuid);
                                        intent.setAction(Intent.ACTION_MAIN);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        ctx.startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCheckErr(String msg) {
                                    sendNotification(msg, ctx);
                                }
                            });
                        }
                    });
                }else {
                    sendNotification("链路已建立成功", context);
                }
            } else {
                Sender sender = new Sender(context);
                sender.sendTF("TF卡驱动加载失败请重试插拔TF卡或PIN码有误请检查PIN码设置");
            }
        }
    }

    @Override
    public void updateState(String state, String logmessage, int localizedResId, VpnStatus.ConnectionStatus level) {
         if (level == VpnStatus.ConnectionStatus.UNKNOWN_LEVEL || level == VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED||level==VpnStatus.ConnectionStatus.LEVEL_NONETWORK) {
            isConnected = false;
        }
    }
}
