package com.zd.vpn.util;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 状态校验接口
 * <p/>
 * Created by yf on 2014-12-26.
 */
public class PostRequest {


    public static byte[] getData(String path) throws Exception {
        InputStream iStream = null;
        byte[] data = new byte[0];
        URL url = null;
        url = new URL(path);
//        System.out.println(path);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setConnectTimeout(5 * 1000);
        if (httpURLConnection.getResponseCode() == 200) {
            iStream = httpURLConnection.getInputStream();
        }
        data = readInstream(iStream);
        return data;
    }

    public static byte[] readInstream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int lengeh = -1;
        while ((lengeh = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, lengeh);
        }
        byteArrayOutputStream.close();
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

}
