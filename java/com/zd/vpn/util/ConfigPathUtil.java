package com.zd.vpn.util;


import android.content.Context;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ConfigPathUtil {
    /* private static final String sd_path0 = "/storage/extSdCard";
     private static final String sd_path1 = "/storage/sdcard0";
     private static final String sd_path2 = "/storage/sdcard1";
     private static final String sd_path3 = "/storage/ext_sd";
     private static final String sd_path4 = "/mnt/sdcard2";
     private static final String sd_path5 = "/mnt/sdcard-ext";
     private static final String sd_path6 = "/mnt/ext_sdcard";
     private static final String sd_path7 = "/mnt/sdcard/SD_CARD";
     private static final String sd_path8 = "/mnt/sdcard/extra_sd";
     private static final String sd_path9 = "/mnt/extrasd_bind";
     private static final String sd_path10 = "/mnt/sdcard/ext_sd";
     private static final String sd_path11 = "/mnt/sdcard/external_SD";
     private static final String sd_path12 = "/sdcard";
     private static final String sd_path13 = "/mnt/sdcard/external_sdcard";
     private static final String sd_path14 = "/mnt/extsd";
     private static final String sd_path15 = "/mnt/external_sd";
     private static final String sd_path16 = "/Removable/MicroSD";*/
    private static final String sd_file = "/SONICOM2.RO";

    public static String getSDPath(Context context) {
        if (Build.VERSION.SDK_INT >= 14) {
            ArrayList<String> list = SdCardUtil.getSDCardInfoAbove14(context);
            for (String l : list) {
                File file = new File(l + sd_file);
                if (file.exists()) {
                    return l;
                }
            }
        } else {
            HashMap<String, SDCardInfo> map = SdCardUtil.getSDCardInfoBelow14();
            Set<String> stringSet = map.keySet();
            for (String l : stringSet) {
                File file = new File(l + sd_file);
                if (file.exists()) {
                    return l;
                }
            }
        }
/*        File file0 = new File(sd_path0+sd_file);
        File file1 = new File(sd_path1+sd_file);
        File file2 = new File(sd_path2+sd_file);
        File file3 = new File(sd_path3+sd_file);
        File file4 = new File(sd_path4+sd_file);
        File file5 = new File(sd_path5+sd_file);
        File file6 = new File(sd_path6+sd_file);
        File file7 = new File(sd_path7+sd_file);
        File file8 = new File(sd_path8+sd_file);
        File file9 = new File(sd_path9+sd_file);
        File file10 = new File(sd_path10+sd_file);
        File file11 = new File(sd_path11+sd_file);
        File file12 = new File(sd_path12+sd_file);
        File file13 = new File(sd_path13+sd_file);
        File file14 = new File(sd_path14+sd_file);
        File file15 = new File(sd_path15+sd_file);
        File file16 = new File(sd_path16+sd_file);
        if (file0.exists()) {
            return sd_path0;
        } else if (file1.exists()) {
            return sd_path1;
        } else if (file2.exists()) {
            return sd_path2;
        } else if (file3.exists()) {
            return sd_path3;
        } else if (file4.exists()) {
            return sd_path4;
        } else if (file5.exists()) {
            return sd_path5;
        } else if (file6.exists()) {
            return sd_path6;
        } else if (file7.exists()) {
            return sd_path7;
        } else if (file8.exists()) {
            return sd_path8;
        } else if (file9.exists()) {
            return sd_path9;
        } else if (file10.exists()) {
            return sd_path10;
        } else if (file11.exists()) {
            return sd_path11;
        } else if (file12.exists()) {
            return sd_path12;
        } else if (file13.exists()) {
            return sd_path13;
        } else if (file14.exists()) {
            return sd_path14;
        } else if (file15.exists()) {
            return sd_path15;
        }  else if (file16.exists()) {
            return sd_path16;
        }else {
            return null;
        }*/
        return null;
    }
}
