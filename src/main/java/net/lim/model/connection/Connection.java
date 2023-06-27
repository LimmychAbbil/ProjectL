package net.lim.model.connection;

import net.lim.model.ServerInfo;
import net.lim.model.adv.Advertisement;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by Limmy on 02.05.2018.
 */
public abstract class Connection {
    protected boolean closed = true;

    public boolean isClosed() {
        return closed;
    }

    public abstract boolean validateConnection();

    public abstract boolean validateVersionSupported(String currentVersion);

    public abstract boolean login(String userName, String password);

    public abstract int sendRegistration(String userName, String password);

    public abstract JSONObject getFileServerInfo();

    public abstract JSONArray getIgnoredFilesInfo();

    public abstract JSONObject getFullHashInfo();

    public abstract JSONObject getServersInfoJSON();

    public abstract String getBackgroundImageName();

    public abstract List<Advertisement> getAdvs();

    public static String getErrorMessage(int code) {
        switch (code) {
            case 200:
                return "";
            case 507:
                return "Username is taken";
            case 508:
                return "You are banned to register new accounts";
            default:
                return "Unknown error";
        }
    }

    public abstract String getServerLaunchCommand(ServerInfo selectedServer);
}
