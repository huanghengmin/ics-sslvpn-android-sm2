package com.zd.vpn.jni;

public class TFJni {
	public static int CALG_SHA1_WITH_RSA = 0;
	public static int CALG_MD5_WITH_RSA = 1;
	public static int AT_KEYEXCHANGE = 1;
	public static int AT_SIGNATURE = 2;
	public static int CALG_DES = 26113;
	public static int CALG_3DES_112 = 26121;
	public static int CALG_3DES = 26115;
	public static int CALG_SM1 = 26130;
	public static int KEY_NO_PADDING = 0;
	public static int KEY_PKCS5_PADDING = 2;

	static{
		System.loadLibrary("ZDTFJNI");
	}

	public native int ApiInit(String ss);
	public native int ApiUninit();
	public native int SetAppPath(String jAppPath);
	
	
	public native int OpenDevice();
	public native int CloseDevice();
	
	
	public native int Login(byte[] var1, long var2);
	
    public native int ReadCertificate(byte[] var1, long[] var2, long var3);
	
	public native int GetVersion(byte[] var1, byte[] var2);

	public native int Logout();

	public native int ChangeUserPin(byte[] var1, long var2, byte[] var4, long var5);

	public native int ReadSerialNum(byte[] var1, long[] var2);
	
	public native int SetActiveContainer(String var1);
	
	public native int EnumContainerName(byte[] var1, int var2, int var3);	
}
