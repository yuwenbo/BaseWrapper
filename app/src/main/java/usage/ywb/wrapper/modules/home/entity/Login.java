package usage.ywb.wrapper.modules.home.entity;

import com.google.gson.annotations.SerializedName;

import usage.ywb.wrapper.base.entity.ConvertEntity;

/**
 * @author yuwenbo
 * @version [ V.2.7.1  2019/12/11 ]
 */
public class Login extends ConvertEntity<User> {

    @SerializedName("REP_SESSION_TOKEN")
    public String token;

    /**
     * 下载URL
     */
    @SerializedName("APKURL")
    public String apkUrl;

    @SerializedName("VERSION")
    public String version;//当前最新版本


}
