package net.lim.model;

import javafx.scene.control.Control;
import org.apache.commons.lang3.StringUtils;

public class ServerInfo extends Control {
    public final static ServerInfo OFFLINE = new ServerInfo("Offline", "", 0);

    private String serverName;
    private String description;
    private String ip;
    private int port;

    public ServerInfo(String serverName, String description, String ip, int port) {
        this.serverName = serverName;
        this.description = description;
        this.ip = ip;
        this.port = port;
    }

    public ServerInfo(String serverName, String ip, int port) {
        this(serverName, null, ip, port);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isEmpty(serverName)) {
            builder.append("Unnamed server: ").append(ip);
        } else {
            builder.append(serverName);
        }

        if (!StringUtils.isEmpty(description)) {
            builder.append("\n").append(description);
        }

        return builder.toString();
    }

    public String getServerName() {
        return serverName;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getDescription() {
        return description;
    }
}
