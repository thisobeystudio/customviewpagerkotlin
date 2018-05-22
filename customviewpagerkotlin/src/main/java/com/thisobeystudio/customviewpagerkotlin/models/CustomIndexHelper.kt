package com.thisobeystudio.customviewpagerkotlin.models

/*
 * Created by Endika Aguilera on 20/5/18.
 * Copyright: (c) 2018 Endika Aguilera
 * Contact: thisobeystudio@gmail.com
 */

import android.os.Parcel
import android.os.Parcelable

/**
 * A simple class to store helper data.
 * **Useful methods:**
 * [.getPagerPosition]
 * [.getDataPosition]
 * [.isRealFirst]
 * [.isRealLast]
 * [.isHelperFirst]
 * [.isHelperLast]
 */

// todo test this as object
data class CustomIndexHelper(
        val pagerPosition: Int,
        val dataPosition: Int,
        val isRealFirst: Boolean,
        val isRealLast: Boolean,
        val isHelperFirst: Boolean,
        val isHelperLast: Boolean) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(pagerPosition)
        parcel.writeInt(dataPosition)
        parcel.writeByte(if (isRealFirst) 1 else 0)
        parcel.writeByte(if (isRealLast) 1 else 0)
        parcel.writeByte(if (isHelperFirst) 1 else 0)
        parcel.writeByte(if (isHelperLast) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomIndexHelper> {
        override fun createFromParcel(parcel: Parcel): CustomIndexHelper {
            return CustomIndexHelper(parcel)
        }

        override fun newArray(size: Int): Array<CustomIndexHelper?> {
            return arrayOfNulls(size)
        }
    }


}