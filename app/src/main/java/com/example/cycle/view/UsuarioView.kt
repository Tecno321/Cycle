package com.example.cycle.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.cycle.model.AppBD
import com.example.cycle.model.RegistroArriendo
import com.example.cycle.model.User
import kotlinx.coroutines.launch

@Composable
fun PantallaPerfilUsuario(
    usuario: User, 
    alVolver: () -> Unit, 
    alCerrarSesion: () -> Unit, 
    arriendosTotales: Int,
    onUsuarioActualizado: (User) -> Unit
) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppBD.getDatabase(contexto) }

    var uriImagen by remember(usuario.id) { 
        mutableStateOf<Uri?>(usuario.fotoUri?.let { Uri.parse(it) }) 
    }
    var mostrarOpciones by remember { mutableStateOf(false) }

    LaunchedEffect(usuario.id, usuario.fotoUri) {
        uriImagen = usuario.fotoUri?.let { Uri.parse(it) }
    }

    val actualizarFoto = { nuevaUri: Uri ->
        uriImagen = nuevaUri
        scope.launch {
            db.UsuarioDao().actualizarFotoPerfil(usuario.id, nuevaUri.toString())
            val usuarioActualizado = db.UsuarioDao().obtenerUsuarioPorId(usuario.id)
            if (usuarioActualizado != null) {
                onUsuarioActualizado(usuarioActualizado)
            }
        }
    }

    val lanzadorGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) actualizarFoto(uri)
    }

    var uriTemporal by remember { mutableStateOf<Uri?>(null) }
    val lanzadorCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito && uriTemporal != null) actualizarFoto(uriTemporal!!)
    }

    val lanzadorPermisos = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) {
            val archivo = crearArchivoImagen(contexto)
            uriTemporal = FileProvider.getUriForFile(contexto, "${contexto.packageName}.fileprovider", archivo)
            lanzadorCamara.launch(uriTemporal!!)
        }
    }

    if (mostrarOpciones) {
        AlertDialog(
            onDismissRequest = { mostrarOpciones = false },
            title = { Text("Seleccionar foto de perfil") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarOpciones = false
                    lanzadorPermisos.launch(android.Manifest.permission.CAMERA)
                }) { Text("Cámara") }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarOpciones = false
                    lanzadorGaleria.launch("image/*")
                }) { Text("Galería") }
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
                Box(modifier = Modifier.align(Alignment.BottomEnd).background(MaterialTheme.colorScheme.primary, CircleShape).padding(4.dp)) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(usuario.usuario, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(usuario.email, fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Resumen de Actividad", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Arriendos realizados:")
                        Text(arriendosTotales.toString(), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(onClick = alCerrarSesion, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun PantallaMisArriendos(
    historial: List<RegistroArriendo>,
    alVolver: () -> Unit,
    alCancelarReserva: (RegistroArriendo) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = alVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
            }
            Text("Mis Arriendos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        if (historial.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes arriendos activos", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(historial) { registro ->
                    CardArriendo(registro, alCancelarReserva)
                }
            }
        }
    }
}

@Composable
fun CardArriendo(registro: RegistroArriendo, alCancelarReserva: (RegistroArriendo) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = registro.urlImagen,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(registro.nombreJuego, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Fecha: ${registro.fecha}", fontSize = 14.sp, color = Color.Gray)
                    Text("Estado: ${registro.estado}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Pagado con tarjeta:", fontSize = 12.sp, color = Color.Gray)
                    Text("**** **** **** ${registro.ultimos4Tarjeta}", fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Código QR:", fontSize = 12.sp, color = Color.Gray)
                    Text(registro.codigoQR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { alCancelarReserva(registro) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Text("Cancelar Reserva")
            }
        }
    }
}
