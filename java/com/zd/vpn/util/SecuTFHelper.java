package com.zd.vpn.util;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.third.SecuTFjni.SecuTFjni;


public class SecuTFHelper {
    SecuTFjni tfJni = new SecuTFjni();

    public void loadCrt(final Context context) {
        SharedPreferences sPreferences = context.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
        boolean read = sPreferences.getBoolean("vpn.read", false);
        if(!read) {
            String pin = sPreferences.getString("vpn.pin", "");
            String strContainer = sPreferences.getString("vpn.certContainerName", "");
            final Sender sender = new Sender(context);
            if (pin.equals("")) {
                sender.sendTF("PIN码未设置,请先配置PIN码!");
                return;
            }
            if (strContainer.equals("")) {
                sender.sendTF("证书容器未设置,请先配置证书容器!");
                return;
            }

            int iRet;

            iRet = tfJni.JvOpenDevice();


            byte[] bHwVer = new byte[32];
            byte[] bSwVer = new byte[32];
            iRet = tfJni.JvGetVersion(bHwVer, bSwVer);
            String bHwVer_V  = new String(bHwVer).trim();
            String bSwVer_V  = new String(bSwVer).trim();

            iRet = tfJni.JvSetActiveContainer(strContainer);


            byte[] password = pin.getBytes();
            iRet = tfJni.JvLogin(password, 6);


            byte[] sn = new byte[12];
            long[] snLen = new long[1];
            iRet = tfJni.JvReadSerialNum(sn, snLen);
            String device_sn  = new String(sn).trim();

            long[] CertLen = new long[1];
            byte[] Cert = null;
            int type = SecuTFjni.AT_KEYEXCHANGE;
            iRet = tfJni.JvReadCertificate(null, CertLen, type);
            if (iRet == -536817653) {
                type = SecuTFjni.AT_SIGNATURE;
                iRet = tfJni.JvReadCertificate(null, CertLen, type);
                Cert = new byte[(int) CertLen[0]];
                iRet = tfJni.JvReadCertificate(Cert, CertLen, type);
            } else {
                Cert = new byte[(int) CertLen[0]];
                iRet = tfJni.JvReadCertificate( Cert, CertLen, type);
            }

            CertificateFactory certificate_factory = null;
            try {
            certificate_factory = CertificateFactory.getInstance("X.509");
            X509Certificate x509certificate = (X509Certificate) certificate_factory.generateCertificate(new ByteArrayInputStream(Cert));
            String serialNum = x509certificate.getSerialNumber().toString(16).toUpperCase();
                Editor editor = sPreferences.edit();
                editor.putString("vpn.serialNumber", serialNum);
                editor.putBoolean("vpn.read", true);
                editor.putString("vpn.subject", x509certificate.getSubjectDN().toString());
                editor.putString("vpn.notBefore", x509certificate.getNotBefore().toString());
                editor.putString("vpn.notAfter", x509certificate.getNotAfter().toString());
                editor.putString("vpn.issue", x509certificate.getIssuerDN().toString());
                editor.commit();
            } catch (Exception e) {
                sender.sendTF("读取证书信息失败!");
            }
            iRet = tfJni.JvLogout();
            iRet = tfJni.JvCloseDevice();
        }
    }


    public X509Certificate getCrt(final Context context) {
        SharedPreferences sPreferences = context.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
        String pin = sPreferences.getString("vpn.pin", "");
        String strContainer = sPreferences.getString("vpn.certContainerName", "");
        final Sender sender = new Sender(context);
        if (pin.equals("")) {
            sender.sendTF("PIN码未设置,请先配置PIN码!");
            return null;
        }
        if (strContainer.equals("")) {
            sender.sendTF("证书容器未设置,请先配置证书容器!");
            return null;
        }

        int iRet;

        iRet = tfJni.JvOpenDevice();


        byte[] bHwVer = new byte[32];
        byte[] bSwVer = new byte[32];
        iRet = tfJni.JvGetVersion(bHwVer, bSwVer);
        String bHwVer_V  = new String(bHwVer).trim();
        String bSwVer_V  = new String(bSwVer).trim();

        iRet = tfJni.JvSetActiveContainer(strContainer);


        byte[] password = pin.getBytes();
        iRet = tfJni.JvLogin(password, 6);


        byte[] sn = new byte[12];
        long[] snLen = new long[1];
        iRet = tfJni.JvReadSerialNum(sn, snLen);
        String device_sn  = new String(sn).trim();

        long[] CertLen = new long[1];
        byte[] Cert = null;
        int type = SecuTFjni.AT_KEYEXCHANGE;
        iRet = tfJni.JvReadCertificate(null, CertLen, type);
        if (iRet == -536817653) {
            type = SecuTFjni.AT_SIGNATURE;
            iRet = tfJni.JvReadCertificate(null, CertLen, type);
            Cert = new byte[(int) CertLen[0]];
            iRet = tfJni.JvReadCertificate(Cert, CertLen, type);
        } else {
            Cert = new byte[(int) CertLen[0]];
            iRet = tfJni.JvReadCertificate(Cert, CertLen, type);
        }

        CertificateFactory certificate_factory = null;
        try {
            certificate_factory = CertificateFactory.getInstance("X.509");
            X509Certificate x509certificate = (X509Certificate) certificate_factory.generateCertificate(new ByteArrayInputStream(Cert));
            return x509certificate;
        } catch (Exception e) {
            sender.sendTF("读取证书信息失败!");
        }finally {
            iRet = tfJni.JvLogout();
            iRet = tfJni.JvCloseDevice();
        }
        return null;
    }
}
