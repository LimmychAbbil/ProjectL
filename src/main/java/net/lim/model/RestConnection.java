package net.lim.model;

import net.lim.LLauncher;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.net.ConnectException;

public class RestConnection extends Connection {

    private String url;

    public RestConnection(String url) {
        this.url = url;
    }

    //TODO should we create a new client for every call?
    @Override
    public boolean login(String userName, String password) {
        Client client = ClientBuilder.newClient();
        Form loginForm = new Form();
        loginForm.param("userName", userName);
        loginForm.param("pass", password);
        Response response = client.target(url + "/login").request().post(Entity.form(loginForm));
        client.close();
        return response.getStatus() == 200;
    }

    @Override
    public int sendRegistration(String userName, String password)  {
        Client client = ClientBuilder.newClient();
        Form registrationForm = new Form();
        registrationForm.param("userName", userName);
        registrationForm.param("pass", password);
        Response response = client.target(url + "/register").request().post(Entity.form(registrationForm));

        client.close();

        return response.getStatus();

    }

    public JSONObject getFileServerInfo() {
        Client client = ClientBuilder.newClient();
        Response response = client.target(url + "/files/serverInfo").request().get();
        JSONObject fileServerInfo = getJsonFromResponse(response);

        client.close();

        return fileServerInfo;
    }

    @Override
    public JSONArray getIgnoredDirsInfo() {
        Client client = ClientBuilder.newClient();
        Response response = client.target(url + "/files/ignoredDirs").request().get();
        JSONObject json = getJsonFromResponse(response);
        return (JSONArray) json.get("ignoredDirs");
    }

    @Override
    public JSONObject getFullHashInfo() {
        Client client = ClientBuilder.newClient();
        Response response = client.target(url + "/files/hash").request().get();
        JSONObject jsonObject = getJsonFromResponse(response);
        return jsonObject;
    }

    private JSONObject getJsonFromResponse(Response response) {
        String jsonString = response.readEntity(String.class);
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(jsonString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public boolean validateConnection() {
        Client client = ClientBuilder.newClient();
        Response response = client.target(url).request().get();
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    @Override
    public boolean validateVersionSupported(String currentVersion) {
        Client client = ClientBuilder.newClient();
        Form versionForm = new Form();
        versionForm.param("version", currentVersion);
        Response response = client.target(url + "/versionCheck").request().post(Entity.form(versionForm));
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }
}
