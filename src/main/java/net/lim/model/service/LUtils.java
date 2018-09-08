package net.lim.model.service;

public class LUtils {
    /**
     * User Name should be 3-15 character long, start from latin letter and contain only letters, digits and _
     * @param userName string username set by user
     * @return is user name valid
     */
    public static boolean isNotValidUserName(String userName) {
        return userName != null && userName.matches("^[a-zA-Z]+[a-zA-Z0-9_]{1,13}[a-zA-Z0-9]$");
    }
}
