package net.lim.model;

import java.lang.management.ManagementFactory;

public class Settings {
    private static Settings instance = new Settings();

    public static long MAX_RAM_MB_SIZE = (((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize()) / (1024 * 1024);

    public static long DEFAULT_XMS_MB_SIZE = 3 * 1024; //3 GB

    /**
     * XMx property in Megabytes
     */
    private long xmx;
    private String filesDir;
    private boolean offlineMode = false;
    private String lserverURL;

    public static Settings getInstance() {
        return instance;
    }

    public long getXmx() {
        return xmx;
    }

    public void setXmx(long xmx) {
        this.xmx = xmx;
    }

    public String getFilesDir() {
        return filesDir;
    }

    public void setFilesDir(String filesDir) {
        this.filesDir = filesDir;
    }

    public boolean isOfflineMode() {
        return offlineMode;
    }

    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
    }

    public String getLserverURL() {
        return lserverURL;
    }

    public void setLserverURL(String lserverURL) {
        this.lserverURL = lserverURL;
    }

    private Settings() {
    }
}
