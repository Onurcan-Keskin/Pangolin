package com.onurcan.pangolin.helpers

import android.content.Context
import android.os.Environment
import com.onurcan.pangolin.R
import com.onurcan.pangolin.appuser.AppUser
import com.onurcan.pangolin.models.FirebaseDBObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Constants {

    // -----------------
    //  profilePrivacy
    //
    // [0 0 0]
    //
    // get.index[0] = See my friends
    // get.index[1] = See my memory
    // get.index[2] = See my photo
    // -----------------

    companion object{
        /* Firebase References */
        val fUserInfoRef = FirebaseDBObject.getUserInfo(AppUser.getUserId())
        val fOnlineDBRef = FirebaseDBObject.getUser(AppUser.getUserId())
        val fSharedRef = FirebaseDBObject.getShared()

        /* Timer */
        const val MS_TO_SECOND = 1000
        const val SECOND_TO_MINUTE = 60
        const val SECOND_TO_HOUR = 60 * 60

        /* Recording Video */
        const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS" //"yyyy-MM-dd-HH-mm-ss-SSS"
        const val VIDEO_EXTENSION = ".mp4"
        const val IMAGE_EXTENSION = ".jpeg"
        const val USE_FRAME_PROCESSOR = true
        const val DECODE_BITMAP = false
        var recPath = Environment.getExternalStorageDirectory().path + "/Pictures/${R.string.app_name}"

        /* OneSignal */
        const val oneSignalAppID="b904f59b-8ac6-461e-a697-15758657212c"

        /* AGCErrorCode */
        const val SUCCESS_CODE = 200
        const val ERROR_CODE_AGC_USER_NOT_FOUND = 80001
        const val ERROR_GOOGLE_TOKEN_VALUE_NULL = 80002
        const val ERROR_PROFILE_JSON_EXCEPTION = 80003
        const val ERROR_GOOGLE_TOKEN_EXCEPTION = 80004
        const val ERROR_AGC_GOOGLE_SIGNIN = 80006
        const val ERROR_HUAWEI_LOGIN_FAILED = 80009
        const val ERROR_HUAWEI_TOKEN_FAILED = 80010
        const val ERROR_EMAIL_AUTH_FAILED = 80012
        const val ERROR_HUAWEI_LOGIN_CANCEL = 80013
        const val ERROR_GOOGLE_LOGIN_CANCEL = 80014

        const val AGC_USER_NOT_FOUND = "AGC user not found"
        const val AGC_USER_LOGIN_SUCCESS = "Successfully login into AGC"
        const val GOOGLE_TOKEN_VALUE_NULL = "Google token value is null"
        const val FACEBOOK_LOGIN_CANCEL = "Facebook login cancelled"
        const val HUAWEI_LOGIN_CANCEL = "Huawei login cancelled"
        const val GOOGLE_LOGIN_CANCEL = "Google login cancelled"
        const val ALREADY_REGISTERED = "been registered"

        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(
                    recPath
                ).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists()) mediaDir else appContext.filesDir
        }

        fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.ROOT)
                    .format(System.currentTimeMillis()) + extension
            )
    }
}