package net.lim.model;

/**
 * Connection to nothing, for debug purposes
 */
public class StubConnection extends Connection {
    @Override
    public boolean login(String userName, String password) {
        return true;
    }

    @Override
    public void sendRegistration(String userName, String password) {
        //nothing to do, it's a stub
    }
}
