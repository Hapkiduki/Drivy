package com.hapkiduki.drivy.ui.components

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hapkiduki.drivy.util.getRealPathFromURI
import java.io.File

typealias ImageHandler = (File, String) -> Unit

@Composable
fun PhotoButton(
    text: String,
    onImageSelected: ImageHandler
) {
    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                var type = "image/jpg"
                val x = File(getRealPathFromURI(context, uri)!!)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    type = context.contentResolver.getType(uri) ?: type
                }
                selectedImage = uri
                onImageSelected(x, type)
            }
        }
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            pickMedia.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }) {
            Text(text = text)
        }
        Spacer(modifier = Modifier.height(30.dp))
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(selectedImage)
                .crossfade(true)
                .build(),
            contentDescription = "Uploaded photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(150.dp)
        )
    }

}