package com.zd.vpn.util;

/**
 * Created by Administrator on 15-8-26.
 */
public class ReturnCode {
    /**
     * 成功状态
     *
     * "客户端绑定信息匹配成功";    RETURN_CLIENT_MSG_MATCH_SUCCESS
     * "服务器未启用三码合一校验";  RETURN_SERVER_DISABLE_CLIENT_MSG_MATCH
     * "客户端绑定信息保存成功";    RETURN_SERVER_SAVE_CLIENT_MSG_SUCCESS
     * "服务器未找到对应用户";      RETURN_SERVER_NOT_FOUND_USER
     * "客户端启动成功";            RETURN_CLIENT_START_SUCCESS
     * "客户端状态信息校验成功"     RETURN_CLIENT_STATUS_SUCCESS
     */

    /**
     * 失败状态
     *
     * "客户端绑定不匹配，请更换成为原有TF卡和SIM卡后尝试连接";   RETURN_CLIENT_MSG_NOT_MATCH
     * "客户端证书已被吊销，无法拨号服务器";                      RETURN_CLIENT_REVOKED
     * "用户已被禁止拨号";                                        RETURN_CLIENT_DISABLE
     * "启动出错，请先初始化基本配置.";                           RETURN_CLIENT_NEED_CONFIG
     * "启动出错，未读取到证书，请确定加密卡存在并已进行证书签发";RETURN_CLIENT_READ_CERT_ERROR
     * "客户端绑定信息校验失败";                                  RETURN_CLIENT_MSG_ERROR
     * "客户端状态信息校验失败"                                   RETURN_CLIENT_STATUS_ERROR
     */

    public static final String RETURN_CLIENT_MSG_MATCH_SUCCESS = "0X00DFAB1";          //"客户端绑定信息匹配成功";
    public static final String RETURN_SERVER_DISABLE_CLIENT_MSG_MATCH = "0X00DFAB2";  //"服务器未启用三码合一校验";
    public static final String RETURN_SERVER_SAVE_CLIENT_MSG_SUCCESS = "0X00DFAB3";    //"客户端绑定信息保存成功";
    public static final String RETURN_SERVER_NOT_FOUND_USER = "0X00DFAB4";              //"服务器未找到对应用户";
    public static final String RETURN_CLIENT_START_SUCCESS = "0X00DFAB5";               //"客户端启动成功";
    public static final String RETURN_CLIENT_STATUS_SUCCESS = "0X00DFAB13";             //"客户端状态信息校验成功";

    public static final String RETURN_CLIENT_MSG_NOT_MATCH = "0X00DFAB6";               //"客户端绑定不匹配，请更换成为原有TF卡和SIM卡后尝试连接";
    public static final String RETURN_CLIENT_REVOKED = "0X00DFAB7";                      //"客户端证书已被吊销，无法拨号服务器";
    public static final String RETURN_CLIENT_DISABLE = "0X00DFAB8";                      //"用户已被禁止拨号";
    public static final String RETURN_CLIENT_NEED_CONFIG = "0X00DFAB9";                 //"启动出错，请先初始化基本配置.";
    public static final String RETURN_CLIENT_READ_CERT_ERROR = "0X00DFAB10";           //"启动出错，未读取到证书，请确定加密卡存在并已进行证书签发";
    public static final String RETURN_CLIENT_MSG_ERROR = "0X00DFAB11";                  //"客户端绑定信息校验失败";
    public static final String RETURN_CLIENT_STATUS_ERROR = "0X00DFAB12";               //"客户端状态信息校验失败";
    public static final String RETURN_PLEASE_INIT_ERROR = "0X00DFAB13";               //"客户端需要先初始化";


    public static String getReturnStatusMsg(String code) {
        if (code.equals(RETURN_CLIENT_MSG_MATCH_SUCCESS)) {
            return "客户端绑定信息匹配成功";
        } else if (code.equals(RETURN_SERVER_DISABLE_CLIENT_MSG_MATCH)) {
            return "服务器未启用三码合一校验";
        } else if (code.equals(RETURN_SERVER_SAVE_CLIENT_MSG_SUCCESS)) {
            return "客户端绑定信息保存成功";
        } else if (code.equals(RETURN_SERVER_NOT_FOUND_USER)) {
            return "服务器未找到对应用户";
        } else if (code.equals(RETURN_CLIENT_START_SUCCESS)) {
            return "客户端启动成功";
        } else if (code.equals(RETURN_CLIENT_MSG_NOT_MATCH)) {
            return "客户端绑定不匹配，请更换成为原有TF卡和SIM卡后尝试连接";
        } else if (code.equals(RETURN_CLIENT_REVOKED)) {
            return "客户端证书已被吊销，无法拨号服务器";
        } else if (code.equals(RETURN_CLIENT_DISABLE)) {
            return "用户已被禁止拨号";
        } else if (code.equals(RETURN_CLIENT_NEED_CONFIG)) {
            return "启动出错，请先初始化基本配置.";
        } else if (code.equals(RETURN_CLIENT_READ_CERT_ERROR)) {
            return "启动出错，未读取到证书，请确定加密卡存在并已进行证书签发";
        } else if (code.equals(RETURN_CLIENT_MSG_ERROR)) {
            return "客户端绑定信息校验失败";
        } else if (code.equals(RETURN_CLIENT_STATUS_ERROR)) {
            return "客户端状态信息校验失败";
        } else if (code.equals(RETURN_CLIENT_STATUS_SUCCESS)) {
            return "客户端状态信息校验成功";
        }else {
            return null;
        }
    }


    public static boolean getBooleanMsg(String code){
        if (code.equals(RETURN_CLIENT_MSG_MATCH_SUCCESS)) {
            return true;
        } else if (code.equals(RETURN_SERVER_DISABLE_CLIENT_MSG_MATCH)) {
            return true;
        } else if (code.equals(RETURN_SERVER_SAVE_CLIENT_MSG_SUCCESS)) {
            return true;
        } else if (code.equals(RETURN_SERVER_NOT_FOUND_USER)) {
            return true;
        } else if (code.equals(RETURN_CLIENT_START_SUCCESS)) {
            return true;
        }  else if (code.equals(RETURN_CLIENT_STATUS_SUCCESS)) {
            return true;
        } else if (code.equals(RETURN_CLIENT_MSG_NOT_MATCH)) {
            return false;
        } else if (code.equals(RETURN_CLIENT_REVOKED)) {
            return false;
        } else if (code.equals(RETURN_CLIENT_DISABLE)) {
            return false;
        } else if (code.equals(RETURN_CLIENT_NEED_CONFIG)) {
            return false;
        } else if (code.equals(RETURN_CLIENT_READ_CERT_ERROR)) {
            return false;
        } else if (code.equals(RETURN_CLIENT_MSG_ERROR)) {
            return false;
        } else if (code.equals(RETURN_CLIENT_STATUS_ERROR)) {
            return false;
        }else {
            return false;
        }
    }
}
