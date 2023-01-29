package net.lim.model;

import org.apache.commons.lang3.StringUtils;

public class ServerInfo {
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
        return (StringUtils.isEmpty(serverName)) ? "Unnamed server" + ip : serverName + "\n" + getDescription();
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
