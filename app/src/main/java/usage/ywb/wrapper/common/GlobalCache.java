package usage.ywb.wrapper.common;


import usage.ywb.wrapper.modules.home.entity.User;

/**
 * 全局共享数据缓存
 *
 * @author yuwenbo
 * @version [ V.2.1.9  2018/6/26 ]
 */
public class GlobalCache {

    private static User runningUser;

    private static String dynamicDomain;//动态域名

    private static String token;


    public static void setLoginRunningUser(User user) {
        runningUser = user;
    }

    public static User getLoginRunningUser() {
        return runningUser;
    }


    //根据服务器类型获取所有的接口相同部分的完整地址
    public static String getRootPath() {
        return Key.PROTOCOL + dynamicDomain + "/" + Key.PATH;
    }

    public static void setDomain(String domain) {
        dynamicDomain = domain;
    }

    public static String getDomain() {
        return dynamicDomain;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        GlobalCache.token = token;
    }
}
