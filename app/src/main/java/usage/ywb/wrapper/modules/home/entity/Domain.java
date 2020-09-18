package usage.ywb.wrapper.modules.home.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author yuwenbo
 * @version [ V.2.4.4  2019/3/29 ]
 */
public class Domain implements Parcelable {

    @SerializedName("DOMAIN")
    public String domain;//域名

    protected Domain(Parcel in) {
        domain = in.readString();
    }

    public static final Creator<Domain> CREATOR = new Creator<Domain>() {
        @Override
        public Domain createFromParcel(Parcel in) {
            return new Domain(in);
        }

        @Override
        public Domain[] newArray(int size) {
            return new Domain[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(domain);
    }
}
