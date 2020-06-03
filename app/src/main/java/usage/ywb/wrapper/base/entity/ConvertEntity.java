package usage.ywb.wrapper.base.entity;

import com.google.gson.annotations.SerializedName;

/**
 * @author yuwenbo
 * @version [ V.2.7.1  2019/12/17 ]
 */
public class ConvertEntity<T> extends Entity {

    @SerializedName("RESULTDATA")
    public T resultData;

}
