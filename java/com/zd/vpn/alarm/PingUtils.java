package com.zd.vpn.alarm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;

import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Message;

public class PingUtils {
    public final static int PING_COUNT = 1;
    public final static int PING_TIMEOUT = 5;

    public static String ping(String address) {
        long pingStartTime = 0;
        long pingEndTime = 0;
        ArrayList<Double> rrts = new ArrayList<Double>();
        try {
            HttpClient client = AndroidHttpClient.newInstance("Linux; Android");
            HttpHead headMethod = new HttpHead("http://" + address);
            headMethod.addHeader(new BasicHeader("Connection", "close"));
            headMethod.setParams(new BasicHttpParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 1000));
            int timeOut = (int) (1000 * (double) PING_TIMEOUT / PING_COUNT);
            HttpConnectionParams.setConnectionTimeout(headMethod.getParams(), timeOut);

            for (int i = 0; i < PING_COUNT; i++) {
                pingStartTime = System.currentTimeMillis();
                HttpResponse response = client.execute(headMethod);
                pingEndTime = System.currentTimeMillis();
                rrts.add((double) (pingEndTime - pingStartTime));
            }
            int packetLoss = PING_COUNT - rrts.size();
            return disposeResult(packetLoss, rrts);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            pingJava(address);
        }
        return null;
    }

    public static String pingJava(String address) {
        long pingStartTime = 0;
        long pingEndTime = 0;
        ArrayList<Double> rrts = new ArrayList<Double>();

        try {
            int timeOut = (int) (1000 * (double) PING_TIMEOUT / PING_COUNT);
            for (int i = 0; i < PING_COUNT; i++) {
                pingStartTime = System.currentTimeMillis();
                boolean status;
                status = InetAddress.getByName(address).isReachable(timeOut);
                pingEndTime = System.currentTimeMillis();
                long rrtVal = pingEndTime - pingStartTime;
                if (status) {
                    rrts.add((double) rrtVal);
                }
            }
            int packetLoss = PING_COUNT - rrts.size();
            return disposeResult(packetLoss, rrts);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String disposeResult(int packetLoss, ArrayList<Double> rrts) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double avg;
        double total = 0;
        if (rrts.size() == 0) {
            return null;
        }
        for (double rrt : rrts) {
            if (rrt < min) {
                min = rrt;
            }
            if (rrt > max) {
                max = rrt;
            }
            total += rrt;
        }
        avg = total / rrts.size();
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putDouble("min", min);
        bundle.putDouble("max", max);
        bundle.putDouble("avg", avg);
        bundle.putInt("send", PING_COUNT);
        bundle.putInt("loss", packetLoss);
        msg.setData(bundle);
        return dispatchMessage(msg);
    }


    private static String dispatchMessage(Message msg) {
        Bundle bundle = msg.getData();
        Double min = bundle.getDouble("min");
        Double max = bundle.getDouble("max");
        Double avg = bundle.getDouble("avg");
        int send = bundle.getInt("send");
        int loss = bundle.getInt("loss");
        StringBuilder sb = new StringBuilder();
        sb.append("数据包：已发送 = ").append(send)
                .append("，已接收 = ").append(send - loss)
                .append("，丢失 = ").append(loss).append("\n")
                .append("往返行程的估计时间<以毫秒为单位>：\n")
                .append("最短 = ").append(min).append("ms")
                .append("，最长 = ").append(max).append("ms")
                .append("，平均 = ").append(avg).append("ms")
                .append("\n\n\n");
        return sb.toString();
    }
}
