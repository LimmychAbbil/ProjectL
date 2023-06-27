package net.lim.controller;

import javafx.scene.control.ScrollPane;
import net.lim.LLauncher;
import net.lim.model.adv.Advertisement;
import net.lim.model.adv.AdvertisementReceiver;
import net.lim.model.adv.RestAdvertisementReceiver;
import net.lim.view.NewsPane;

import java.net.URL;
import java.util.List;

public class NewsController implements Controller {

    @Override
    public void init() {

    }

    public void hideNewsButtonPressed(ScrollPane pane) {
        pane.setVisible(!pane.isVisible());
    }

    public void fillNewsFlow(NewsPane newsPane) {
        if (ConnectionController.getInstance().getConnection() != null) {
            AdvertisementReceiver advertisementReceiver = new RestAdvertisementReceiver(ConnectionController.getInstance().getConnection());
            List<Advertisement> allAds = advertisementReceiver.receiveAdvertisements();
            newsPane.clearTextFlow();

            for (Advertisement ad : allAds) {
                newsPane.putNewToArea(ad);
            }
        }
    }

    public void linkPressed(URL url) {
        LLauncher.getFXHostServices().showDocument(url.toString());
    }
}
