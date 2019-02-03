package net.lim.model;

public class Settings {
    private static Settings ourInstance = new Settings();

    private static String xms;
    private static String filesDir;
    private static boolean offlineMode = false;
    private static String lserverURL;

    public static Settings getInstance() {
        return ourInstance;
    }

    public static String getXms() {
        return xms;
    }

    public static void setXms(String xms) {
        Settings.xms = xms;
    }

    public static String getFilesDir() {
        return filesDir;
    }

    public static void setFilesDir(String filesDir) {
        Settings.filesDir = filesDir;
    }

    public static boolean isOfflineMode() {
        return offlineMode;
    }

    public static void setOfflineMode(boolean offlineMode) {
        Settings.offlineMode = offlineMode;
    }

    public static String getLserverURL() {
        return lserverURL;
    }

    public static void setLserverURL(String lserverURL) {
        Settings.lserverURL = lserverURL;
    }

    private Settings() {
    }
}
