package com.example.cycle.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.cycle.crearArchivoImagen
import com.example.cycle.guardarUriImagenPerfil
import com.example.cycle.model.RegistroArriendo
import com.example.cycle.model.User
import com.example.cycle.obtenerInformacionUsuario
import com.example.cycle.obtenerUriImagenPerfil
import kotlin.toString

@Composable
fun PantallaPerfilUsuario(usuario: User, alVolver: () -> Unit, alCerrarSesion: () -> Unit, arriendosTotales: Int) {
    val contexto = LocalContext.current
    var uriImagen by remember { mutableStateOf<Uri?>(obtenerUriImagenPerfil(contexto)?.let { Uri.parse(it) }) }
    var mostrarOpciones by remember { mutableStateOf(false) }

    val lanzadorGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uriImagen = uri
            guardarUriImagenPerfil(contexto, uri.toString())
        }
    }

    var uriTemporal by remember { mutableStateOf<Uri?>(null) }
    val lanzadorCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito && uriTemporal != null) {
            uriImagen = uriTemporal
            guardarUriImagenPerfil(contexto, uriTemporal.toString())
        }
    }

    val lanzadorPermisos = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) {
            val archivo = crearArchivoImagen(contexto)
            uriTemporal = FileProvider.getUriForFile(contexto, "${contexto.packageName}.fileprovider", archivo)
            lanzadorCamara.launch(uriTemporal!!)
        } else {
            Toast.makeText(contexto, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    if (mostrarOpciones) {
        AlertDialog(
            onDismissRequest = { mostrarOpciones = false },
            title = { Text("Seleccionar foto de perfil") },
            text = { Text("¿Desde dónde quieres subir tu foto?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarOpciones = false
                    lanzadorPermisos.launch(android.Manifest.permission.CAMERA)
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(8.dp))
                        Text("Cámara")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarOpciones = false
                    lanzadorGaleria.launch("image/*")
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(8.dp))
                        Text("Galería")
                    }
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = alVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") }
            Text("Perfil de Usuario", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { mostrarOpciones = true },
                contentAlignment = Alignment.Center
            ) {
                if (uriImagen != null) {
                    AsyncImage(
                        model = uriImagen,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(4.dp)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(usuario.usuario, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(usuario.email, fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Resumen de Actividad", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Arriendos realizados:")
                        Text(arriendosTotales.toString(), fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Miembro desde:")
                        Text("Octubre 2023", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = alCerrarSesion,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}
@Composable
fun PantallaMisArriendos(historial: List<RegistroArriendo>, alVolver: () -> Unit, alCancelarReserva: (RegistroArriendo) -> Unit) {
    var registroSeleccionado by remember { mutableStateOf<RegistroArriendo?>(null) }
    var mostrarConfirmacionCancelacion by remember { mutableStateOf(false) }

    if (registroSeleccionado != null && !mostrarConfirmacionCancelacion) {
        AlertDialog(
            onDismissRequest = { registroSeleccionado = null },
            confirmButton = {
                TextButton(onClick = { registroSeleccionado = null }) {
                    Text("Cerrar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarConfirmacionCancelacion = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Cancelar Reserva")
                }
            },
            title = { Text("Resumen del Arriendo") },
            text = {
                Column {
                    Text("Juego: ${registroSeleccionado!!.nombreJuego}", fontWeight = FontWeight.Bold)
                    Text("Monto pagado: ${registroSeleccionado!!.precio} CLP")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Información de Pago", fontWeight = FontWeight.Bold)
                    Text("Tarjeta: **** **** **** ${registroSeleccionado!!.ultimos4Tarjeta}")
                    Text("Titular: ${registroSeleccionado!!.nombreTarjeta}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Estado: ${registroSeleccionado!!.estado}")
                    Text("Código de Retiro: ${registroSeleccionado!!.codigoQR}")
                }
            }
        )
    }

    if (mostrarConfirmacionCancelacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionCancelacion = false },
            confirmButton = {
                Button(
                    onClick = {
                        registroSeleccionado?.let { alCancelarReserva(it) }
                        registroSeleccionado = null
                        mostrarConfirmacionCancelacion = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Confirmar Cancelación")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacionCancelacion = false }) {
                    Text("Atrás")
                }
            },
            title = { Text("¿Cancelar Reserva?") },
            text = { Text("¿Estás seguro de que deseas cancelar la reserva de ${registroSeleccionado?.nombreJuego}? Esta acción no se puede deshacer.") }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = alVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") }
            Text("Mis Arriendos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        if (historial.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No tienes arriendos activos", color = Color.Gray) }
        } else {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(historial) { registro ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { registroSeleccionado = registro },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = registro.urlImagen,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp).background(Color.White),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(registro.nombreJuego, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Fecha: ${registro.fecha}", fontSize = 14.sp)
                                Text("Estado: ${registro.estado}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.size(40.dp).background(Color.White).padding(4.dp)) {
                                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.fillMaxSize(), tint = Color.Black)
                                }
                                Text(registro.codigoQR, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}