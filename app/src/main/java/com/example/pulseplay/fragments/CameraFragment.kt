package com.example.pulseplay.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pulseplay.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment(), TextureView.SurfaceTextureListener {

    private lateinit var textureView: TextureView
    private lateinit var captureButton: Button
    private lateinit var galleryButton: Button
    private lateinit var flipButton: Button
    private lateinit var cameraManager: CameraManager
    private var cameraId: String = ""
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var imageReader: ImageReader? = null
    private val cameraOpenCloseLock = Semaphore(1)
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private var previewSize: Size? = null
    private var isBackCamera = true
    private var currentPhotoPath: String? = null

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 200
        private const val REQUEST_STORAGE_PERMISSION = 201
        private const val TAG = "CameraFragment"
        private const val MAX_PREVIEW_WIDTH = 1920
        private const val MAX_PREVIEW_HEIGHT = 1080
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(
                requireContext(),
                "Permission required to access gallery",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textureView = view.findViewById(R.id.texture_view)
        captureButton = view.findViewById(R.id.capture_button)
        galleryButton = view.findViewById(R.id.gallery_button)
        flipButton = view.findViewById(R.id.flip_button)

        cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = getBackCameraId()

        textureView.surfaceTextureListener = this

        captureButton.setOnClickListener { takePicture() }
        galleryButton.setOnClickListener { checkGalleryPermission() }
        flipButton.setOnClickListener { flipCamera() }
    }

    private fun checkGalleryPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                showPermissionRationale()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun showPermissionRationale() {
        Toast.makeText(
            requireContext(),
            "Gallery access is needed to view your photos",
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_VIEW).apply {
            type = "image/*"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }
        startActivity(galleryIntent)
    }

    private fun flipCamera() {
        isBackCamera = !isBackCamera
        closeCamera()
        cameraId = if (isBackCamera) getBackCameraId() else getFrontCameraId()
        if (textureView.isAvailable) {
            openCamera()
        }
    }

    private fun getBackCameraId(): String {
        for (id in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(id)
            if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                return id
            }
        }
        return cameraManager.cameraIdList[0]
    }

    private fun getFrontCameraId(): String {
        for (id in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(id)
            if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                return id
            }
        }
        return cameraManager.cameraIdList[0]
    }

    private fun chooseOptimalSize(choices: Array<Size>): Size {
        return choices.firstOrNull {
            it.width <= MAX_PREVIEW_WIDTH && it.height <= MAX_PREVIEW_HEIGHT &&
                    it.width == it.height * 4 / 3
        } ?: choices.last()
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (textureView.isAvailable) {
            openCamera()
        }
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        super.onPause()
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread?.looper ?: Looper.getMainLooper())
    }

    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e(TAG, "Error stopping background thread", e)
        }
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
            return
        }

        try {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?: throw RuntimeException("Cannot get available preview sizes")

            previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java))
            textureView.surfaceTexture?.setDefaultBufferSize(
                previewSize?.width ?: 0,
                previewSize?.height ?: 0
            )

            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening")
            }

            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    createCameraPreviewSession()
                    cameraOpenCloseLock.release()
                }

                override fun onDisconnected(camera: CameraDevice) {
                    cameraOpenCloseLock.release()
                    camera.close()
                    cameraDevice = null
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    cameraOpenCloseLock.release()
                    camera.close()
                    cameraDevice = null
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Camera error: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            }, backgroundHandler)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening camera", e)
            cameraOpenCloseLock.release()
        }
    }

    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            cameraCaptureSession?.close()
            cameraCaptureSession = null
            cameraDevice?.close()
            cameraDevice = null
            imageReader?.close()
            imageReader = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing", e)
        } finally {
            cameraOpenCloseLock.release()
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val texture = textureView.surfaceTexture
            texture?.setDefaultBufferSize(previewSize?.width ?: 0, previewSize?.height ?: 0)
            val surface = Surface(texture)

            captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder?.addTarget(surface)

            cameraDevice?.createCaptureSession(
                listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        if (cameraDevice == null) return

                        cameraCaptureSession = session
                        try {
                            captureRequestBuilder?.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                            )
                            cameraCaptureSession?.setRepeatingRequest(
                                captureRequestBuilder?.build()!!,
                                null,
                                backgroundHandler
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error starting camera preview", e)
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        activity?.runOnUiThread {
                            Toast.makeText(
                                context,
                                "Failed to configure camera",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                backgroundHandler
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error creating camera preview session", e)
        }
    }

    private fun takePicture() {
        if (cameraDevice == null || !textureView.isAvailable) return

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION
            )
            return
        }

        try {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val largestJpegSize = map?.getOutputSizes(ImageFormat.JPEG)?.maxByOrNull { it.width * it.height }
                ?: Size(640, 480)

            imageReader?.close()
            imageReader = ImageReader.newInstance(
                largestJpegSize.width,
                largestJpegSize.height,
                ImageFormat.JPEG,
                1
            ).apply {
                setOnImageAvailableListener({ reader ->
                    backgroundHandler?.post {
                        val image = reader.acquireLatestImage()
                        try {
                            val buffer = image.planes[0].buffer
                            val bytes = ByteArray(buffer.remaining())
                            buffer.get(bytes)
                            currentPhotoPath = saveImageToGallery(bytes)
                        } finally {
                            image.close()
                        }
                    }
                }, backgroundHandler)
            }

            val texture = textureView.surfaceTexture?.apply {
                setDefaultBufferSize(previewSize?.width ?: 0, previewSize?.height ?: 0)
            }
            val previewSurface = Surface(texture)
            val captureSurface = imageReader?.surface

            val surfaces = mutableListOf<Surface>().apply {
                add(previewSurface)
                captureSurface?.let { add(it) }
            }

            cameraDevice?.createCaptureSession(
                surfaces,
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        cameraCaptureSession = session
                        try {
                            val captureBuilder = cameraDevice?.createCaptureRequest(
                                CameraDevice.TEMPLATE_STILL_CAPTURE
                            )?.apply {
                                addTarget(captureSurface!!)
                                set(
                                    CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                                )
                                set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation())
                            }

                            cameraCaptureSession?.stopRepeating()
                            cameraCaptureSession?.capture(
                                captureBuilder?.build()!!,
                                null,
                                backgroundHandler
                            )

                            // Restart preview
                            captureRequestBuilder = cameraDevice?.createCaptureRequest(
                                CameraDevice.TEMPLATE_PREVIEW
                            )?.apply {
                                addTarget(previewSurface)
                            }
                            cameraCaptureSession?.setRepeatingRequest(
                                captureRequestBuilder?.build()!!,
                                null,
                                backgroundHandler
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error during capture", e)
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        activity?.runOnUiThread {
                            Toast.makeText(
                                context,
                                "Failed to configure capture session",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                backgroundHandler
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error taking picture", e)
        }
    }

    private fun saveImageToGallery(bytes: ByteArray): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}.jpg"

        // Use MediaStore to save directly to gallery
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }

        val resolver = requireContext().contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return try {
            uri?.let {
                resolver.openOutputStream(it)?.use { output ->
                    output.write(bytes)
                    activity?.runOnUiThread {
                        Toast.makeText(
                            context,
                            "Photo saved to gallery",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                uri.toString()
            } ?: run {
                Log.e(TAG, "Failed to create MediaStore entry")
                ""
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image", e)
            activity?.runOnUiThread {
                Toast.makeText(context, "Error saving photo", Toast.LENGTH_SHORT).show()
            }
            ""
        }
    }

    private fun getJpegOrientation(): Int {
        val rotation = activity?.windowManager?.defaultDisplay?.rotation ?: Surface.ROTATION_0
        val sensorOrientation = cameraManager.getCameraCharacteristics(cameraId)
            .get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0

        return when (rotation) {
            Surface.ROTATION_0 -> sensorOrientation
            Surface.ROTATION_90 -> (sensorOrientation + 270) % 360
            Surface.ROTATION_180 -> (sensorOrientation + 180) % 360
            Surface.ROTATION_270 -> (sensorOrientation + 90) % 360
            else -> 0
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(
                        context,
                        "Camera permission required",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture()
                } else {
                    Toast.makeText(
                        context,
                        "Storage permission required to save photos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        openCamera()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        if (cameraDevice != null) {
            createCameraPreviewSession()
        }
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) = Unit
}