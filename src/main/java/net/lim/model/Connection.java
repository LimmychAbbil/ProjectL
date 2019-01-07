package net.lim.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;

/**
 * Created by Limmy on 02.05.2018.
 */
public abstract class Connection {
    public abstract boolean validateConnection();

    public abstract boolean validateVersionSupported(String currentVersion);

    public abstract boolean login(String userName, String password);

    public abstract int sendRegistration(String userName, String password);

    public abstract JSONObject getFileServerInfo();

    public abstract JSONArray getIgnoredDirsInfo();

    public abstract JSONObject getFullHashInfo();

    public abstract JSONObject getServersInfoJSON();

    public abstract File getBackgroundImage();

    public static String getErrorMessage(int code) {
        switch (code) {
            case 200:
                return "";
            case 507:
                return "Имя пользователя занято";
            case 508:
                return "Вам запрещено регистрироваться";
            default:
                return "Неизвестная ошибка";
        }
    }
}
