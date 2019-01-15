package net.lim.model.adv;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Limmy on 13.05.2018.
 */
public class StubAdvertisementReceiver implements AdvertisementReceiver {

    @Override
    public List<Advertisement> receiveAdvertisements(int maxNumber) {
        if (maxNumber < 0) {
            throw new IllegalArgumentException("Wrong number of news to recieve");
        }

        List<Advertisement> ads = new ArrayList<>();

        for (int i = 0; i < maxNumber; i++) {
            ads.add(new Advertisement("Advertisement " + i, "some text"));
        }
        try {
            ads.add(new Advertisement("Ad with link", "abs", new URL("https://google.com")));
        } catch (MalformedURLException e) {
//            e.printStackTrace();
        }
        return ads;
    }
}
