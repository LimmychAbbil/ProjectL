package net.lim.controller;

import javafx.concurrent.Task;

public interface Controller {

    void init();

    default void startTask(Task<?> task) {
        Thread taskThread = new Thread(task);
        taskThread.setDaemon(true);
        taskThread.start();
    }
}
