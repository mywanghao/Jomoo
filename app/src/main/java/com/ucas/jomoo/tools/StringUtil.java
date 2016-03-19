package com.ucas.jomoo.tools;

/**
 * Created by Dalink on 15/10/21.
 */
public class StringUtil {
    public static String verifyPassword(String pwd, String pwdName) {

        if (pwd == null || pwd.length() == 0) {
            return "请输入" + pwdName;
        }
        if (pwd.length() != 4) {
            return "您输入的" + pwdName + "位数不正确";
        }
        if (!pwd.matches("^\\d{4}$")) {
            return pwdName + "必须为四位数字";
        }
        return null;
    }
}
