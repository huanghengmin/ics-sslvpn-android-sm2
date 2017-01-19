package com.zd.vpn.util;

import android.content.Context;
import android.content.Intent;

public class Sender {
	
	private Context context;
	
	public Sender(Context context){
		
		this.context = context;
	}
	
	public void send(String msg){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = new Intent("android.intent.action.PEOXYSTART_BROADCAST");  
	    intent.putExtra("msg", msg);  
	    context.sendBroadcast(intent);
	}
	public void sendTF(String msg){
		Intent intent = new Intent("android.intent.action.TFINFO_BROADCAST");  
	    intent.putExtra("msg", msg);  
	    context.sendBroadcast(intent);
	}
}
