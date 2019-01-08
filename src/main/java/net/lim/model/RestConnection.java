package net.lim.model;

import net.lim.LLauncher;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.ConnectException;
import java.util.List;

import static net.lim.model.FileManager.DEFAULT_DIRECTORY;

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
            return response.getStatus() == 200;
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
            return getJsonFromResponse(response);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Override
    public JSONArray getIgnoredDirsInfo() {
        Client client = null;
        try {
            client = ClientBuilder.newClient();
            Response response = client.target(url + "/files/ignoredDirs").request().get();
            JSONObject json = getJsonFromResponse(response);
            return (JSONArray) json.get("ignoredDirs");
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
            return getJsonFromResponse(response);
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
        return readBackgroundImageName();
    }

    private void downloadBackgroundImage(String imageName, File image) {
        Client client = null;
        try {
            client = ClientBuilder.newClient();
            Response response = client.target(url + "/images/get/" + imageName).request().get();
            InputStream is = response.readEntity(InputStream.class);
            if (is != null) {
                FileOutputStream fos = new FileOutputStream(image);
                while (is.available() > 0) {
                    fos.write(is.read());
                }
                is.close();
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }

    }

    private String readBackgroundImageName() {
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
