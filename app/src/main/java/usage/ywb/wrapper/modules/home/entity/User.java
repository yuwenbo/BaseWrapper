package usage.ywb.wrapper.modules.home.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class User implements Parcelable {

    @SerializedName("PASSWORD")
    public String password;

    @SerializedName("USERNAME")
    public String userName;

    public User() {

    }

    public User(Parcel in) {
        password = in.readString();
        userName = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(password);
        dest.writeString(userName);
    }
}
