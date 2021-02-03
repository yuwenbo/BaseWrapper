package usage.ywb.wrapper.modules.home.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class User() : Parcelable {
    @JvmField
    @SerializedName("PASSWORD")
    var password: String? = null

    @JvmField
    @SerializedName("USERNAME")
    var userName: String? = null

    constructor(parcel: Parcel) : this() {
        password = parcel.readString()
        userName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(password)
        parcel.writeString(userName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }


}