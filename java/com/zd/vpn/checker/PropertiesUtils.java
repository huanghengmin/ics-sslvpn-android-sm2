package com.zd.vpn.checker;

/**
 * Created by Administrator on 15-12-14.
 */
public class PropertiesUtils {
    //客户端配置文件
    public static final String CONFIG_PATH = "/client.ovpn";
    //客户端证书
    public static final String CERT_PATH = "/client.crt";
    //客户端私钥
    public static final String KEY_PATH = "/client.key";
    //签名证书
    public static final String CA_PATH = "/ca.crt";
    //策略配置文件
    public static final String STRATEGY_PATH = "/strategy.json";
    //策略下载地址
    public static final String DOWN_STRATEGY = "/ClientStrategyAction_findStrategy.action";
    //检测签名证书
    public static final String DOWN_CONFIG = "/CheckAction_check.action";
    //终端校验地址
    public static final String CHECK_TERMINAL = "/DoTerminalThreeYards";
    //检测更新
    public static final String CHECK_UPGRADE = "/UpgradeVersionAction_check.action";
    //更新
    public static final String ACTION_UPGRADE = "/UpgradeVersionAction_upgrade.action";
    //APK File
    public static final String APK_FILE = "sslvpn.apk";
}
