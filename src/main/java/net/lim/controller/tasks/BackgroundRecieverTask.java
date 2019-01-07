package net.lim.controller.tasks;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class BackgroundRecieverTask extends Service {

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                return null;
            }
        };
    }
}
