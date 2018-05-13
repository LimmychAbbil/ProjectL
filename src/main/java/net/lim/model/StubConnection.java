package net.lim.model;

/**
 * Connection to nothing, for debug purposes
 */
public class StubConnection extends Connection {
    @Override
    public boolean login(String userName, String password) {
        System.out.println("ПОПЫТКА АВТОРИЗОВАТЬСЯ: " + userName + " | " + password); //TODO авторизация
        return true;
    }
}
