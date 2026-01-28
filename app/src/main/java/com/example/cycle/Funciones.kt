package com.example.cycle

import android.content.Context
import android.net.Uri
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun guardarCredenciales(contexto: Context, usuario: String, email: String, contrasena: String) {
    val preferencias = contexto.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    preferencias.edit()
        .putString("saved_username", usuario)
        .putString("saved_email", email)
        .putString("saved_password", contrasena)
        .apply()
}

fun verificarCredenciales(contexto: Context, usuario: String, contrasena: String): Boolean {
    val preferencias = contexto.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    val usuarioGuardado = preferencias.getString("saved_username", null)
    val contrasenaGuardada = preferencias.getString("saved_password", null)
    return usuarioGuardado != null && contrasenaGuardada != null && usuarioGuardado == usuario && contrasenaGuardada == contrasena
}

fun obtenerInformacionUsuario(contexto: Context): Pair<String, String> {
    val preferencias = contexto.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    val usuario = preferencias.getString("saved_username", "Usuario") ?: "Usuario"
    val email = preferencias.getString("saved_email", "usuario@example.com") ?: "usuario@example.com"
    return Pair(usuario, email)
}

fun guardarUriImagenPerfil(contexto: Context, uri: String) {
    val preferencias = contexto.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    preferencias.edit().putString("profile_image_uri", uri).apply()
}

fun obtenerUriImagenPerfil(contexto: Context): String? {
    val preferencias = contexto.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    return preferencias.getString("profile_image_uri", null)
}

fun crearArchivoImagen(contexto: Context): File {
    val marcaTiempo = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val directorioAlmacenamiento = contexto.getExternalFilesDir("Pictures")
    return File.createTempFile("JPEG_${marcaTiempo}_", ".jpg", directorioAlmacenamiento)
}
