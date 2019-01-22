package net.lim.controller.tasks;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.lim.model.connection.Connection;

/**
 * Service which create login tasks
 */
public class LoginService extends Service<Boolean> {

    private Connection connection;

    private String userName;
    private String password;

    public LoginService(Connection connection) {
        this.connection = connection;
    }

    public void start(String userName, String password) {
        this.userName = userName;
        this.password = password;
        super.start();
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                updateMessage("Login...");
                boolean loginSuccess = false;
                try {
                    loginSuccess = connection.login(userName, password);
                    if (!loginSuccess) {
                        updateMessage("Wrong user or password");
                    }
                } catch (Exception e) {
                    updateMessage("Can't connect: " + e.getMessage());
                }
                return loginSuccess;
            }
        };
    }
}
