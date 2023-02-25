package com.hapkiduki.drivy

import android.net.Uri

data class User(
    val name: String,
    val email: String,
    val photo: Uri?
)
