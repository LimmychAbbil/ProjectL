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

    @Override
    public void sendRegistration(String userName, String password) {
        System.out.println("Отправка на сервер регистрационной формы... Логин: " + userName + ", пароль: " + password);
    }
}
