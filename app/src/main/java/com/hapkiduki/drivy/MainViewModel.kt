package com.hapkiduki.drivy

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel : ViewModel() {
    private lateinit var driveInstance: Drive

    var user = mutableStateOf<User?>(null)
        private set

    var autenticated = mutableStateOf(false)
        private set

    fun instanceDrive(drive: Drive, account: GoogleSignInAccount) {
        driveInstance = drive
        autenticated.value = true
        user.value = User(
            name = account.displayName ?: "No name",
            email = account.email ?: "No email",
            photo = account.photoUrl
        )
    }

    fun listFiles() {
        if (this::driveInstance.isInitialized) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val files = driveInstance.Files().list().execute()
                    Log.i("Archivos", "getFiles: Cantidad ${files.size}")
                } catch (e: Exception) {
                    Log.i("Archivos", "getFiles: Error $e")
                }

            }
        }
    }


    fun createFolder(folderName: String) {
        if (this::driveInstance.isInitialized) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val archivos = driveInstance.Files().list()
                    Log.i("Archivos", "getFiles: Cantidad ${archivos.size}")
                } catch (e: Exception) {
                    Log.i("Archivos", "getFiles: Error $e")
                }


                val gFolder = com.google.api.services.drive.model.File()
                gFolder.name = folderName
                gFolder.mimeType = "application/vnd.google-apps.folder"

                try {
                    val newFolderId =
                        driveInstance.Files().create(gFolder).setFields("id").execute()
                    Log.i("Archivos", "getFiles: $newFolderId")
                } catch (e: Exception) {
                    Log.i("Archivos", "getFiles: Error $e")
                }

            }
        }
    }

    fun uploadPhoto(folderId: String, image: File, type: String) {
        if (this::driveInstance.isInitialized) {
            viewModelScope.launch(Dispatchers.IO) {
                try {

                    val gfile = com.google.api.services.drive.model.File()


                    val fileContent = FileContent(type, image)
                    gfile.name = image.name


                    val parents: MutableList<String> = ArrayList(1)
                    parents.add(folderId)

                    gfile.parents = parents

                    driveInstance.Files().create(gfile, fileContent).setFields("id").execute()
                    Log.i("Archivos", "getFiles: Pero claro que pero por su pollo")

                } catch (e: Exception) {
                    Log.i("Archivos", "getFiles: Error $e")
                }
            }
        }
    }

}