package net.lim.model;

/**
 * Created by Limmy on 02.05.2018.
 */
public abstract class Connection {
    public abstract boolean login(String userName, String password);
    //TODO add parameters
    public abstract void sendRegistration(String userName, String password);
}
