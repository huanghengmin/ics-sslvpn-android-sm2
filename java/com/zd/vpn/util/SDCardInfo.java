package com.zd.vpn.util;

/**
 * Created by Administrator on 2016/7/25.
 */
public class SDCardInfo {
    /**
     * 名称
     */
    private String label;

    /**
     * 挂载点
     */
    private String mountPoint;

    /**
     * 是否已挂载
     */
    private boolean mounted;

    /**
     * @Description:获取SD卡名称
     * @return SD卡名称
     */
    public String getLabel() {
        return label;
    }

    /**
     * @Description:设置SD卡名称
     * @param label SD卡名称
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @Description:获取挂载路径
     * @return 挂载路径
     */
    public String getMountPoint() {
        return mountPoint;
    }

    /**
     * @Description:设置挂载路径
     * @param mountPoint 挂载路径
     */
    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    /**
     * @Description:是否已经挂载
     * @return true:已经挂载，false:未挂载
     */
    public boolean isMounted() {
        return mounted;
    }

    /**
     * @Description:设置是否已经挂载
     * @param mounted
     *        true:已经挂载，false:未挂载
     */
    public void setMounted(boolean mounted) {
        this.mounted = mounted;
    }

    @Override
    public String toString() {
        return "SDCardInfo [label=" + label + ", mountPoint=" + mountPoint + ", mounted=" + mounted + "]";
    }

}
