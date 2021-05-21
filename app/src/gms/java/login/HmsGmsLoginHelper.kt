package login

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.exovideoreference.utils.showLogInfo
import com.onurcan.exovideoreference.utils.showToast
import com.onurcan.pangolin.utils.toSafeString
import com.onurcan.pangolin.R
import com.onurcan.pangolin.ui.contracts.*

class HmsGmsLoginHelper(context: Context) : LoginHelper {

    private val googleRequestCode = 1994
    private val cntx = context

    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestId()
            .requestEmail()
            .build()
    }

    private lateinit var loginHelperCallback: LoginHelperCallback

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(context, gso)
    }

    override fun onLoginClick(loginType: LoginType) {
        when (loginType) {
            LoginType.GOOGLE_HUAWEI -> onGoogleLogInClick()
            LoginType.EMAIL -> onEmailLogInClick()
            else -> onGoogleLogInClick()
        }
    }

    private fun onGoogleLogInClick() {
        loginHelperCallback.redirectToSignIn(googleSignInClient.signInIntent, googleRequestCode)
    }

    private fun onEmailLogInClick() {
        loginHelperCallback.redirectToEmail()
    }

    override fun checkSilentSignIn() {
        checkGoogleSilentSignIn()
    }

    private fun checkGoogleSilentSignIn() {
        googleSignInClient.silentSignIn().addOnSuccessListener { googleAccount ->
            firebaseAuthWithGoogle(googleAccount, isSilentLogin = true)
        }.addOnCanceledListener {
            loginHelperCallback.onSilentSignInFail()
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount, isSilentLogin: Boolean = false) {
        val auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val loginUserData = LoginUserData(
                    userId = user?.uid.toSafeString(),
                    loginType = LoginType.GOOGLE
                )
                val loginUserInfoData = LoginUserInfoData(
                    name = user?.displayName.toSafeString(),
                    email = user?.email.toSafeString(),
                    photoUrl = user?.photoUrl.toString(),
                    userId = user?.uid.toSafeString(),
                    memory = "I'am using Pangolin",
                    profilePrivacy = "000"
                )
                showLogInfo(this.javaClass.simpleName, "SignInWithCredential:success")
                if (isSilentLogin)
                    loginHelperCallback.onSilentSignInSuccess(loginUserData)
                else
                    loginHelperCallback.onLoginSuccess(loginUserData, loginUserInfoData)
            } else {
                loginHelperCallback.onLoginFail("Firebase Google Auth Fail")
            }
        }
    }

    override fun onDataReceived(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            googleRequestCode -> onGoogleSignInDataReceived(data)
        }
    }

    override fun setCallback(loginHelperCallback: LoginHelperCallback) {
        this.loginHelperCallback = loginHelperCallback
    }

    override fun onLoginEmail(email: String, password: String, verifyCode: String) {
        showLogInfo(this.javaClass.simpleName,verifyCode)
    }

    override fun sendVerificationCode(email: String, buttonString: String, interval: Int) {
        val btn = buttonString
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                showToast(cntx, "Please wait verification code, and then type it and press Login")
            }
        }
    }

    private fun onGoogleSignInDataReceived(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account!!)
        } catch (e: ApiException) {
            showLogError(this.javaClass.simpleName, "Google SignIn Failed: $e")
        }
    }
}