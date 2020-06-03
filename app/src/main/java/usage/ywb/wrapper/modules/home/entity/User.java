package usage.ywb.wrapper.modules.home.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("PASSWORD")
    public String password;

    @SerializedName("USERNAME")
    public String userName;

}
