package com.onurcan.pangolin.ui.contracts

import android.content.Intent

interface LoginHelper {
    fun onLoginClick(loginType: LoginType)
    fun checkSilentSignIn()
    fun onDataReceived(requestCode: Int, resultCode: Int, data: Intent?)
    fun setCallback(loginHelperCallback: LoginHelperCallback)
    fun onLoginEmail(email: String, password: String, verifyCode: String)
    fun sendVerificationCode(email: String, buttonString: String, interval: Int)
}

interface LoginHelperCallback {
    fun onLoginSuccess(loginUserData: LoginUserData, loginUserInfoData: LoginUserInfoData)
    fun onLoginFail(errorMessage: String)
    fun updateUserDB(loginUserData: LoginUserData)
    fun updateUserInfoDB(loginUserInfoData: LoginUserInfoData)
    fun onSilentSignInFail()
    fun redirectToEmail()
    fun redirectToSignIn(signInIntent: Intent, requestCode: Int)
    fun onSilentSignInSuccess(loginUserData: LoginUserData)
}

enum class LoginType(val displayName: String) {
    GOOGLE_HUAWEI("Google or Huawei"),
    GOOGLE("Google"),
    HUAWEI("Huawei"),
    EMAIL("Email")
}

data class LoginUserData(
    val userId: String,
    val loginType: LoginType
)

data class LoginUserInfoData(
    val name: String,
    val email: String,
    val userId: String,
    val photoUrl: String,
    val memory:String,
    val profilePrivacy:String
)