package usage.ywb.wrapper.base.entity;

import com.google.gson.annotations.SerializedName;

/**
 * @author yuwenbo
 * @version [ V.2.7.1  2019/12/12 ]
 */
public class Entity {

    @SerializedName("RESULTCODE")
    public int resultCode;

    @SerializedName("RESULT")
    public int result;

    @SerializedName("RESULTDESC")
    public String resultDesc;

}
