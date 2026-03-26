package com.yundou.loans.utils

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

object PermissionConstants {
    val CONTACTS = arrayOf(Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS)
    val RECORDPHONE = arrayOf(Manifest.permission.READ_PHONE_STATE)
    val PHONE = arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.USE_SIP, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.ADD_VOICEMAIL)
    val CALENDAR = arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
    val CAMERA = arrayOf(Manifest.permission.CAMERA,)
    val SENSORS = mutableListOf(Manifest.permission.BODY_SENSORS)
    val LOCATION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE)
    val MICROPHONE = arrayOf(Manifest.permission.RECORD_AUDIO)
    val SMS = arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS)
    @RequiresApi(Build.VERSION_CODES.Q)
    val RECOGNITION = arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)

    fun LOCATION_ARRAY(): Array<String> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACTIVITY_RECOGNITION,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
}