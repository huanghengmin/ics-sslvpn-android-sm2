package com.zd.vpn.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.zd.vpn.LaunchVPN;
import com.zd.vpn.R;
import com.zd.vpn.checker.CheckUpgrade;
import com.zd.vpn.core.OpenVPNService;
import com.zd.vpn.core.OpenVpnManagementThread;
import com.zd.vpn.core.ProfileManager;
import com.zd.vpn.core.VpnStatus;
import com.zd.vpn.checker.CheckStatusSignCRLValidity;
import com.zd.vpn.service.CheckUtils;
import com.zd.vpn.service.StrategyService;
import com.zd.vpn.util.ConfigUtil;
import com.zd.vpn.util.NetUtil;
import com.zd.vpn.util.Sender;
import com.zd.vpn.util.StrategyUtil;
import com.zd.vpn.util.TelInfoUtil;
import com.zd.vpn.checker.CheckTerminalStatusPost;
import com.zd.vpn.util.ToastUtils;



/**
 * 入口
 */
public class MoreActivity extends Activity implements OnClickListener, VpnStatus.StateListener {

    protected OpenVPNService mService;

    Button start;
    Button setting;
    //    Button setting_advanced;
    Button cert;
    //    Button config;
    Button about;
    Button exit;

    SharedPreferences shPreferences;
    private boolean logout = false;
    private Display mDisplay;
    private DisplayMetrics mDisplayMetrics;
    private Matrix mDisplayMatrix;
    private WindowManager mWindowManager;
    public InfoReceiver receiver;

    private boolean register = false;

//    public static boolean init = false;

    private boolean isConnected = false;//是否已经连接VPN

//    private AlarmReceiver alarmReceiver = new AlarmReceiver();

    // hsc's code
    // the state values set below is for the problem that when exit the show
    // certification fragment,it will show "再按一次退出客户端"
    public static final int INT_SHOW_CERTIFICATION_FRAGMENT_CREATE = 0;
    public static final int INT_SHOW_CERTIFICATION_FRAGMENT_PAUSE = 1;
    public static final int INT_SHOW_CERTIFICATION_FRAGMENT_DESTROY = 2;
    public static final int INT_SHOW_CERTIFICATION_FRAGMENT_ATTACH = 3;

    // the first time to show the certification
//    public static boolean whether_first_time_show_certification_fragment = true;

    public static int int_show_certification_fragment_state = INT_SHOW_CERTIFICATION_FRAGMENT_DESTROY;
    private Handler handler = new Handler();

    private ServiceConnection mConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            OpenVPNService.LocalBinder binder = (OpenVPNService.LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zd_main);
        start = (Button) findViewById(R.id.start);
        setting = (Button) findViewById(R.id.setting);
//        setting_advanced = (Button)findViewById(R.id.setting_advanced);
        cert = (Button) findViewById(R.id.cert);
//        config = (Button)findViewById(R.id.config);
        about = (Button) findViewById(R.id.about);
        exit = (Button) findViewById(R.id.exit);

        start.setOnClickListener(this);
        setting.setOnClickListener(this);
//        setting_advanced.setOnClickListener(this);
        cert.setOnClickListener(this);
//        config.setOnClickListener(this);
        about.setOnClickListener(this);
        exit.setOnClickListener(this);

        receiver = new InfoReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TFINFO_BROADCAST");
        registerReceiver(receiver, filter);
        register = true;
        shPreferences = getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
        getXy(shPreferences);
        // 初始化工作
        if (shPreferences.getString("vpn.ip", "").length() == 0) {
            ToastUtils.show(this.getApplicationContext(), "请先初始化基本配置！");
        }
        VpnStatus.addStateListener(this);

        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

//        alarmReceiver.setAlarm(this);

