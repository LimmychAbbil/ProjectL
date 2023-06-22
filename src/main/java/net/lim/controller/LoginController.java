package net.lim.controller;

import net.lim.controller.tasks.LoginService;
import net.lim.model.ServerInfo;
import net.lim.view.LoginPane;
import net.lim.view.RegistrationPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoginController implements Controller {

    private ServerInfo selectedServer;

    private StageController stageController;
    private RegistrationController registrationController;
    private LoginService loginService;

    public LoginController(StageController stageController) {
        this.stageController = stageController;
        this.registrationController = new RegistrationController();
    }

    @Override
    public void init() {
        registrationController.init();
        this.loginService = new LoginService(ConnectionController.getInstance().getConnection());

    }

    public void serverSelected(Object selectedServer) {
        if (selectedServer instanceof ServerInfo) {
            this.selectedServer = (ServerInfo) selectedServer;
        }
    }

    public void loginButtonPressed(String userName, String password) {
        stageController.getOrCreateBasicView().getProgressView().setVisible(true);
        createLoginTask(userName, password);
    }

    public void registrationButtonPressed(RegistrationPane registrationPane) {
        registrationPane.setVisible(true);
    }

    public List<ServerInfo> retrieveServerList() {
        if (ConnectionController.getInstance().getConnection() == null) return Collections.emptyList();
        JSONObject serversInfoJSON = ConnectionController.getInstance().getConnection().getServersInfoJSON();
        if (serversInfoJSON == null) {
            return Collections.emptyList();
        }
        List<ServerInfo> serverInfoList = new ArrayList<>();
        JSONArray serversInfoArray = (JSONArray) serversInfoJSON.get("Servers");
        for (Object serverInfoJSONObject : serversInfoArray) {
            JSONObject serverInfoJSON = (JSONObject) serverInfoJSONObject;
            String serverName = (String) serverInfoJSON.get("serverName");
            String serverDescription = (String) serverInfoJSON.get("serverDescription");
            String serverIPPort = (String) serverInfoJSON.get("serverIP");

            serverInfoList.add(new ServerInfo(serverName, serverDescription, serverIPPort.split(":")[0], Integer.parseInt(serverIPPort.split(":")[1])));
        }

        return serverInfoList;
    }

    public ServerInfo getSelectedServer() {
        return selectedServer;
    }

    private String getServerURL() {
        if (selectedServer != null) {
            return selectedServer.getIp() + ":" + selectedServer.getPort();
        }
        return ""; //offline connection
    }

    private void createLoginTask(String userName, String password) {
        loginService.start(userName, password);
        stageController.getOrCreateBasicView().getProgressView().getTextMessageProperty().bind(loginService.messageProperty());
        loginService.setOnSucceeded(e -> {
                    boolean loginSuccess = loginService.getValue();

                    if (loginSuccess) {
                        stageController.getLauncherController().startFileChecking(userName);
                    } else {
                        startTask(stageController.createWaitingTask(5000));
                    }
                }
        );
    }


    public RegistrationController getOrCreateRegistrationController() {
        if (registrationController == null) {
            registrationController = new RegistrationController();
        }
        return registrationController;
    }
}
