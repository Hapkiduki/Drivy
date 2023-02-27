package com.hapkiduki.drivy

import android.os.Bundle
import android.util.Log
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.services.drive.Drive
import com.hapkiduki.drivy.ui.components.GoogleButton
import com.hapkiduki.drivy.ui.components.ImageHandler
import com.hapkiduki.drivy.ui.components.PhotoButton
import com.hapkiduki.drivy.ui.theme.DrivyTheme
import com.hapkiduki.drivy.util.driveInstance

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels()

        setContent {

            val context = LocalContext.current
            val authenticated by viewModel.autenticated
            val user by viewModel.user

            driveInstance(context, onDriveInstance = { drive, account ->
                Log.i("Drive Instance", "Drive was instanced")
                viewModel.instanceDrive(drive, account)
            })
            DrivyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(
                        onDriveInstanced = viewModel::instanceDrive,
                        authenticated = authenticated,
                        user = user,
                        onListFilesButtonClick = {
                            viewModel.listFiles()
                        },
                        onFolderButtonClick = {
                            viewModel.createFolder("Prueba Drive")
                        },
                        onNewImageUploaded = { image, type ->
                            viewModel.uploadPhoto(
                                folderId = "1nKIyQA1cfGGKlGlk6_SeXirHNukDttYt",
                                image, type
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(
    onDriveInstanced: (Drive, GoogleSignInAccount) -> Unit,
    authenticated: Boolean,
    user: User?,
    onFolderButtonClick: () -> Unit,
    onListFilesButtonClick: () -> Unit,
    onNewImageUploaded: ImageHandler,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Drivy Files",
                        color = MaterialTheme.colorScheme.surface,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF19C5B5)
                ),
                actions = {
                    if (authenticated)
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(user!!.photo)
                                .crossfade(true)
                                .build(),
                            contentDescription = user!!.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(50.dp)
                        )
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                GoogleButton(text = "Google Sign in", onSuccessSignIn = { acount ->

                    driveInstance(context, onDriveInstance = { drive, _ ->
                        Log.i("Drive Instance", "Drive was instanced")
                        onDriveInstanced(drive, acount.result)
                    })
                })
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(top = 10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (authenticated)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = user!!.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W600
                    )
                    Text(
                        text = user!!.email,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600
                    )
                }

            Text(text = "Select Image", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = onListFilesButtonClick) {
                Text(text = "List Files")
            }
            Button(onClick = onFolderButtonClick) {
                Text(text = "New Folder")
            }
            PhotoButton(
                text = "Upload photo",
                onImageSelected = onNewImageUploaded
            )

        }
    }

}


