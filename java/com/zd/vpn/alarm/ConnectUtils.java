package com.zd.vpn.alarm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectUtils {
    public final static int CONNECT_TIMEOUT = 5;

    public static synchronized boolean connect(String ip, int port) {
        Socket socket = null;
        try {
            socket = new Socket();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
            socket.connect(inetSocketAddress, CONNECT_TIMEOUT * 1000 * 60);
            if (socket.isConnected()) {
                return true;
            }
        } catch (IOException var4) {
            return false;
        } finally {
            if (socket != null&&socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static void main(String args[])throws Exception{
        boolean falg = connect("222.46.20.174",1194);
        System.out.print(falg);
    }
}
