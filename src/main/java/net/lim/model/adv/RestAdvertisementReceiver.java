package net.lim.model.adv;

import net.lim.model.connection.Connection;

import java.util.List;

public class RestAdvertisementReceiver implements AdvertisementReceiver {
    private Connection connection;

    public RestAdvertisementReceiver(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Advertisement> receiveAdvertisements(int maxNumber) {
        List<Advertisement> list = connection.getAdvs();
        if (list.size() > maxNumber) {
            return list.subList(0, maxNumber);
        } else {
            return list;
        }
    }
}
