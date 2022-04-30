package dk.itu.moapd.scootersharing.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.databinding.FragmentScanBinding
import dk.itu.moapd.scootersharing.viewmodels.RideViewModel
import dk.itu.moapd.scootersharing.viewmodels.ScooterViewModel
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanFragment : Fragment() {

    private lateinit var binding: FragmentScanBinding
    private lateinit var broadcast: String
    private lateinit var rideViewModel: RideViewModel
    private lateinit var scooterViewModel: ScooterViewModel
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDirectory: File
    private var imageCapture: ImageCapture? = null
    private var currentLocation: Location? = null
    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val latitude = intent?.getDoubleExtra("latitude", 0f.toDouble())
            val longitude = intent?.getDoubleExtra("longitude", 0f.toDouble())

            if (latitude == null || latitude == 0f.toDouble())
                return

            if (longitude == null || longitude == 0f.toDouble())
                return

            val location = Location(LocationManager.GPS_PROVIDER)
            location.latitude = latitude
            location.longitude = longitude
            currentLocation = location
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        broadcast = arguments?.get("broadcast").toString()
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(locationReceiver, IntentFilter(R.string.location_event.toString()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanBinding.inflate(layoutInflater)
        outputDirectory = getOutputDirectory()
        rideViewModel = ViewModelProvider(requireActivity())[RideViewModel::class.java]
        scooterViewModel = ViewModelProvider(requireActivity())[ScooterViewModel::class.java]
        cameraExecutor = Executors.newSingleThreadExecutor()
        if (areAllPermissionsGranted())
            startCamera()

        return binding.root
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else requireActivity().filesDir
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    private fun areAllPermissionsGranted(): Boolean {
        return arrayOf(Manifest.permission.CAMERA).all {
            ContextCompat.checkSelfPermission(
                requireContext(), it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat("yy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        //Save the image to MediaStore
        /*imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {

                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Log.d(ScanFragment::class.java.canonicalName, msg)
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(
                        ScanFragment::class.java.canonicalName,
                        "Photo capture failed: ${exc.message}",
                        exc
                    )
                }
            }
        )*/
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also { imageAnalysis ->
                    imageAnalysis.setAnalyzer(cameraExecutor,
                        object : ImageAnalysis.Analyzer {
                            @SuppressLint("UnsafeOptInUsageError")
                            override fun analyze(imageProxy: ImageProxy) {
                                val mediaImage = imageProxy.image ?: return

                                val image = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.imageInfo.rotationDegrees
                                )
                                val options = BarcodeScannerOptions.Builder()
                                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                                    .build()
                                val scanner = BarcodeScanning.getClient(options)
                                val result = scanner.process(
                                    InputImage.fromMediaImage(
                                        mediaImage,
                                        image.rotationDegrees
                                    )
                                ).addOnSuccessListener { barcodes ->
                                    if (barcodes.isEmpty()) {
                                        return@addOnSuccessListener
                                    }
                                    val barcode = barcodes[0]
                                    when (barcode.valueType) {
                                        Barcode.TYPE_TEXT -> {
                                            val scooterId =
                                                barcode.displayValue?.toLongOrNull()
                                            val intent = Intent(broadcast)
                                            intent.putExtra("scooterId", scooterId)
                                            LocalBroadcastManager.getInstance(requireContext())
                                                .sendBroadcast(intent)
                                            binding.root.findNavController().popBackStack()
                                        }
                                    }
                                }
                                    .addOnFailureListener {
                                        Log.e(
                                            ScanFragment::class.java.canonicalName,
                                            "Error analyzing: $it.message"
                                        )
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                                runBlocking { result }
                            }
                        }
                    )
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector,
                    preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(ScanFragment::class.java.canonicalName, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
}