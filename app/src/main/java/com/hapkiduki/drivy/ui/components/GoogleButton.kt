package com.hapkiduki.drivy.ui.components

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.hapkiduki.drivy.util.getGoogleSignInClient

@Composable
fun GoogleButton(
    text: String, onSuccessSignIn: (Task<GoogleSignInAccount>) -> Unit
) {
    val context = LocalContext.current
    val googleLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { activityResult ->
                if (activityResult.resultCode == Activity.RESULT_OK) {
                    val intent = activityResult.data
                    if (activityResult.data != null) {
                        val task: Task<GoogleSignInAccount> =
                            GoogleSignIn.getSignedInAccountFromIntent(intent)
                        Log.i("Google Signin", "Logged In")
                        Toast.makeText(context, task.result.email, Toast.LENGTH_LONG).show()
                        onSuccessSignIn(task)
                    } else {
                        Log.i("Google Signin", "Error Signing")
                        Toast.makeText(context, "Google Login Error!", Toast.LENGTH_LONG).show()
                    }
                }

            })

    Button(onClick = {
        googleLauncher.launch(getGoogleSignInClient(context).signInIntent)
    }) {
        Text(text = text)
    }
}