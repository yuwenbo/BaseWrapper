package usage.ywb.wrapper.modules.home.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import usage.ywb.wrapper.mvp.common.entity.ConvertEntity

/**
 * @author yuwenbo
 * @version [ V.2.7.1  2019/12/11 ]
 */
class Login protected constructor(`in`: Parcel) : ConvertEntity<User?>(), Parcelable {
    @JvmField
    @SerializedName("REP_SESSION_TOKEN")
    var token: String?

    /**
     * 下载URL
     */
    @SerializedName("APKURL")
    var apkUrl: String?

    @SerializedName("VERSION")
    var version //当前最新版本
            : String?
    var domain: Domain?
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(token)
        dest.writeString(apkUrl)
        dest.writeString(version)
        dest.writeParcelable(domain, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<Login> = object : Parcelable.Creator<Login> {
            override fun createFromParcel(`in`: Parcel): Login? {
                return Login(`in`)
            }

            override fun newArray(size: Int): Array<Login?> {
                return arrayOfNulls(size)
            }
        }
    }

    init {
        token = `in`.readString()
        apkUrl = `in`.readString()
        version = `in`.readString()
        domain = `in`.readParcelable(Domain::class.java.classLoader)
    }
}