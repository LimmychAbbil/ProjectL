package net.lim.model;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class RestConnection extends Connection {

    private String url;

    public RestConnection(String url) {
        this.url = url;
    }

    @Override
    public boolean login(String userName, String password) {
        Client client = ClientBuilder.newClient();
        javax.ws.rs.core.Response response = client.target(url).queryParam("username", userName).queryParam("pass", password).request().get();
        return response.getStatus() == 200;
    }

    @Override
    public void sendRegistration(String userName, String password) {

    }
}
