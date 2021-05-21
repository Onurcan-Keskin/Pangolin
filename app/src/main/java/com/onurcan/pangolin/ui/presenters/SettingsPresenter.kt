package com.onurcan.pangolin.ui.presenters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.onurcan.exovideoreference.utils.showLogDebug
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.pangolin.R
import com.onurcan.pangolin.appuser.AppUser
import com.onurcan.pangolin.ui.contracts.ISettings
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File

class SettingsPresenter constructor(private val contract: ISettings.ViewSettings) :
    ISettings.PresenterSettings {

    override fun onViewsCreated() {
        contract.initUI()
        contract.populateView()
    }

    fun resultActivity(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        context: Context,
        mStorageRef: StorageReference,
        mDatabaseRef: DatabaseReference
    ) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri

                val thumbFilePath = File(resultUri.path)

                val thumbBitmap: Bitmap = Compressor(context)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(thumbFilePath)

                val baos = ByteArrayOutputStream()
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val thumbByte = baos.toByteArray()

                val filepathRef: StorageReference =
                    mStorageRef.child("User").child("Images").child(AppUser.getUserId())
                        .child(System.currentTimeMillis().toString() + ".jpeg")
                val thumbFilepathRef: StorageReference =
                    mStorageRef.child("User").child("Thumbs").child(AppUser.getUserId())
                        .child(
                            System.currentTimeMillis().toString() + ".jpeg"
                        )

                filepathRef.putFile(resultUri)
                    .addOnCompleteListener { uploadTask: Task<UploadTask.TaskSnapshot> ->
                        if (uploadTask.isSuccessful) {
                            filepathRef.downloadUrl.addOnSuccessListener { uri: Uri? ->
                                val downloadURL = uri.toString()
                                val uploadTaskRef =
                                    thumbFilepathRef.putBytes(thumbByte)
                                uploadTaskRef.addOnCompleteListener { thumbTask ->
                                    if (thumbTask.isSuccessful) {
                                        filepathRef.downloadUrl.addOnSuccessListener { uri2: Uri? ->
                                            val thumbDownloadUrl = uri2.toString()
                                            mDatabaseRef.child("thumbUrl")
                                                .setValue(thumbDownloadUrl)
                                                .addOnCompleteListener { taskFinal ->
                                                    if (taskFinal.isSuccessful) {
                                                        val updateMap: MutableMap<String, Any> =
                                                            HashMap()
                                                        updateMap["photoUrl"] = downloadURL
                                                        updateMap["thumbUrl"] = thumbDownloadUrl
                                                        mDatabaseRef.updateChildren(updateMap)
                                                            .addOnCompleteListener { tsk ->
                                                                if (tsk.isSuccessful) {
                                                                    showLogDebug(
                                                                        this.javaClass.simpleName,
                                                                        "Update Photo - Thumb: Success"
                                                                    )
                                                                } else {
                                                                    showLogError(
                                                                        this.javaClass.simpleName,
                                                                        "Update Photo - Thumb: Failed"
                                                                    )
                                                                }
                                                            }

                                                    }
                                                }
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.error_image_upload),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }
                            }
                        }
                    }

            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(
                context,
                context.getString(R.string.error_image_upload),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
}