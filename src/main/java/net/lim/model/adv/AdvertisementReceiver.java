package net.lim.model.adv;

import java.util.List;

/**
 * Created by Limmy on 13.05.2018.
 */
public interface AdvertisementReceiver {
    List<Advertisement> recieveAdvertisements();
    List<Advertisement> recieveAdvertisements(int maxNumber);
}
