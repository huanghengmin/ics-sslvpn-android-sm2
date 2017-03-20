/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package com.zd.vpn.api;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.VpnService;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.Settings;
import android.widget.Toast;

import com.zd.vpn.LaunchVPN;
import com.zd.vpn.checker.CheckStatusSignCRLValidity;
import com.zd.vpn.checker.CheckTerminalStatusPost;
import com.zd.vpn.core.ConfigParser;
import com.zd.vpn.core.OpenVPNService;
import com.zd.vpn.core.VPNLaunchHelper;
import com.zd.vpn.core.VpnStatus;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import com.zd.vpn.VpnProfile;
import com.zd.vpn.core.ProfileManager;
import com.zd.vpn.service.CheckUtils;
import com.zd.vpn.service.StrategyService;
import com.zd.vpn.util.ConfigUtil;
import com.zd.vpn.util.NetUtil;
import com.zd.vpn.util.ReturnCode;
import com.zd.vpn.util.ReturnObject;
import com.zd.vpn.util.TFJniUtils;
import com.zd.vpn.util.TelInfoUtil;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class ExternalOpenVPNService extends Service implements VpnStatus.StateListener {

    private static final int SEND_TOALL = 0;

    final RemoteCallbackList<IOpenVPNStatusCallback> mCallbacks =
            new RemoteCallbackList<>();

    private OpenVPNService mService;
    private ExternalAppDatabase mExtAppDb;


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

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && Intent.ACTION_UNINSTALL_PACKAGE.equals(intent.getAction())){
                // Check if the running config is temporary and installed by the app being uninstalled
                VpnProfile vp = ProfileManager.getLastConnectedVpn();
                if (ProfileManager.isTempProfile()) {
                    if(intent.getPackage().equals(vp.mProfileCreator)) {
                        if (mService != null && mService.getManagement() != null)
                            mService.getManagement().stopVPN(false);
                    }
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        VpnStatus.addStateListener(this);
        mExtAppDb = new ExternalAppDatabase(this);

        Intent intent = new Intent(getBaseContext(), OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mHandler.setService(this);
        IntentFilter uninstallBroadcast = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED );
        registerReceiver(mBroadcastReceiver, uninstallBroadcast);

    }

    private final IOpenVPNAPIService.Stub mBinder = new IOpenVPNAPIService.Stub() {

        private String checkOpenVPNPermission() throws SecurityRemoteException {
            PackageManager pm = getPackageManager();

            for (String appPackage : mExtAppDb.getExtAppList()) {
                ApplicationInfo app;
                try {
                    app = pm.getApplicationInfo(appPackage, 0);
                    if (Binder.getCallingUid() == app.uid) {
                        return appPackage;
                    }
                } catch (NameNotFoundException e) {
                    // App not found. Remove it from the list
                    mExtAppDb.removeApp(appPackage);
                }

            }
            throw new SecurityException("Unauthorized OpenVPN API Caller");
        }

        @Override
        public List<APIVpnProfile> getProfiles() throws RemoteException {
            checkOpenVPNPermission();

            ProfileManager pm = ProfileManager.getInstance(getBaseContext());

            List<APIVpnProfile> profiles = new LinkedList<>();

            for (VpnProfile vp : pm.getProfiles()) {
                if (!vp.profileDeleted)
                    profiles.add(new APIVpnProfile(vp.getUUIDString(), vp.mName, vp.mUserEditable, vp.mProfileCreator));
            }

            return profiles;
        }


        private void startProfile(VpnProfile vp)
        {
           /* Intent vpnPermissionIntent = VpnService.prepare(ExternalOpenVPNService.this);
            *//* Check if we need to show the confirmation dialog,
             * Check if we need to ask for username/password *//*

            int neddPassword = vp.needUserPWInput(false);

            if(vpnPermissionIntent != null || neddPassword != 0){
                Intent shortVPNIntent = new Intent(Intent.ACTION_MAIN);
                shortVPNIntent.setClass(getBaseContext(), LaunchVPN.class);
                shortVPNIntent.putExtra(LaunchVPN.EXTRA_KEY, vp.getUUIDString());
                shortVPNIntent.putExtra(LaunchVPN.EXTRA_HIDELOG, true);
                shortVPNIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(shortVPNIntent);
            } else {
                VPNLaunchHelper.startOpenVpn(vp, getBaseContext());
            }*/

        }

        @Override
        public void startProfile(String profileUUID) throws RemoteException {
            /*checkOpenVPNPermission();

            VpnProfile vp = ProfileManager.get(getBaseContext(), profileUUID);
            if (vp.checkProfile(getApplicationContext()) != com.zd.vpn.R.string.no_error_found)
                throw new RemoteException(getString(vp.checkProfile(getApplicationContext())));

            startProfile(vp);*/
        }

        public void startVPN(String inlineConfig) throws RemoteException {
           /* String callingApp = checkOpenVPNPermission();

            ConfigParser cp = new ConfigParser();
            try {
                cp.parseConfig(new StringReader(inlineConfig));
                VpnProfile vp = cp.convertProfile();
                vp.mName = "Remote APP VPN";
                if (vp.checkProfile(getApplicationContext()) != com.zd.vpn.R.string.no_error_found)
                    throw new RemoteException(getString(vp.checkProfile(getApplicationContext())));

                vp.mProfileCreator = callingApp;


                *//*int needpw = vp.needUserPWInput(false);
                if(needpw !=0)
                    throw new RemoteException("The inline file would require user input: " + getString(needpw));
                    *//*

                ProfileManager.setTemporaryProfile(vp);

                startProfile(vp);

            } catch (IOException | ConfigParser.ConfigParseError e) {
                throw new RemoteException(e.getMessage());
            }*/
        }


        @Override
        public boolean addVPNProfile(String name, String config) throws RemoteException {
            return addNewVPNProfile(name, true, config) != null;
        }


        @Override
        public APIVpnProfile addNewVPNProfile(String name, boolean userEditable, String config) throws RemoteException {
            String callingPackage = checkOpenVPNPermission();

            ConfigParser cp = new ConfigParser();
            try {
                cp.parseConfig(new StringReader(config));
                VpnProfile vp = cp.convertProfile();
                vp.mName = name;
                vp.mProfileCreator = callingPackage;
                vp.mUserEditable = userEditable;
                ProfileManager pm = ProfileManager.getInstance(getBaseContext());
                pm.addProfile(vp);
                pm.saveProfile(ExternalOpenVPNService.this, vp);
                pm.saveProfileList(ExternalOpenVPNService.this);
                return new APIVpnProfile(vp.getUUIDString(), vp.mName, vp.mUserEditable, vp.mProfileCreator);
            } catch (IOException e) {
                VpnStatus.logException(e);
                return null;
            } catch (ConfigParser.ConfigParseError e) {
                VpnStatus.logException(e);
                return null;
            }
        }

        @Override
        public void removeProfile(String profileUUID) throws RemoteException {
            checkOpenVPNPermission();
            ProfileManager pm = ProfileManager.getInstance(getBaseContext());
            VpnProfile vp = ProfileManager.get(getBaseContext(), profileUUID);
            pm.removeProfile(ExternalOpenVPNService.this, vp);
        }

        @Override
        public boolean protectSocket(ParcelFileDescriptor pfd) throws RemoteException {
            checkOpenVPNPermission();
            try {
                boolean success= mService.protect(pfd.getFd());
                pfd.close();
                return success;
            } catch (IOException e) {
                throw new RemoteException(e.getMessage());
            }
        }


        @Override
        public Intent prepare(String packageName) {
            ExternalAppDatabase appDatabase = new ExternalAppDatabase(ExternalOpenVPNService.this);
            if (appDatabase.isAllowed(packageName))

                return null;
            else {
                appDatabase.addApp(packageName);
                return null;
            }

          /*  Intent intent = new Intent();
            intent.setClass(ExternalOpenVPNService.this, ConfirmDialog.class);
            return intent;*/
        }

        @Override
        public Intent prepareVPNService() throws RemoteException {
            checkOpenVPNPermission();

            if (VpnService.prepare(ExternalOpenVPNService.this) == null)
                return null;
            else
                return new Intent(getBaseContext(), GrantPermissionsActivity.class);
        }


        @Override
        public void registerStatusCallback(IOpenVPNStatusCallback cb)
                throws RemoteException {
            checkOpenVPNPermission();

            if (cb != null) {
                cb.newStatus(mMostRecentState.vpnUUID, mMostRecentState.state,
                        mMostRecentState.logmessage, mMostRecentState.level.name());
                mCallbacks.register(cb);
            }


        }

        @Override
        public void unregisterStatusCallback(IOpenVPNStatusCallback cb)
                throws RemoteException {
            checkOpenVPNPermission();

            if (cb != null)
                mCallbacks.unregister(cb);
        }

        @Override
        public void disconnect() throws RemoteException {
            checkOpenVPNPermission();
            if (mService != null && mService.getManagement() != null)
                mService.getManagement().stopVPN(false);
        }

        @Override
        public void pause() throws RemoteException {
            checkOpenVPNPermission();
            if (mService != null)
                mService.userPause(true);
        }

        @Override
        public void resume() throws RemoteException {
            checkOpenVPNPermission();
            if (mService != null)
                mService.userPause(false);

        }

        @Override
        public String startZDVPN() throws RemoteException {
//            checkOpenVPNPermission();
            SharedPreferences sPreferences = getApplicationContext().getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
            boolean read = sPreferences.getBoolean("vpn.read", false);
            if (read) {
                boolean b = CheckUtils.isServiceWorked(getApplicationContext(), "com.zd.vpn.service.StrategyService");
                if (!b) {
                    Intent service = new Intent(getApplicationContext(), StrategyService.class);
                    startService(service);
                }
                String ip = sPreferences.getString("vpn.ip", "");
                String port = sPreferences.getString("vpn.port", "");
                String poliPort = sPreferences.getString("vpn.poliPort", "");
                CheckStatusSignCRLValidity check = new CheckStatusSignCRLValidity();
                final boolean tcpudp = sPreferences.getBoolean("vpn.tcpUdp", false);
                ReturnObject returnObject = check.check(getApplicationContext(), ip, poliPort, port,tcpudp);
                if (returnObject.isFlag()) {
                    String serialnumber = sPreferences.getString("vpn.serialNumber", "");
                    TelInfoUtil telInfo = new TelInfoUtil(getApplicationContext());
                    CheckTerminalStatusPost tPost = new CheckTerminalStatusPost(getApplicationContext(), ip, poliPort, telInfo.getImei(), telInfo.getSim(), serialnumber);
                    ReturnObject object = tPost.postData();
                    if (object.isFlag()) {
                        ConfigUtil configUtil = new ConfigUtil(getApplicationContext());
                        String uuid = configUtil.config(getApplicationContext());
                        Intent intent = new Intent(getApplicationContext(), LaunchVPN.class);
                        intent.putExtra(LaunchVPN.EXTRA_KEY, uuid);
                        intent.setAction(Intent.ACTION_MAIN);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
//                        return object.getMsg();
                        return ReturnCode.RETURN_CLIENT_STATUS_SUCCESS;
                    } else {
//                        return returnObject.getMsg();
                        return ReturnCode.RETURN_CLIENT_STATUS_SUCCESS;
                    }
                } else {
//                    return returnObject.getMsg();
                    return ReturnCode.RETURN_CLIENT_STATUS_ERROR;
                }
            }else {
                return ReturnCode.RETURN_PLEASE_INIT_ERROR;
            }
        }

        @Override
        public void stopZDVPN() throws RemoteException {
//            checkOpenVPNPermission();
            if (mService != null && mService.getManagement() != null)
                mService.getManagement().stopVPN(false);
        }

        @Override
        public boolean init(String ip, int port, int strategyPort, String pinCode, String container, boolean tcpudp) {
            /*try {
                checkOpenVPNPermission();
            } catch (SecurityRemoteException e) {
                e.printStackTrace();
            }*/
            // 判断网络
            /*if (!NetUtil.isNetworkConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "请先设置网络连接！", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                return false;
            }*/
            SharedPreferences sPreferences = getApplicationContext().getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sPreferences.edit();
            editor.putString("vpn.ip", ip);
            editor.putString("vpn.port", String.valueOf(port));
            editor.putString("vpn.pin", pinCode);
            editor.putBoolean("vpn.tcpUdp", tcpudp);
            editor.putString("vpn.poliPort", String.valueOf(strategyPort));
            editor.putString("vpn.certContainerName", container);
            editor.commit();
//            SecuTFHelper helper = new SecuTFHelper();
//            helper.loadCrt(getApplication());
            TFJniUtils tfJniUtils = new TFJniUtils();
            tfJniUtils.loadCrt(getApplicationContext());
            boolean b = CheckUtils.isServiceWorked(getApplicationContext(), "com.zd.vpn.service.StrategyService");
            if (!b) {
                Intent service = new Intent(getApplicationContext(), StrategyService.class);
                startService(service);
            }
            return true;
        }

        @Override
        public String[] loadCert() {
            /*try {
                checkOpenVPNPermission();
            } catch (SecurityRemoteException e) {
                e.printStackTrace();
            }*/
            SharedPreferences sPreferences = getApplicationContext().getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
            boolean read = sPreferences.getBoolean("vpn.read", false);
            TFJniUtils tfJniUtils = new TFJniUtils();
            if (!read) {
                //请先初始化基本配置
                Toast.makeText(getBaseContext(), "读取证书信息出错，请先初始化基本配置！", Toast.LENGTH_LONG).show();
                String[] strs = new String[5];
                strs[0] = "请先初始化基本配置";
                strs[1] = "";
                strs[2] = "";
                strs[3] = "";
                strs[4] = "";
                return strs;
            } else {
                if (tfJniUtils.compareSN(getApplicationContext())) {
                    String subject = sPreferences.getString("vpn.subject", "");
                    String serialNumber = sPreferences.getString("vpn.serialNumber", "");
                    String notBefore = sPreferences.getString("vpn.notBefore", "");
                    String notAfter = sPreferences.getString("vpn.notAfter", "");
                    String issue = sPreferences.getString("vpn.issue", "");

                    String[] strs = new String[5];
                    strs[0] = subject;
                    strs[1] = serialNumber;
                    strs[2] = notBefore;
                    strs[3] = notAfter;
                    strs[4] = issue;
                    return strs;
                } else {
                    Toast.makeText(getBaseContext(), "TF卡或证书已更换，请重新进行初始化配置！", Toast.LENGTH_LONG).show();
                    String[] strs = new String[5];
                    strs[0] = "TF卡或证书已更换，请重新进行初始化配置！";
                    strs[1] = "";
                    strs[2] = "";
                    strs[3] = "";
                    strs[4] = "";
                    return strs;
                }
            }
        }
    };


    private UpdateMessage mMostRecentState;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallbacks.kill();
        unbindService(mConnection);
        VpnStatus.removeStateListener(this);
        unregisterReceiver(mBroadcastReceiver);
    }



    class UpdateMessage {
        public String state;
        public String logmessage;
        public VpnStatus.ConnectionStatus level;
        public String vpnUUID;

        public UpdateMessage(String state, String logmessage, VpnStatus.ConnectionStatus level) {
            this.state = state;
            this.logmessage = logmessage;
            this.level = level;
        }
    }

    @Override
    public void updateState(String state, String logmessage, int resid, VpnStatus.ConnectionStatus level) {
        mMostRecentState = new UpdateMessage(state, logmessage, level);
        if (ProfileManager.getLastConnectedVpn() != null)
            mMostRecentState.vpnUUID = ProfileManager.getLastConnectedVpn().getUUIDString();

        Message msg = mHandler.obtainMessage(SEND_TOALL, mMostRecentState);
        msg.sendToTarget();

    }

    private static final OpenVPNServiceHandler mHandler = new OpenVPNServiceHandler();


    static class OpenVPNServiceHandler extends Handler {
        WeakReference<ExternalOpenVPNService> service = null;

        private void setService(ExternalOpenVPNService eos) {
            service = new WeakReference<>(eos);
        }

        @Override
        public void handleMessage(Message msg) {

            RemoteCallbackList<IOpenVPNStatusCallback> callbacks;
            switch (msg.what) {
                case SEND_TOALL:
                    if (service == null || service.get() == null)
                        return;

                    callbacks = service.get().mCallbacks;


                    // Broadcast to all clients the new value.
                    final int N = callbacks.beginBroadcast();
                    for (int i = 0; i < N; i++) {
                        try {
                            sendUpdate(callbacks.getBroadcastItem(i), (UpdateMessage) msg.obj);
                        } catch (RemoteException e) {
                            // The RemoteCallbackList will take care of removing
                            // the dead object for us.
                        }
                    }
                    callbacks.finishBroadcast();
                    break;
            }
        }

        private void sendUpdate(IOpenVPNStatusCallback broadcastItem,
                                UpdateMessage um) throws RemoteException {
            broadcastItem.newStatus(um.vpnUUID, um.state, um.logmessage, um.level.name());
        }
    }



}