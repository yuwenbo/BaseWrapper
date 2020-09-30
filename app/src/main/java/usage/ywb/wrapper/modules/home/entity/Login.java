package usage.ywb.wrapper.modules.home.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import usage.ywb.wrapper.mvp.ui.base.entity.ConvertEntity;

/**
 * @author yuwenbo
 * @version [ V.2.7.1  2019/12/11 ]
 */
public class Login extends ConvertEntity<User> implements Parcelable {

    @SerializedName("REP_SESSION_TOKEN")
    public String token;

    /**
     * 下载URL
     */
    @SerializedName("APKURL")
    public String apkUrl;

    @SerializedName("VERSION")
    public String version;//当前最新版本

    public Domain domain;


    protected Login(Parcel in) {
        token = in.readString();
        apkUrl = in.readString();
        version = in.readString();
        domain = in.readParcelable(Domain.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeString(apkUrl);
        dest.writeString(version);
        dest.writeParcelable(domain, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Login> CREATOR = new Creator<Login>() {
        @Override
        public Login createFromParcel(Parcel in) {
            return new Login(in);
        }

        @Override
        public Login[] newArray(int size) {
            return new Login[size];
        }
    };
}