        boolean b = CheckUtils.isServiceWorked(this, "com.zd.vpn.service.StrategyService");
        if (!b) {
            Intent service = new Intent(this, StrategyService.class);
            startService(service);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        try {
            super.onConfigurationChanged(newConfig);
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // land
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                // port
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void onClick(View v) {
        final SharedPreferences sPreferences = getApplicationContext().getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
        boolean read = sPreferences.getBoolean("vpn.read", false);
        //判断各类条件
        if (v == start || v == cert /*|| v == config*/) {
            if (shPreferences.getString("vpn.ip", "").length() == 0) {
                ToastUtils.show(this.getApplicationContext(), "请先初始化基本配置！");
                return;
            } else if (v == cert && !read) {
                ToastUtils.show(this.getApplicationContext(), "正在初始化TF卡，请稍后查看！");
                return;
            } else if (v == start && !read) {
                // 判断网络
                if (!NetUtil.isNetworkConnected(this)) {
                    Toast.makeText(this, "请先设置网络连接！", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                    return;
                }
            }
        }

        //点击事件
        if (v == start) {
            if (isConnected) {
                //停止VPN
                AlertDialog alertDialog = new AlertDialog.Builder(this).
                        setTitle("停止VPN？").
                        setMessage("确定停止VPN吗？").
                        setIcon(R.drawable.icon).
                        setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                ProfileManager.setConntectedVpnProfileDisconnected(getApplicationContext());
                                if (mService != null && mService.getManagement() != null)
                                    mService.getManagement().stopVPN(false);
                            }
                        }).
                        setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        }).
                        create();
                alertDialog.show();
            } else {
                if (read) {
                    // 下载证书文件
                    showProgressDialog("正在检测版本信息，请稍候...");
                    final String ip = sPreferences.getString("vpn.ip", "");
                    final String port = sPreferences.getString("vpn.port", "");
                    final String poliPort = sPreferences.getString("vpn.poliPort", "");
                    final CheckUpgrade checkUpgrade = new CheckUpgrade(getApplicationContext(), ip, poliPort);
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
                                    hideProgressDialog();
                                    ToastUtils.showLong(getApplicationContext(), msg);
                                }

                                //更新失败
                                @Override
                                public void onCheckErr(String msg) {
                                    hideProgressDialog();
                                    ToastUtils.showLong(getApplicationContext(), msg);
                                }
                            });
                        }

                        //无需更新
                        @Override
                        public void onCheckErr(String msg) {
                            showProgressMsg("正在检测签名信息，请稍候...");
                            CheckStatusSignCRLValidity check = new CheckStatusSignCRLValidity(getApplicationContext());
                            check.check(getApplicationContext(), ip, poliPort, port, new CheckStatusSignCRLValidity.CheckListener() {
                                @Override
                                public void onCheckOk(String msg) {
                                    showProgressMsg("正在检测客户端绑定信息，请稍候...");
                                    StrategyUtil strategyUtil = new StrategyUtil(getApplicationContext());
                                    if (strategyUtil.getThreeyards()) {
                                        String serialnumber = shPreferences.getString("vpn.serialNumber", "");
                                        String ip = shPreferences.getString("vpn.ip", "");
                                        String port = shPreferences.getString("vpn.poliPort", "");
                                        TelInfoUtil telInfo = new TelInfoUtil(getApplicationContext());
                                        CheckTerminalStatusPost tPost = new CheckTerminalStatusPost(getApplicationContext(), ip, port, telInfo.getImei(), telInfo.getSim(), serialnumber);
                                        tPost.postData(new CheckTerminalStatusPost.OnThreeYardsPostListener() {
                                            @Override
                                            public void onThreeYardsPostOk(String msg) {
                                                hideProgressDialog();
                                                ConfigUtil configUtil = new ConfigUtil(getApplicationContext());
                                                String uuid = configUtil.config(getApplicationContext());
                                                Intent intent = new Intent(getApplicationContext(), LaunchVPN.class);
                                                intent.putExtra(LaunchVPN.EXTRA_KEY, uuid);
                                                intent.setAction(Intent.ACTION_MAIN);
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onThreeYardsPostErr(String msg) {
                                                hideProgressDialog();
                                                if (msg != null && !"".equals(msg)) {
                                                    Sender sender = new Sender(getApplicationContext());
                                                    sender.sendTF(msg);
                                                }
                                            }
                                        });
                                    } else {
                                        hideProgressDialog();
                                        ConfigUtil configUtil = new ConfigUtil(getApplicationContext());
                                        String uuid = configUtil.config(getApplicationContext());
                                        Intent intent = new Intent(getApplicationContext(), LaunchVPN.class);
                                        intent.putExtra(LaunchVPN.EXTRA_KEY, uuid);
                                        intent.setAction(Intent.ACTION_MAIN);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCheckErr(String msg) {
                                    hideProgressDialog();
                                    ToastUtils.showLong(getApplicationContext(), msg);
                                }
                            });
                        }
                    });
                } else {
                    Sender sender = new Sender(getApplicationContext());
                    sender.sendTF("TF卡驱动加载失败请重试插拔TF卡或PIN码有误请检查PIN码设置");
                }
            }
        } else if (v == setting) {
            Intent intent = new Intent(getApplicationContext(), BasicSettingActivity.class);
            startActivity(intent);
        }/*else if (v == setting_advanced) {
            Intent intent = new Intent(getApplicationContext(), GeneralSettingsActivity.class);
            startActivity(intent);
        }*/ else if (v == cert) {
            Intent intent = new Intent(getApplicationContext(), ShowCertActivity.class);
            startActivity(intent);
        }/* else if (v == config) {
            ConfigUtil configUtil = new ConfigUtil(getApplicationContext());
            String uuid = "";
            if (configUtil.getUuid() == null) {
                uuid = configUtil.config(getApplicationContext());
            } else {
                uuid = configUtil.getUuid();
            }
            Intent vprefintent = new Intent(getApplicationContext(), VPNPreferences.class);
            vprefintent.putExtra(getApplicationContext().getPackageName() + ".profileUUID", uuid);
            startActivity(vprefintent);
        }*/ else if (v == about) {
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
//            Intent intent = new Intent(getApplicationContext(), ApnActivity.class);
            startActivity(intent);
        } else if (v == exit) {

            //停止VPN
            AlertDialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("退出VPN？").
                    setMessage("确定退出吗？").
                    setIcon(R.drawable.icon).
                    setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            ProfileManager.setConntectedVpnProfileDisconnected(getApplicationContext());
                            if (mService != null && mService.getManagement() != null)
                                mService.getManagement().stopVPN(false);
                            finish();
                            System.exit(0);
                        }
                    }).
                    setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                        }
                    }).
                    create();
            alertDialog.show();
        }
    }

    private ProgressDialog mDialog;// 网络加载

    public void showProgressDialog(String msg) {
        // 如果已经存在进度条并且该进度条处于显示状态，将取消该进度条的显示。
        if (mDialog != null && mDialog.isShowing()) {
            hideProgressDialog();
        }

        mDialog = new ProgressDialog(this);

        mDialog.setTitle(msg);
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public void showProgressMsg(String msg) {
        // 如果已经存在进度条并且该进度条处于显示状态，将取消该进度条的显示。
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.setTitle(msg);
        }
    }

    public void hideProgressDialog() {
        try {
            mDialog.dismiss();
            mDialog = null;
        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void getXy(SharedPreferences shPreferences) {
        String sdk = Build.VERSION.SDK;
        int SDK = Integer.parseInt(sdk);
        if (SDK >= 15) {
            mDisplayMatrix = new Matrix();
            mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            mDisplay = mWindowManager.getDefaultDisplay();
            mDisplayMetrics = new DisplayMetrics();
            mDisplay.getRealMetrics(mDisplayMetrics);
            mDisplay.getRealMetrics(mDisplayMetrics);
            float[] dims = {mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels};
            float degrees = getDegreesForRotation(mDisplay.getRotation());
            boolean requiresRotation = (degrees > 0);
            if (requiresRotation) {
                // Get the dimensions of the device in its native orientation
                mDisplayMatrix.reset();
                mDisplayMatrix.preRotate(-degrees);
                mDisplayMatrix.mapPoints(dims);
                dims[0] = Math.abs(dims[0]);
                dims[1] = Math.abs(dims[1]);
                shPreferences.edit().putFloat("vpn.width", dims[0]).commit();
                shPreferences.edit().putFloat("vpn.height", dims[1]).commit();
            }
        }
    }

    private float getDegreesForRotation(int value) {
        switch (value) {
            case Surface.ROTATION_90:
                return 360f - 90f;
            case Surface.ROTATION_180:
                return 360f - 180f;
            case Surface.ROTATION_270:
                return 360f - 270f;
        }
        return 0f;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (register) {
            unregisterReceiver(receiver);
            register = false;
        }
        unbindService(mConnection);
//        alarmReceiver.cancelAlarm();
    }

    // 按两次退出
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0
                && int_show_certification_fragment_state == INT_SHOW_CERTIFICATION_FRAGMENT_DESTROY) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (logout == false) {
                    logout = true;
                    Toast.makeText(this, "再按一次退出客户端", Toast.LENGTH_SHORT).show();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            logout = false;
                        }
                    };
                    new Timer().schedule(task, 2000);
                } else {
                    finish();
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0
                && !(int_show_certification_fragment_state == INT_SHOW_CERTIFICATION_FRAGMENT_DESTROY)) {
            this.finish();
        }
        return true;
    }

    @Override
    protected void onResume() {
        Log.i("vpn", "onResume");
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void updateState(final String state, final String logmessage, final int localizedResId, final VpnStatus.ConnectionStatus level) {
        //改变状态信息
        Log.v("vpn state", state + "|" + logmessage + "|" + level.toString());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (level == VpnStatus.ConnectionStatus.LEVEL_CONNECTED) {
                    start.setText("停止");
                    isConnected = true;
                } else if (level == VpnStatus.ConnectionStatus.UNKNOWN_LEVEL || level == VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED) {
                    start.setText("启动");
                    isConnected = false;
                } else if (level == VpnStatus.ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT) {
                    start.setText("准备链接");
                    isConnected = false;
                } else {
                    start.setText(getString(localizedResId));
                    isConnected = false;
                }
            }
        });
    }

    public class InfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            if (msg.equals("PIN码被锁住")) {
                /*ProfileManager.setConntectedVpnProfileDisconnected(getApplicationContext());
                OpenVpnManagementThread.stopOpenVPN();
                finish();*/
            }
            Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
