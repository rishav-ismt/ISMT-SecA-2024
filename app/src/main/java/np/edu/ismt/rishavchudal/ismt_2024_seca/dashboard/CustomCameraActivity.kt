package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.ActivityCustomCameraBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CustomCameraActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private var cameraExecutor: ExecutorService? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private lateinit var customCameraBinding: ActivityCustomCameraBinding

    companion object {
        const val FILE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val CAMERA_ACTIVITY_SUCCESS_RESULT_CODE = 3012
        const val CAMERA_ACTIVITY_FAILURE_RESULT_CODE = 3013
        const val CAMERA_ACTIVITY_CANCEL_RESULT_CODE = 3014
        const val CAMERA_ACTIVITY_OUTPUT_FILE_PATH = "output_file_path"
        const val CAMERA_ACTIVITY_OUTPUT_EXCEPTION_MESSAGE = "output_exception_message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customCameraBinding = ActivityCustomCameraBinding.inflate(layoutInflater)
        setContentView(customCameraBinding.root)

        if (allPermissionGranted()) {
            initializeCustomCamera()
        } else {
            requestPermissions(
                getPermissionsRequiredForCamera().toTypedArray(),
                10
            )
        }

        customCameraBinding.mbCancel.setOnClickListener { setFailureResultBackToCallingComponent("Camera cancelled...") }

        customCameraBinding.mbCameraClick.setOnClickListener { captureImage() }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun allPermissionGranted(): Boolean {
        var granted = false
        for (permission in getPermissionsRequiredForCamera()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                granted = true
            }
        }
        return granted
    }

    private fun getPermissionsRequiredForCamera(): List<String> {
        val permissions: MutableList<String> = ArrayList()
        permissions.add(Manifest.permission.CAMERA)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return permissions
    }

    private fun initializeCustomCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture!!.addListener(
            Runnable {
                try {
                    val processCameraProvider = cameraProviderFuture!!.get()
                    val preview =
                        Preview.Builder().build()
                    preview.setSurfaceProvider(
                        customCameraBinding.previewViewCamera.surfaceProvider
                    )
                    imageCapture = ImageCapture.Builder().build()
                    val defaultCameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    processCameraProvider.unbindAll()
                    processCameraProvider.bindToLifecycle(
                        this,
                        defaultCameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun captureImage() {
        //If this instance is null, we don't have to proceed further
        if (imageCapture == null) {
            setFailureResultBackToCallingComponent("Cannot start camera...")
            return
        }
        val fileName: String = SimpleDateFormat(FILE_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/")
        }

        //Lets create an output options object which will contains the file and metadata
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        //Setting up image capture listener, which is triggered after photo has been taken
        imageCapture!!.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(
                    outputFileResults: ImageCapture.OutputFileResults
                ) {
                    setSuccessResultBackToCallingComponent(
                        outputFileResults.savedUri.toString()
                    )
                }

                override fun onError(exception: ImageCaptureException) {
                    setFailureResultBackToCallingComponent(exception.message)
                }
            }
        )
    }

    private fun setSuccessResultBackToCallingComponent(outputFileUriPath: String) {
        val intent = Intent()
        intent.putExtra(CAMERA_ACTIVITY_OUTPUT_FILE_PATH, outputFileUriPath)
        setResult(CAMERA_ACTIVITY_SUCCESS_RESULT_CODE, intent)
        finish()
    }

    private fun setFailureResultBackToCallingComponent(exceptionMessage: String?) {
        val intent = Intent()
        intent.putExtra(CAMERA_ACTIVITY_OUTPUT_FILE_PATH, exceptionMessage)
        setResult(CAMERA_ACTIVITY_FAILURE_RESULT_CODE, intent)
        finish()
    }

    override fun onBackPressed() {
        setResult(CAMERA_ACTIVITY_FAILURE_RESULT_CODE)
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor!!.shutdown()
    }
}