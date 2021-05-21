package com.onurcan.pangolin.ui.fragments

import login.HmsGmsLoginHelper
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.onurcan.pangolin.ui.mvp.BaseFragment
import com.onurcan.pangolin.R
import com.onurcan.pangolin.databinding.FragmentOnboarding3Binding
import com.onurcan.pangolin.ui.activities.MainActivity
import com.onurcan.pangolin.ui.contracts.LoginHelper
import com.onurcan.pangolin.ui.presenters.LoginContract
import com.onurcan.pangolin.ui.presenters.LoginPresenter

class Onboarding3Fragment : BaseFragment(R.layout.fragment_onboarding3), LoginContract {

    private lateinit var binding: FragmentOnboarding3Binding
    private lateinit var context: FragmentActivity

    private val hgLoginHelper: LoginHelper by lazy {
        HmsGmsLoginHelper(context)
    }

    private val helperBuild: HmsGmsLoginHelper by lazy {
        HmsGmsLoginHelper(context)
    }

    private val presenter: LoginPresenter by lazy { LoginPresenter(this, hgLoginHelper) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = requireActivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOnboarding3Binding.bind(view)
        presenter.onViewsCreate()

        binding.onboard3OpenDialogBt.setOnClickListener {
            initDialog(R.style.DialogSlide)
        }
    }

    fun initDialog(type: Int) {
        val dialog = Dialog(context, R.style.BlurTheme)
        dialog.window!!.attributes.windowAnimations = type
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.setContentView(R.layout.dialog_signin)
        dialog.setCanceledOnTouchOutside(true)

        val dGHBtn = dialog.findViewById<View>(R.id.login_btn)

        dGHBtn.setOnClickListener {
            dialog.dismiss()
            presenter.onSignInGHClick()
        }

        dialog.show()
    }

    override fun redirectToMain() {
        startActivity(
            Intent(
                context,
                MainActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    override fun restartApp() {
        context.recreate()
    }

    override fun redirectToSignIn(signInIntent: Intent, requestCode: Int) {
        startActivityForResult(signInIntent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        hgLoginHelper.onDataReceived(requestCode, resultCode, data)
    }

}