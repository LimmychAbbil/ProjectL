package net.lim.model.connection;

import net.lim.model.adv.Advertisement;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class RestConnection extends Connection {

    private String url;

    public RestConnection(String url) {
        this.url = url;
    }

    @Override
    public boolean login(String userName, String password) {
        Client client = null;
        try {
            client = ClientBuilder.newClient();
            Form loginForm = new Form();
            loginForm.param("userName", userName);
            loginForm.param("pass", password);
            Response response = client.target(url + "/login").request().post(Entity.form(loginForm));
            return response.getStatus() == Response.Status.OK.getStatusCode();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Override
    public int sendRegistration(String userName, String password) {
        Client client = null;
        try {
            client = ClientBuilder.newClient();
            Form registrationForm = new Form();
            registrationForm.param("userName", userName);
            registrationForm.param("pass", password);
            Response response = client.target(url + "/register").request().post(Entity.form(registrationForm));
            return response.getStatus();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public JSONObject getFileServerInfo() {
        Client client = null;
        try {
            client = ClientBuilder.newClient();
            Response response = client.target(url + "/files/serverInfo").request().get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return getJsonFromResponse(response);
            } else {
                //TODO handle server error
                return new JSONObject();
            }
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Override
    public JSONArray getIgnoredFilesInfo() {
        Client client = null;
        try {
            client = ClientBuilder.newClient();
            Response response = client.target(url + "/files/ignoredFiles").request().get();
            JSONObject json = getJsonFromResponse(response);
            return (JSONArray) json.get("ignoredFiles");
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Override
    public JSONObject getFullHashInfo() {
        Client client = null;
        try {
            client = ClientBuilder.newClient();
            Response response = client.target(url + "/files/hash").request().get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return getJsonFromResponse(response);
            } else {
                //TODO handle server error
                return new JSONObject();
            }
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Override
    public boolean validateConnection() {
        Client client = null;
        try {
            client = ClientBuilder.newClient();
            Response response = client.target(url).request().get();
            return response.getStatus() == Response.Status.OK.getStatusCode();
        } finally {
             if (client != null) {
                 client.close();
             }
        }
    }

    @Override
    public boolean validateVersionSupported(String currentVersion) {
        Client client = null;
        try {
            client = ClientBuilder.newClient();
            Form versionForm = new Form();
            versionForm.param("version", currentVersion);
            Response response = client.target(url + "/versionCheck").request().post(Entity.form(versionForm));
            return response.getStatus() == Response.Status.OK.getStatusCode();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Override
    public JSONObject getServersInfoJSON() {
        Client client = null;
        try {
            client = ClientBuilder.newClient();
            Response response = client.target(url + "/servers").request().get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return getJsonFromResponse(response);
            }
        } catch (ProcessingException ignored) {
            //NOOP: client not connected to server
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }

    public String getBackgroundImageName() {
        Client client = null;
        try {
            client = ClientBuilder.newClient();
            Response response = client.target(url + "/images/current").request().get();
            JSONObject currentImageNameJSON = getJsonFromResponse(response);
            return (String) currentImageNameJSON.get("current.background");
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Override
    public List<Advertisement> getAdvs() {
        List<Advertisement> list = new ArrayList<>();
        Client client = null;
        try {
            client = ClientBuilder.newClient();
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
        } finally {
            if (client != null) {
                client.close();
            }
        }

        return list;
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
}
