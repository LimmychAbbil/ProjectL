package net.lim.model.connection;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;
import net.lim.controller.LauncherController;
import net.lim.model.ServerInfo;
import net.lim.model.adv.Advertisement;
import net.lim.service.ConfigReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RestConnection extends Connection {

    private final String url;

    public RestConnection(String url) {
        this.url = url;
    }

    @Override
    public boolean login(String userName, String password) {
        try (Client client = createClient()){
            Form loginForm = new Form();
            loginForm.param("userName", userName);
            loginForm.param("pass", password);
            Response response = client.target(url + "/login").request().post(Entity.form(loginForm));
            LauncherController.token = (String) getJsonFromResponse(response).get("tokenHash");

            return response.getStatus() == Response.Status.OK.getStatusCode();
        }
    }

    @Override
    public int sendRegistration(String userName, String password) {
        try (Client client = createClient()){
            Form registrationForm = new Form();
            registrationForm.param("userName", userName);
            registrationForm.param("pass", password);
            Response response = client.target(url + "/register").request().post(Entity.form(registrationForm));
            return response.getStatus();
        }
    }

    public JSONObject getFileServerInfo() {
        try (Client client = createClient()){
            Response response = client.target(url + "/files/serverInfo").request().get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return getJsonFromResponse(response);
            } else {
                //TODO handle server error
                return new JSONObject();
            }
        }
    }

    @Override
    public JSONArray getIgnoredFilesInfo() {
        try (Client client = createClient()){
            Response response = client.target(url + "/files/ignoredFiles").request().get();
            JSONObject json = getJsonFromResponse(response);
            return (JSONArray) json.get("ignoredFiles");
        }
    }

    @Override
    public JSONObject getFullHashInfo() {
        try (Client client = createClient()) {
            Response response = client.target(url + "/files/hash").request().get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return getJsonFromResponse(response);
            } else {
                //TODO handle server error
                return new JSONObject();
            }
        }
    }

    @Override
    public boolean validateConnection() {
        URI uri;
        try {
            uri = new URI(url);

        } catch (URISyntaxException e) {
            System.err.println("Can't validate connection, invalid URI: " + e.getMessage());
            return false;
        }
        if (!uri.isAbsolute()) {
            return false;
        }
        try (Client client = createClient()) {
            Response response = client.target(uri).request().get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                super.closed = false;
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.err.println("Can't establish a connection: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validateVersionSupported(String currentVersion) {
        try (Client client = createClient()){
            Form versionForm = new Form();
            versionForm.param("version", currentVersion);
            Response response = client.target(url + "/versionCheck").request().post(Entity.form(versionForm));
            return response.getStatus() == Response.Status.OK.getStatusCode();
        }
    }

    @Override
    public JSONObject getServersInfoJSON() {
        try(Client client = createClient()) {
            Response response = client.target(url + "/servers").request().get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return getJsonFromResponse(response);
            }
        } catch (ProcessingException ignored) {
            //NOOP: client not connected to server
        }
        return null;
    }

    public String getBackgroundImageName() {

        try (Client client = createClient()) {
            Response response = client.target(url + "/images/current").request().get();
            JSONObject currentImageNameJSON = getJsonFromResponse(response);
            return (String) currentImageNameJSON.get("current.background");
        }
    }

    @Override
    public List<Advertisement> getAdvs() {
        List<Advertisement> list = new ArrayList<>();

        try (Client client = createClient()) {
            Response response = client.target(url + "/adv").request().get();
            JSONObject advJSON = getJsonFromResponse(response);
            JSONArray allAdvs = (JSONArray) advJSON.get("Advertisements");
            for (Object adv: allAdvs) {
                String advString = (String) adv;
                String[] advParts = advString.split(";");
                Advertisement advertisement;
                 if (advParts.length == 2) {
                    advertisement = new Advertisement(advParts[0], advParts[1]);
                } else if (advParts.length == 3) {
                    try {
                        advertisement = new Advertisement(advParts[0], advParts[1], URI.create(advParts[2]).toURL());
                    } catch (MalformedURLException e) {
                        advertisement = new Advertisement(advParts[0], advParts[1]);
                    }
                } else {
                    continue;
                }

                list.add(advertisement);
            }
        }

        return list;
    }

    @Override
    public String getServerLaunchCommand(ServerInfo selectedServer) {
        if (selectedServer == null || selectedServer == ServerInfo.OFFLINE) {
            return LauncherController.DEFAULT_COMMAND;
        }
        try (Client client = createClient()) {

            Response response = client.target(url + "/servers/startupCommand")
                    .queryParam("serverName", selectedServer.getServerName()).request().get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                JSONObject commandJson = getJsonFromResponse(response);

                return (String) commandJson.get(selectedServer.getServerName());
            }

            return ""; //TODO something instead of empty string to inform user about incorrect server configuration?
        }
    }

    @Override
    public String getRulesURL() {
        return "http://google.com"; //TODO
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

    private Client createClient() {
        ClientBuilder builder = ClientBuilder.newBuilder();
        String timeoutStringInConfigFile = ConfigReader.getProperties().getProperty("connection.timeout");
        if (timeoutStringInConfigFile != null) {
            builder.connectTimeout(Long.parseLong(timeoutStringInConfigFile), TimeUnit.SECONDS);
            builder.readTimeout(Long.parseLong(timeoutStringInConfigFile), TimeUnit.SECONDS);
        }

        return builder.build();
    }
}
