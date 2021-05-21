package com.onurcan.pangolin.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.View
import androidx.camera.core.*
import androidx.camera.core.impl.VideoCaptureConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.common.util.concurrent.ListenableFuture
import com.onurcan.pangolin.ui.mvp.BaseFragment
import com.onurcan.exovideoreference.utils.showLogDebug
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.exovideoreference.utils.showLogInfo
import com.onurcan.pangolin.R
import com.onurcan.pangolin.databinding.FragmentCameraBinding
import com.onurcan.pangolin.helpers.Constants
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragment : BaseFragment(R.layout.fragment_camera) {

    private lateinit var binding: FragmentCameraBinding


    private lateinit var outputDirectory: File

    private lateinit var cameraProviderFeature: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private lateinit var previewView: Preview
    private lateinit var cameraControl: CameraControl
    private lateinit var cameraInfo: CameraInfo

    private lateinit var imageAnalysis: ImageAnalysis
    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var videoCapture: VideoCapture
    private lateinit var imageCapture: ImageCapture

    private var isFrontFacing = true
    private var isRecording = false

    private var camera: Camera? = null

    private lateinit var context: FragmentActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        context = activity!!
        super.onCreate(savedInstanceState)
        cameraProviderFeature = ProcessCameraProvider.getInstance(context.applicationContext)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
//        val barLayout = context.findViewById<AppBarLayout>(R.id.appBarLayout)
//        barLayout.visibility=View.GONE
        if (allPermissionsGranted()) {
            binding.previewView.post {
                initBackPhotoCamera()
            }
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }


        binding.toggleCamera.setOnClickListener {
            toggleCamera()
        }

        binding.toggleFlash.setOnClickListener {
            toggleTorch()
            setTorchStateObserver()
        }

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context.applicationContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
        private var lastAnalyzedTimestamp = 0L

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {
            val currentTimestamp = System.currentTimeMillis()
            // Calculate the average luma no more often than every second
            if (currentTimestamp - lastAnalyzedTimestamp >=
                TimeUnit.SECONDS.toMillis(1)
            ) {
                val buffer = image.planes[0].buffer
                val data = buffer.toByteArray()
                val pixels = data.map { it.toInt() and 0xFF }
                val luma = pixels.average()
                showLogDebug(this.javaClass.simpleName, "Average luminosity: $luma")
                lastAnalyzedTimestamp = currentTimestamp
            }
            image.close()
        }
    }

    fun toggleTorch() {
        when (cameraInfo.torchState.value) {
            TorchState.ON -> {
                cameraControl.enableTorch(false)
            }
            else -> {
                cameraControl.enableTorch(true)
            }
        }
    }

    private fun setTorchStateObserver() {
        cameraInfo.torchState.observe(this, androidx.lifecycle.Observer { state ->
            if (state == TorchState.ON)
                binding.toggleFlash.setImageResource(R.drawable.ic_flash_on)
            else
                binding.toggleFlash.setImageResource(R.drawable.ic_flash_off)
        })
    }

    fun toggleCamera() {
        isFrontFacing = if (isFrontFacing) {
            binding.previewView.post { initFrontPhotoCamera() }
            false
        } else {
            binding.previewView.post { initBackPhotoCamera() }
            true
        }
    }

    fun recordVideo() {
        isRecording = if (!isRecording) {
            startRecording()
            true
        } else {
            stopRecording()
            false
        }
    }

    fun takePhoto() {
        val file = Constants.createFile(
            outputDirectory,
            Constants.FILENAME,
            Constants.IMAGE_EXTENSION
        )
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputFileOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Handler(Looper.getMainLooper()).post {
                        //TODO Handle Saved Photo
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    showLogError(this.javaClass.simpleName, exception.message!!)
                }
            }
        )
    }

    @SuppressLint("RestrictedApi")
    fun startRecording() {
        val file = Constants.createFile(
            outputDirectory,
            Constants.FILENAME,
            Constants.VIDEO_EXTENSION
        )
        videoCapture.startRecording(
            file,
            executor,
            object : VideoCapture.OnVideoSavedCallback {
                override fun onVideoSaved(file: File) {
                    Handler(Looper.getMainLooper()).post {
                        //TODO Handle Saved Video
                    }
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    showLogError(this.javaClass.simpleName, message)
                }
            }
        )
    }

    @SuppressLint("RestrictedApi")
    fun stopRecording() {
        videoCapture.stopRecording()
    }

    @SuppressLint("RestrictedApi")
    fun initFrontVideoCamera() {
        CameraX.unbindAll()
        binding.toggleFlash.visibility = View.INVISIBLE

        outputDirectory = Constants.getOutputDirectory(context)
        previewView = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
            setTargetRotation(binding.previewView.display.rotation)
        }.build()

        cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

        val videoCaptureConfig = VideoCaptureConfig.Builder().apply {
            setTargetRotation(binding.previewView.display.rotation)
            setCameraSelector(cameraSelector)
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
        }
        videoCapture = VideoCapture(videoCaptureConfig.useCaseConfig)

        cameraProviderFeature.addListener({
            val cameraProvider = cameraProviderFeature.get()
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                previewView,
                videoCapture
            )
            cameraInfo = camera?.cameraInfo!!
            cameraControl = camera?.cameraControl!!
            binding.previewView.preferredImplementationMode =
                PreviewView.ImplementationMode.TEXTURE_VIEW
            previewView.setSurfaceProvider(binding.previewView.createSurfaceProvider(cameraInfo))
        }, ContextCompat.getMainExecutor(context.applicationContext))
    }

    @SuppressLint("RestrictedApi")
    fun initBackVideoCamera() {
        CameraX.unbindAll()
        binding.toggleFlash.visibility = View.VISIBLE

        outputDirectory = Constants.getOutputDirectory(context)
        previewView = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
            setTargetRotation(binding.previewView.display.rotation)
        }.build()

        cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        val videoCaptureCfg = VideoCaptureConfig.Builder().apply {
            setTargetRotation(binding.previewView.display.rotation)
            setCameraSelector(cameraSelector)
            setMaxResolution(Size(1080, 2310))
            setDefaultResolution(Size(1080, 2310))
        }

        videoCapture = VideoCapture(videoCaptureCfg.useCaseConfig)

        cameraProviderFeature.addListener({
            val cameraProvider = cameraProviderFeature.get()
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                previewView,
                videoCapture
            )
            cameraInfo = camera?.cameraInfo!!
            cameraControl = camera?.cameraControl!!
            binding.previewView.preferredImplementationMode =
                PreviewView.ImplementationMode.TEXTURE_VIEW
            previewView.setSurfaceProvider(binding.previewView.createSurfaceProvider(cameraInfo))
        }, ContextCompat.getMainExecutor(context.applicationContext))
    }

    @SuppressLint("RestrictedApi")
    fun initFrontPhotoCamera() {
        CameraX.unbindAll()
        binding.toggleFlash.visibility = View.INVISIBLE

        previewView = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_4_3)
            setTargetRotation(binding.previewView.display.rotation)
            setDefaultResolution(Size(1920, 1080))
            setMaxResolution(Size(3024, 4032))
        }.build()

        imageAnalysis = ImageAnalysis.Builder().apply {
            setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        }.build()
        imageAnalysis.setAnalyzer(executor, LuminosityAnalyzer())

        imageCapture = ImageCapture.Builder().apply {
            setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
        }.build()

        cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

        cameraProviderFeature.addListener(Runnable {
            val cameraProvider = cameraProviderFeature.get()
            val camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                previewView,
                imageAnalysis,
                imageCapture
            )
            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo
            binding.previewView.preferredImplementationMode =
                PreviewView.ImplementationMode.TEXTURE_VIEW
            previewView.setSurfaceProvider(binding.previewView.createSurfaceProvider(cameraInfo))
        }, ContextCompat.getMainExecutor(context.applicationContext))
    }

    @SuppressLint("RestrictedApi")
    fun initBackPhotoCamera() {
        CameraX.unbindAll()
        binding.toggleFlash.visibility = View.VISIBLE

        previewView = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
            setTargetRotation(binding.previewView.display.rotation)
            setDefaultResolution(Size(1920, 1080))
            setMaxResolution(Size(3024, 4032))
        }.build()

        imageAnalysis = ImageAnalysis.Builder().apply {
            setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        }.build()
        imageAnalysis.setAnalyzer(executor, LuminosityAnalyzer())

        imageCapture = ImageCapture.Builder().apply {
            setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
        }.build()

        cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        cameraProviderFeature.addListener(Runnable {
            val cameraProvider = cameraProviderFeature.get()
            val camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                previewView,
                imageAnalysis,
                imageCapture
            )
            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo
            binding.previewView.preferredImplementationMode =
                PreviewView.ImplementationMode.TEXTURE_VIEW
            previewView.setSurfaceProvider(binding.previewView.createSurfaceProvider(cameraInfo))
        }, ContextCompat.getMainExecutor(context.applicationContext))
    }

    @SuppressLint("RestrictedApi")
    override fun onDestroy() {
        super.onDestroy()
        fragmentManager?.beginTransaction()?.remove(CameraFragment())?.commitAllowingStateLoss()

        CameraX.unbindAll()
        imageAnalysis.clearAnalyzer()
        imageAnalysis.clear()

        showLogInfo(this.javaClass.simpleName,"onDestroy")
    }

    @SuppressLint("RestrictedApi")
    override fun onDestroyView() {
        super.onDestroyView()
        fragmentManager?.beginTransaction()?.remove(CameraFragment())?.commitAllowingStateLoss()

        CameraX.unbindAll()
        imageAnalysis.clearAnalyzer()
        imageAnalysis.clear()

        showLogInfo(this.javaClass.simpleName,"onDestroyView")
    }

    @SuppressLint("RestrictedApi")
    override fun onDetach() {
        super.onDetach()
        fragmentManager?.beginTransaction()?.remove(CameraFragment())?.commitAllowingStateLoss()

        CameraX.unbindAll()
        imageAnalysis.clearAnalyzer()
        imageAnalysis.clear()

        showLogInfo(this.javaClass.simpleName,"onDetach")
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private val REQUEST_CODE_PERMISSIONS = 10
    }
}