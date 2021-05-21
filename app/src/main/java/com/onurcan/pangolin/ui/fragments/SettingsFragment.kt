package com.onurcan.pangolin.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.FragmentActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.exovideoreference.utils.showSnackbar
import com.onurcan.pangolin.R
import com.onurcan.pangolin.databinding.FragmentSettingsBinding
import com.onurcan.pangolin.helpers.Constants
import com.onurcan.pangolin.helpers.FragmentHelper
import com.onurcan.pangolin.helpers.SwipeDismissActions
import com.onurcan.pangolin.helpers.ViewUtils
import com.onurcan.pangolin.ui.contracts.ISettings
import com.onurcan.pangolin.ui.mvp.BaseFragment
import com.onurcan.pangolin.ui.presenters.SettingsPresenter
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class SettingsFragment : BaseFragment(R.layout.fragment_settings), ISettings.ViewSettings,
    AppBarLayout.OnOffsetChangedListener {

    private lateinit var context: FragmentActivity
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var mStorageRef: StorageReference

    private val MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24.toLong()
    private val dateFormat: DateFormat = SimpleDateFormat("EE dd, MMM", Locale.ENGLISH)
    private val calendar = Calendar.getInstance()

    private var name: String? = ""
    private var bio: String? = ""

    private var isHideToolbarView = false

    private val presenter: SettingsPresenter by lazy {
        SettingsPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = requireActivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        presenter.onViewsCreated()

        FragmentHelper.onKeyBack(this, context)

        binding.fragSettingsUsernameLyt.setOnClickListener {
            initNameDialog(R.style.DialogSlide)
            // setUpDatePicker()
        }

        binding.fragSettingsBioLyt.setOnClickListener {
            initBioDialog(R.style.DialogSlide)
        }
    }

    override fun initUI() {

        mStorageRef = FirebaseStorage.getInstance().reference

        binding.backArrow.setOnClickListener {
            FragmentHelper.popBackStack(context)
        }

        SwipeDismissActions.fragmentDismiss(binding.fragMaterialRoot, context)

        binding.appBarLayout.post {
            val height = ViewUtils.getScreenWidth(context) * 1 / 3
            setOffset(height)
        }

        binding.toolbarImage.layoutParams.height = ViewUtils.getScreenWidth(context)

        binding.appBarLayout.addOnOffsetChangedListener(this)

        binding.fragSettingsChangeProf.setOnClickListener {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(context)
        }

    }

    fun setUpDatePicker() {
        calendar.clear()
        /* Initially start from today */
        val dateRangeToday = MaterialDatePicker.todayInUtcMilliseconds()

        /* Constrain Calendar */
        val calendarConst = CalendarConstraints.Builder()
        calendarConst.setValidator(DateValidatorPointForward.now())

        /* Material Date Picker */
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTheme(R.style.DatePickerStyle)
        builder.setTitleText("R.string.date_picker_header")
        builder.setSelection(
            Pair(
                dateRangeToday,
                dateRangeToday + MILLIS_IN_A_DAY
            )
        )
        builder.setCalendarConstraints(calendarConst.build())
        val materialDatePicker = builder.build()
        materialDatePicker.addOnPositiveButtonClickListener { selection: Pair<Long, Long>? ->
            Toast.makeText(
                context,
                "addOnPositiveButtonClickListener",
                Toast.LENGTH_SHORT
            ).show()
        }
        materialDatePicker.addOnNegativeButtonClickListener { selection: View? ->
            Toast.makeText(
                context,
                "addOnNegativeButtonClickListener",
                Toast.LENGTH_SHORT
            ).show()
        }
        materialDatePicker.addOnCancelListener { selection: DialogInterface? ->
            Toast.makeText(
                context,
                "addOnCancelListener",
                Toast.LENGTH_SHORT
            ).show()
        }
        materialDatePicker.addOnDismissListener { selection: DialogInterface? ->
            Toast.makeText(
                context,
                "addOnDismissListener",
                Toast.LENGTH_SHORT
            ).show()
        }
        materialDatePicker.show(requireFragmentManager(), this.javaClass.simpleName)
    }

    override fun setOffset(offsetPX: Int) {
        val params = binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior as AppBarLayout.Behavior
        behavior.onNestedPreScroll(
            binding.root,
            binding.appBarLayout,
            binding.toolbarImage,
            0,
            offsetPX,
            intArrayOf(0, 0)
        )
    }

    override fun initNameDialog(type: Int) {
        val dialog = Dialog(context, R.style.BlurTheme)
        dialog.window!!.attributes.windowAnimations = type
        dialog.setContentView(R.layout.dialog_edit_name)

        val dBack = dialog.findViewById<ImageButton>(R.id.back_arrow)
        val dName = dialog.findViewById<TextInputEditText>(R.id.dialog_name)
        val dFAB = dialog.findViewById<FloatingActionButton>(R.id.dialog_fab)
        dName.setText(name)

        val dMaterial = dialog.findViewById<MaterialCardView>(R.id.dialog_material_card)

        SwipeDismissActions.dialogDismiss(dMaterial, dialog)

        dFAB.setOnClickListener {
            val dUName = dName.text.toString()
            if (dUName.isEmpty()) {
                showSnackbar(dName, getString(R.string.error_bio))
            } else {
                Constants.fUserInfoRef.child("name").setValue(dUName)
                showSnackbar(dName, getString(R.string.prompt_save_username, dUName))
                dialog.dismiss()
            }
        }

        dBack.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun initBioDialog(type: Int) {
        val dialog = Dialog(context, R.style.BlurTheme)
        dialog.window!!.attributes.windowAnimations = type
        dialog.setContentView(R.layout.dialog_edit_bio)

        val dBack = dialog.findViewById<ImageButton>(R.id.back_arrow)
        val dBio = dialog.findViewById<TextInputEditText>(R.id.dialog_bio)
        val dFAB = dialog.findViewById<FloatingActionButton>(R.id.dialog_fab)
        dBio.setText(bio)

        val dMaterial = dialog.findViewById<MaterialCardView>(R.id.dialog_material_card)

        SwipeDismissActions.dialogDismiss(dMaterial, dialog)

        dFAB.setOnClickListener {
            val dUBio = dBio.text.toString()
            if (dUBio.isEmpty()) {
                showSnackbar(dBio, getString(R.string.error_bio))
            } else {
                Constants.fUserInfoRef.child("memory").setValue(dUBio)
                showSnackbar(dBio, getString(R.string.prompt_save_username, dUBio))
                dialog.dismiss()
            }
        }

        dBack.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun populateView() {
        Constants.fUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val photoUrl = snapshot.child("photoUrl").value.toString()
                name = snapshot.child("name").value.toString()
                bio = snapshot.child("memory").value.toString()

                Picasso.get().load(photoUrl).centerCrop().fit().into(binding.toolbarImage)

                binding.fragSettingsCToolbar.title = name
                binding.fragSettingsUsername.text = name
                binding.fragSettingsBio.text = bio

                binding.toolbarHeaderView.header.bindTo(photoUrl, name)
            }

            override fun onCancelled(error: DatabaseError) {
                showLogError(this.javaClass.simpleName, error.toString())
            }
        })
        Constants.fOnlineDBRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val signInMethod = snapshot.child("signInMethod").value.toString()

                binding.fragSettingsLoginMethod.text = signInMethod
            }

            override fun onCancelled(error: DatabaseError) {
                showLogError(this.javaClass.simpleName, error.toString())
            }
        })
        try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            val version = info.versionName
            binding.fragSettingsAppVersion.text =
                context.getString(R.string.action_app_version, version)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val maxScroll = binding.appBarLayout.totalScrollRange
        val perc = abs(verticalOffset).toFloat() / maxScroll.toFloat()
        if (perc >= .9f && isHideToolbarView) {
            binding.toolbarHeaderView.header.visibility = View.VISIBLE
            binding.toolbar.background =
                ContextCompat.getDrawable(context, R.color.darkPurple_Navigation)
            isHideToolbarView = !isHideToolbarView
        } else if (perc < .9f && !isHideToolbarView) {
            binding.toolbarHeaderView.header.visibility = View.GONE
            binding.toolbar.background = ContextCompat.getDrawable(context, R.color.transparent)
            isHideToolbarView = !isHideToolbarView
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imgRequestCode && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            CropImage.activity(imageUri).setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL).start(context)
        }
        presenter.resultActivity(
            requestCode,
            resultCode,
            data,
            context,
            mStorageRef,
            Constants.fUserInfoRef
        )
    }

    companion object {
        private const val imgRequestCode = 2020
    }
}