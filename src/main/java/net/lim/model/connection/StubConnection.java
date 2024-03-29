package net.lim.model.connection;

import net.lim.model.ServerInfo;
import net.lim.model.adv.Advertisement;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;

public class StubConnection extends Connection {

    @Override
    public boolean validateConnection() {
        super.closed = false;
        return true;
    }

    @Override
    public boolean validateVersionSupported(String currentVersion) {
        return true;
    }

    @Override
    public boolean login(String userName, String password) {
        return true;
    }

    @Override
    public int sendRegistration(String userName, String password) {
        return 200;
    }

    @Override
    public JSONObject getFileServerInfo() {
        throw new IllegalStateException("Shouldn't get here when offline mode is selected");
    }

    @Override
    public JSONArray getIgnoredFilesInfo() {
        throw new IllegalStateException("Shouldn't get here when offline mode is selected");
    }

    @Override
    public JSONObject getFullHashInfo() {
        throw new IllegalStateException("Shouldn't get here when offline mode is selected");
    }

    @Override
    public JSONObject getServersInfoJSON() {
        return null;
    }

    @Override
    public String getBackgroundImageName() {
        return null;
    }

    @Override
    public List<Advertisement> getAdvs() {
        return Collections.emptyList();
    }

    @Override
    public String getServerLaunchCommand(ServerInfo selectedServer) {
        return "echo 'Stub connection is used'";
    }

    @Override
    public String getRulesURL() {
        return "http://google.com";
    }
}
