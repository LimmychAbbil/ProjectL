package net.lim.controller.tasks.ui;

import javafx.concurrent.Task;
import javafx.scene.image.ImageView;

public class RotateStatusBarTask extends Task<Void> {

    private final ImageView statusBarImageView;

    public RotateStatusBarTask(ImageView statusBarImageView) {
        this.statusBarImageView = statusBarImageView;
    }

    @Override
    protected Void call() throws Exception {
        rotateStatusIcon();
        return null;
    }

    public void rotateStatusIcon() {
        double d = 0;
        try {
            while (!isCancelled()) {
                d+=1;
                statusBarImageView.setRotate(d);
                Thread.sleep(75);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
