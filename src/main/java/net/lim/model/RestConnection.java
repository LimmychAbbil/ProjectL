package net.lim.model;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;

public class RestConnection extends Connection {

    private String url;

    public RestConnection(String url) {
        this.url = url;
    }

    @Override
    public boolean login(String userName, String password) {
        Client client = ClientBuilder.newClient();
        Form loginForm = new Form();
        loginForm.param("userName", userName);
        loginForm.param("pass", password);
        javax.ws.rs.core.Response response = client.target(url).request().post(Entity.form(loginForm));
        return response.getStatus() == 200;
    }

    @Override
    public void sendRegistration(String userName, String password) {

    }
}
