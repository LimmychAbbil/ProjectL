package net.lim.model;

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
        //TODO add StringUtils library
        return (serverName == null || serverName.isEmpty()) ? "Unnamed server" + ip : serverName;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
