package com.zd.vpn.util;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: 钱晓盼
 * Date: 12-6-11
 * Time: 下午2:15
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil {
    public static boolean saveUploadFile(File uploadFile,String outFilePath) throws IOException {
        File file = new File(outFilePath);
        if(file.exists()){
            file.delete();
        }
        copy(uploadFile,outFilePath);
        return true;
    }

    public static String readFileByLines(String  path) {
        File file = new File(path);
        if(file.exists()){
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempString = null;
                while ((tempString = reader.readLine()) != null) {
                    stringBuilder.append(tempString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                    }
                }
            }
            return stringBuilder.toString();
        }
        return null;
    }

    /**
     * 上传文件
     * @param savePath          保存路径
     * @param uploadFile        上传文件
     * @param uploadFileFileName  上传文件文件名
     * @throws IOException
     */
    public static void upload(String savePath,File uploadFile,String uploadFileFileName) throws IOException {
        File dir = new File(savePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        String newFile = dir+"/"+uploadFileFileName;
        copy(uploadFile, newFile);
    }

    /**
     *
     * @param from   被复制文件
     * @param to     保存后文件地址
     */
    public static void copy(File from,String to) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        bis = new BufferedInputStream(
                new FileInputStream(from));
        bos = new BufferedOutputStream(
                new FileOutputStream(
                        new File(to)));
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len = bis.read(buf))!=-1){
            bos.write(buf,0,len);
        }
        bos.flush();
        bos.close();
        bis.close();
    }


    public static void save(InputStream in,File f) throws IOException {
        FileOutputStream out = new FileOutputStream(f);
        byte[] content = new byte[1024 * 1024];
        int length = -1;
        while ((length = in.read(content, 0, content.length)) != -1) {
            out.write(content, 0, length);
            out.flush();
        }
        out.close();
        in.close();
    }

    /**
     *
     * @param from   被复制文件
     * @param to     保存后文件地址
     */
    public static void copy(byte[] from,String to) throws IOException {
        File to_file = new File(to);
        FileOutputStream fStream2 = new FileOutputStream(to_file);
        fStream2.write(from);
        fStream2.flush();
        fStream2.close();
    }


    //删除指定文件夹下所有文件
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }


    //删除文件夹
//param folderPath 文件夹完整绝对路径

    public static boolean delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static String readAsString(File f){
        StringBuilder sb= new StringBuilder();
        InputStream is= null;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (line!=null){
            sb.append(line);
            sb.append("\n");
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
