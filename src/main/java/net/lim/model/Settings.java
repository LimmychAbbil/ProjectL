package net.lim.model;

public class Settings {
    private static Settings ourInstance = new Settings();

    private String xms;
    private String filesDir;
    private boolean offlineMode = false;
    private String lserverURL;

    public static Settings getInstance() {
        return ourInstance;
    }

    public String getXms() {
        return xms;
    }

    public void setXms(String xms) {
        this.xms = xms;
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
