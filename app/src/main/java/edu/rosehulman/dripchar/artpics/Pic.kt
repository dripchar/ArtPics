package edu.rosehulman.dripchar.artpics

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ServerTimestamp

data class Pic(
    var caption: String = "",
    var url: String = "",
    var uid : String = "",
    var isSelected: Boolean = false
   ): Parcelable {

    @ServerTimestamp
    var timeStamp: Timestamp? = null
    @get:Exclude var id = ""

    companion object CREATOR : Parcelable.Creator<Pic> {

        override fun createFromParcel(parcel: Parcel): Pic {
            return Pic(parcel)
        }

        override fun newArray(size: Int): Array<Pic?> {
            return arrayOfNulls(size)
        }

        fun fromSnapshot(snapshot: DocumentSnapshot) : Pic {
            val pic = snapshot.toObject(Pic:: class.java)!!
            //TODO
            pic.id = snapshot.id
            return pic
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(caption)
        parcel.writeString(url)
        parcel.writeString(uid)
    }

    override fun describeContents(): Int {
        return 0
    }



}