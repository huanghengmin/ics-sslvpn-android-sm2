package com.zd.vpn.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ConfigFileUtil {

    private static final String remote = "remote ";

    public static void update(String ip, String def_port, byte[] data,String config_path) {
        String data_str = new String(data);
        StringBuilder sb = new StringBuilder();
        String[] datas = null;
        if (data_str.contains("\n")) {
            datas = data_str.split("\n");
        }
        if (datas != null) {
            for (int i = 0; i < datas.length; i++) {
                String line = datas[i];
                if (line.startsWith(remote)) {
                    line = remote + ip + " " + def_port;
                }
                sb.append(line).append("\n");
            }
        }
        try {
            FileOutputStream fo = new FileOutputStream(config_path);
            fo.write(sb.toString().getBytes());
            fo.flush();
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getIp(byte[] data) {
        String data_str = new String(data);
        String[] datas = null;
        if (data_str.contains("\n")) {
            datas = data_str.split("\n");
        }
        if (datas != null) {
            for (int i = 0; i < datas.length; i++) {
                String line = datas[i];
                if (line.startsWith(remote)) {
                    return (line.split(" ")[1]);
                }

            }
        }
        return "";

    }

    public static String getPort(byte[] data) {
        String data_str = new String(data);
        String[] datas = null;
        if (data_str.contains("\n")) {
            datas = data_str.split("\n");
        }
        if (datas != null) {
            for (int i = 0; i < datas.length; i++) {
                String line = datas[i];
                if (line.startsWith(remote)) {
                    return (line.split(" ")[2]);
                }

            }
        }
        return "";

    }

    public static void update(String ip, String def_port,boolean tcpUdp, byte[] data,String config_path) {
        String data_str = new String(data);
        StringBuilder sb = new StringBuilder();
        String[] datas = null;
        if (data_str.contains("\n")) {
            datas = data_str.split("\n");
        }
        if (datas != null) {
            boolean replace  = false;
            for (int i = 0; i < datas.length; i++) {
                String line = datas[i];
                if (line.startsWith(remote)) {
                    if(!replace) {
                        line = remote + ip + " " + def_port;
                        replace = true;
                    }
                } else if (line.startsWith("proto")) {
                    line = "proto " + (tcpUdp ? "udp" : "tcp");
                }
                sb.append(line).append("\n");
            }
        }
        try {
            FileOutputStream fo = new FileOutputStream(config_path);
            fo.write(sb.toString().getBytes());
            fo.flush();
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
