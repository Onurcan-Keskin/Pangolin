package com.onurcan.pangolin.ui.presenters

import android.content.Intent
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.exovideoreference.utils.showLogInfo
import com.onurcan.pangolin.utils.toSafeString
import com.onurcan.pangolin.appuser.AppUser
import com.onurcan.pangolin.models.FirebaseDBObject
import com.onurcan.pangolin.ui.contracts.*

class LoginPresenter constructor(
    private val loginContract: LoginContract,
    private val iLoginHelper: LoginHelper,
) : LoginHelperCallback {

    fun onViewsCreate() {
        iLoginHelper.setCallback(this)
        iLoginHelper.checkSilentSignIn()
    }

    fun onEmailClick() {
        iLoginHelper.setCallback(this)
    }

    fun onSignInGHClick() {
        iLoginHelper.onLoginClick(LoginType.GOOGLE_HUAWEI)
    }

    override fun onLoginSuccess(
        loginUserData: LoginUserData,
        loginUserInfoData: LoginUserInfoData
    ) {
        iLoginHelper.setCallback(this)
        AppUser.setUserId(loginUserData.userId)
        updateUserDB(loginUserData)
        updateUserInfoDB(loginUserInfoData)
    }

    override fun onLoginFail(errorMessage: String) {
        showLogError(this.javaClass.simpleName, errorMessage)
    }

    override fun updateUserDB(loginUserData: LoginUserData) {
        val userDbRef = FirebaseDBObject.getUser(loginUserData.userId)
        val userMap = HashMap<String, String>()
        userMap["tokenID"] = loginUserData.userId
        userMap["signInMethod"] = loginUserData.loginType.displayName

        userDbRef.setValue(userMap).addOnCompleteListener {
            if (it.isSuccessful)
                loginContract.redirectToMain()
            else
                showLogError(this.javaClass.simpleName, "on update fail")
        }
    }


    override fun updateUserInfoDB(loginUserInfoData: LoginUserInfoData) {
        val userInfoDbRef = FirebaseDBObject.getUserInfo(loginUserInfoData.userId)
        val userInfoMap = HashMap<String, String>()
        userInfoMap["email"] = loginUserInfoData.email.toSafeString()
        userInfoMap["name"] = loginUserInfoData.name.toSafeString()
        userInfoMap["photoUrl"] = loginUserInfoData.photoUrl.toSafeString()
        userInfoMap["userID"] = loginUserInfoData.userId.toSafeString()
        userInfoMap["memory"] = loginUserInfoData.memory.toSafeString()
        userInfoMap["profilePrivacy"] = loginUserInfoData.profilePrivacy.toSafeString()

        userInfoDbRef.setValue(userInfoMap).addOnCompleteListener {
            if (it.isSuccessful)
                showLogInfo(this.javaClass.simpleName, "on update success")
            else
                showLogError(this.javaClass.simpleName, "on update fail")
        }
    }

    override fun onSilentSignInFail() {
        showLogError(this.javaClass.simpleName, "Sign failed")
    }

    override fun redirectToEmail() {
        showLogInfo(this.javaClass.simpleName, "Redirecting")
    }

    override fun redirectToSignIn(signInIntent: Intent, requestCode: Int) {
        loginContract.redirectToSignIn(signInIntent, requestCode)
    }

    override fun onSilentSignInSuccess(loginUserData: LoginUserData) {
        AppUser.setUserId(loginUserData.userId)
        loginContract.redirectToMain()
    }

}

interface LoginContract {
    fun redirectToMain()
    fun restartApp()
    fun redirectToSignIn(signInIntent: Intent, requestCode: Int)
}