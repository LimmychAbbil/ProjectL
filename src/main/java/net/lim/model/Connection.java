package net.lim.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by Limmy on 02.05.2018.
 */
public abstract class Connection {
    public abstract boolean validateConnection();
    public abstract boolean validateVersionSupported(String currentVersion);
    public abstract boolean login(String userName, String password);
    public abstract boolean sendRegistration(String userName, String password);
    public abstract JSONObject getFileServerInfo();
    public abstract JSONArray getIgnoredDirsInfo();
    public abstract JSONObject getFullHashInfo();

}
