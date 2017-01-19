package com.zd.vpn.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.zd.vpn.jni.TFJni;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.spongycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.security.cert.CertificateException;
//import java.security.cert.CertificateFactory;
//import java.security.cert.X509Certificate;

/**
 * Created by Administrator on 15-12-23.
 */
public class TFJniUtils {

    private TFJni tfJni = new TFJni();

    public String getPath(Context context) {
        context.getExternalCacheDir().toString();
        String sdPath = ConfigPathUtil.getSDPath(context);
        if (sdPath != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                return sdPath + "/Android/data/" + context.getPackageName();
            } else {
                return null;
            }
        }
        return null;
    }

    public static X509CertificateStructure get_x509_certificate(byte[] derFile) {
        try{
            ByteArrayInputStream inStream = new ByteArrayInputStream(derFile);
            ASN1InputStream aIn = new ASN1InputStream(inStream);
            ASN1Sequence seq  = (ASN1Sequence) aIn.readObject();
            X509CertificateStructure cert = new X509CertificateStructure(seq);
            return cert;
        }catch (Exception e){
            return null;
        }
    }

    public boolean compareSN(Context context) {
        SharedPreferences sPreferences = context.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
        String pin = sPreferences.getString("vpn.pin", "");
        String strContainer = sPreferences.getString("vpn.certContainerName", "");
        final Sender sender = new Sender(context);
        if (pin.equals("")) {
            sender.sendTF("PIN码未设置,请先配置PIN码!");
        }
        if (strContainer.equals("")) {
            sender.sendTF("证书容器未设置,请先配置证书容器!");
        }
        File pathString = new File(context.getFilesDir().getAbsolutePath());
        File file = pathString.getParentFile();
        String soLib = file.getAbsolutePath() + "/lib/libSecuTFMcApi.so";
        try {
            int iRet = tfJni.ApiInit(soLib);
            if (iRet == 0 || iRet == -3) {
                String path = getPath(context);
                if (path != null) {
                    tfJni.SetAppPath(path);
                }
                try {
                    iRet = tfJni.OpenDevice();
                    if (iRet == 0) {
                        /*byte[] bHwVer = new byte[32];
                        byte[] bSwVer = new byte[32];
                        iRet = tfJni.GetVersion(bHwVer, bSwVer);
                        String bHwVer_V = new String(bHwVer).trim();
                        String bSwVer_V = new String(bSwVer).trim();*/
                        iRet = tfJni.SetActiveContainer(strContainer);
                        byte[] password = pin.getBytes();
                        try {
                            iRet = tfJni.Login(password, 6);
                            if (iRet == 0) {
                                byte[] sn = new byte[12];
                                long[] snLen = new long[1];
                                iRet = tfJni.ReadSerialNum(sn, snLen);
                                String device_sn = new String(sn).trim();

                                long[] CertLen = new long[1];
                                byte[] Cert = null;
                                int type = TFJni.AT_SIGNATURE;
                                iRet = tfJni.ReadCertificate(null, CertLen, type);
                                if (iRet != 0) {
                                    type = TFJni.AT_KEYEXCHANGE;
                                    iRet = tfJni.ReadCertificate(null, CertLen, type);
                                    Cert = new byte[(int) CertLen[0]];
                                    iRet = tfJni.ReadCertificate(Cert, CertLen, type);
                                } else {
                                    Cert = new byte[(int) CertLen[0]];
                                    iRet = tfJni.ReadCertificate(Cert, CertLen, type);
                                }
                                String oldsn = sPreferences.getString("vpn.sn","");
                                String oldcert = sPreferences.getString("vpn.cert","");
                                String cert = new String(Base64.encode(Cert));
                                if(oldsn.equals(device_sn)&&cert.equals(oldcert)){
                                    return true;
                                }
                            } else {
                                sender.sendTF("登陆设备失败!");
                            }
                        } catch (Exception e) {
                            sender.sendTF("读取证书信息失败!");
                        } finally {
                            iRet = tfJni.Logout();
                        }
                    } else {
                        sender.sendTF("打开设备失败!");
                    }
                } finally {
                    iRet = tfJni.CloseDevice();
                }
            } else {
                sender.sendTF("动态库加载失败!");
            }
        } finally {
            tfJni.ApiUninit();
        }
        return false;
    }

    public void loadCrt(Context context) {
        SharedPreferences sPreferences = context.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
        String pin = sPreferences.getString("vpn.pin", "");
        String strContainer = sPreferences.getString("vpn.certContainerName", "");
        final Sender sender = new Sender(context);
        if (pin.equals("")) {
            sender.sendTF("PIN码未设置,请先配置PIN码!");
        }
        if (strContainer.equals("")) {
            sender.sendTF("证书容器未设置,请先配置证书容器!");
        }
        File pathString = new File(context.getFilesDir().getAbsolutePath());
        File file = pathString.getParentFile();
        String soLib = file.getAbsolutePath() + "/lib/libSecuTFMcApi.so";
        try {
            int iRet = tfJni.ApiInit(soLib);
            if (iRet == 0 || iRet == -3) {
                String path = getPath(context);
                if (path != null) {
                    tfJni.SetAppPath(path);
                }
                try {
                    iRet = tfJni.OpenDevice();
                    if (iRet == 0) {
                        /*byte[] bHwVer = new byte[32];
                        byte[] bSwVer = new byte[32];
                        iRet = tfJni.GetVersion(bHwVer, bSwVer);
                        String bHwVer_V = new String(bHwVer).trim();
                        String bSwVer_V = new String(bSwVer).trim();*/
                        iRet = tfJni.SetActiveContainer(strContainer);
                        byte[] password = pin.getBytes();
                        try {
                            iRet = tfJni.Login(password, 6);
                            if (iRet == 0) {
                                byte[] sn = new byte[12];
                                long[] snLen = new long[1];
                                iRet = tfJni.ReadSerialNum(sn, snLen);
                                String device_sn = new String(sn).trim();

                                long[] CertLen = new long[1];
                                byte[] Cert = null;
                                int type = TFJni.AT_SIGNATURE;
                                iRet = tfJni.ReadCertificate(null, CertLen, type);
                                if (iRet != 0) {
                                    type = TFJni.AT_KEYEXCHANGE;
                                    iRet = tfJni.ReadCertificate(null, CertLen, type);
                                    Cert = new byte[(int) CertLen[0]];
                                    iRet = tfJni.ReadCertificate(Cert, CertLen, type);
                                } else {
                                    Cert = new byte[(int) CertLen[0]];
                                    iRet = tfJni.ReadCertificate(Cert, CertLen, type);
                                }

                            /*    CertificateFactory certificate_factory = CertificateFactory.getInstance("X.509");
                                X509Certificate x509certificate = (X509Certificate) certificate_factory.generateCertificate(new ByteArrayInputStream(Cert));
                                String serialNum = x509certificate.getSerialNumber().toString(16).toUpperCase();*/
                                X509CertificateStructure  x509CertificateStructure = get_x509_certificate(Cert);
                                String serialNum = x509CertificateStructure.getSerialNumber().getValue().toString(16);
                                SharedPreferences.Editor editor = sPreferences.edit();
                                editor.putString("vpn.serialNumber", serialNum);
                                editor.putBoolean("vpn.read", true);
                                editor.putString("vpn.subject", x509CertificateStructure.getSubject().toString());
                                editor.putString("vpn.notBefore", x509CertificateStructure.getStartDate().toString());
                                editor.putString("vpn.notAfter", x509CertificateStructure.getEndDate().toString());
                                editor.putString("vpn.issue", x509CertificateStructure.getIssuer().toString());
                                editor.putString("vpn.cert", new String(Base64.encode(Cert)));
                                editor.putString("vpn.sn", device_sn);
                                editor.commit();
                            } else {
                                sender.sendTF("登陆设备失败!");
                            }
                        } catch (Exception e) {
                            sender.sendTF("读取证书信息失败!");
                        } finally {
                            iRet = tfJni.Logout();
                        }
                    } else {
                        sender.sendTF("打开设备失败!");
                    }
                } finally {
                    iRet = tfJni.CloseDevice();
                }
            } else {
                sender.sendTF("动态库加载失败!");
            }
        } finally {
            tfJni.ApiUninit();
        }
    }

    public String getCrt(Context context) {
        SharedPreferences sPreferences = context.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
        boolean read = sPreferences.getBoolean("vpn.read", false);
        final Sender sender = new Sender(context);
        if (!read) {
            sender.sendTF("请先初始化基本配置！");
            return null;
        }else {
            if (compareSN(context)) {
                String cert = sPreferences.getString("vpn.cert", null);
                return cert;
                /*byte[] bytes = Base64.decode(cert.getBytes());
                CertificateFactory certificate_factory = null;
                try {
                    certificate_factory = CertificateFactory.getInstance("X.509");
                    X509Certificate x509certificate = (X509Certificate) certificate_factory.generateCertificate(new ByteArrayInputStream(bytes));
                    return x509certificate;
                } catch (CertificateException e) {
                    e.printStackTrace();
                    return null;
                }*/
            }else {
                sender.sendTF("TF卡或证书已更换，请重新进行初始化配置！");
                return null;
            }
        }
    }
}
