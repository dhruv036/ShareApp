package com.dhruv.fillsharing.util

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var uid:String ="",
    var phone:String="",
    var password:String="",
    var name:String=""
    ): Parcelable