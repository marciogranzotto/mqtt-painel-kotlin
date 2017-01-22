package com.granzotto.mqttpainel.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by marciogranzotto on 21/01/17.
 */
class ConnectionObj(val serverUrl: String, val serverPort: String, val user: String? = null, val password: String? = null) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ConnectionObj> = object : Parcelable.Creator<ConnectionObj> {
            override fun createFromParcel(source: Parcel): ConnectionObj = ConnectionObj(source)
            override fun newArray(size: Int): Array<ConnectionObj?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readString(), source.readString(), source.readString(), source.readString())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(serverUrl)
        dest?.writeString(serverPort)
        dest?.writeString(user)
        dest?.writeString(password)
    }
}