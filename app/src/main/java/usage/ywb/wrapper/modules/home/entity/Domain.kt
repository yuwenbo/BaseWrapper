package usage.ywb.wrapper.modules.home.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * @author yuwenbo
 * @version [ V.2.4.4  2019/3/29 ]
 */
class Domain protected constructor(`in`: Parcel) : Parcelable {
    @JvmField
    @SerializedName("DOMAIN")
    var domain //域名
            : String?

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(domain)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Domain> = object : Parcelable.Creator<Domain> {
            override fun createFromParcel(`in`: Parcel): Domain? {
                return Domain(`in`)
            }

            override fun newArray(size: Int): Array<Domain?> {
                return arrayOfNulls(size)
            }
        }
    }

    init {
        domain = `in`.readString()
    }
}