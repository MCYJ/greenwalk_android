import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.codehanzoom.greenwalk.MainActivity
import com.codehanzoom.greenwalk.R
import com.codehanzoom.greenwalk.compose.TopBar
import com.codehanzoom.greenwalk.model.PloggingResponseBody
import com.codehanzoom.greenwalk.utils.UploadService
import com.codehanzoom.greenwalk.viewModel.PloggingInfoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private fun resizeBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
    return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)
}

@Composable
fun CameraPreviewScreen(navController: NavHostController) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember { ImageCapture.Builder().build() }
    var isCapturing by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    CameraUI(previewView, navController, imageCapture, context, isCapturing, capturedImageUri) { capturing, uri ->
        isCapturing = capturing
        capturedImageUri = uri
    }
}

@Composable
fun CameraUI(
    previewView: PreviewView,
    navController: NavHostController,
    imageCapture: ImageCapture,
    context: Context,
    isCapturing: Boolean,
    capturedImageUri: Uri?,
    setCapturing: (Boolean, Uri?) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val coroutineScope = rememberCoroutineScope()

    TopBar(title = "사진촬영", navController = navController)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Box(modifier = Modifier
            .width(screenWidth)
        ) {
            if (isCapturing && capturedImageUri != null) {
                Image(
                    painter = rememberImagePainter(capturedImageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .height(500.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
            else {
                AndroidView({ previewView },
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth())
            }
        }

        Spacer(modifier = Modifier.height(150.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_camera),
            contentDescription = null,
            Modifier.clickable {
                coroutineScope.launch {
                    Toast.makeText(context, "사진처리중 입니다!", Toast.LENGTH_LONG).show()
                    setCapturing(true, null)
                    captureImageAndSendToServer(
                        imageCapture,
                        context,
                        "http://aws-v5-beanstalk-env.eba-znduyhtv.ap-northeast-2.elasticbeanstalk.com/",
                        0, // steps
                        0.0 // distance
                    ) { uri ->
                        setCapturing(true, uri)
                    }
                    delay(20000)
                    navController.navigate("PointScreen")
                }
                coroutineScope.launch {
                    var remainTime = 20
                    for (i in 1..4) {
                        remainTime -= 5
                        delay(5000)
                        Toast.makeText(context, "$remainTime 초 남았습니다!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

private fun captureImageAndSendToServer(
    imageCapture: ImageCapture,
    context: Context,
    serverUrl: String,
    step: Int,
    walking: Double,
    onImageCaptured: (Uri?) -> Unit
) {
    val name = "CameraxImage_${System.currentTimeMillis()}.png"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }
    }
    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    val accessToken = MainActivity.prefs.getString("accessToken", "")
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri
                onImageCaptured(savedUri)
                savedUri?.let { uri ->
                    sendImageToServer(context, uri, serverUrl, step, walking, "Bearer $accessToken")
                    Log.d("imagecapture send", accessToken)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraXApp", "Image save failed: $exception")
                onImageCaptured(null)
            }
        }
    )
}

private fun sendImageToServer(
    context: Context,
    imageUri: Uri,
    serverUrl: String,
    step: Int,
    walking: Double,
    accessToken: String
) {
    val mediaTypeTextPlain = "text/plain".toMediaTypeOrNull()
    val viewModel = PloggingInfoViewModel()

    try {
        context.contentResolver.openInputStream(imageUri).use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val resizedBitmap = resizeBitmap(bitmap, 640, 640)
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val imageBytes = outputStream.toByteArray()

            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)  // 읽기 타임아웃을 30초로 설정
                .connectTimeout(20, TimeUnit.SECONDS)  // 연결 타임아웃을 30초로 설정
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(UploadService::class.java)
            val mediaTypePng = "image/png".toMediaTypeOrNull()
            val requestFile = RequestBody.create(mediaTypePng, imageBytes)
            val imagePart = MultipartBody.Part.createFormData("image", "image.png", requestFile)
            val stepBody = RequestBody.create(mediaTypeTextPlain, step.toString())
            val walkingBody = RequestBody.create(mediaTypeTextPlain, walking.toString())

            val call = service.uploadImage(imagePart, stepBody, walkingBody, "Bearer $accessToken")
            call.enqueue(object : Callback<PloggingResponseBody> {
                override fun onResponse(call: Call<PloggingResponseBody>, response: Response<PloggingResponseBody>) {
                    if (response.isSuccessful) {
                        val ploggingInfo = response.body()
                        ploggingInfo?.let {
                            body ->
                            viewModel.setImageUrl(ploggingInfo.imageUrl?:"err")
                            viewModel.setPoint(ploggingInfo.point?:-1)
                            viewModel.setTrashCount(ploggingInfo.trashCount?:-1)
                        }

                        println("response: ${response.message()}, trashCount: ${viewModel.getTrashCount()}")
                        println("Image uploaded successfully!")
                    } else {
                        println("Failed to upload image: ${response.code()}, error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<PloggingResponseBody>, t: Throwable) {
                    println("Network error: ${t.message}")
                }
            })
        }
    } catch (e: FileNotFoundException) {
        Log.e("CameraXApp", "File not found: $e")
    } catch (e: IOException) {
        Log.e("CameraXApp", "Error accessing file: $e")
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }
