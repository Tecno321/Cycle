package com.example.cycle.view


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cycle.*
import com.example.cycle.model.AppBD
import com.example.cycle.model.Juego
import com.example.cycle.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun PantallaPago(
    juego: Juego,
    alVolver: () -> Unit,
    alExitoPago: (Juego, String, String) -> Unit
) {
    var numeroTarjeta by remember { mutableStateOf("") }
    var fechaExpiracion by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var nombreTarjeta by remember { mutableStateOf("") }
    val contexto = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = alVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") }
            Text("Pago del Arriendo", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(model = juego.urlImagen, contentDescription = null, modifier = Modifier.size(60.dp).background(Color.White), contentScale = ContentScale.Crop)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(juego.nombre, fontWeight = FontWeight.Bold)
                        Text("Total: ${juego.precio} CLP", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Detalles de la Tarjeta", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = numeroTarjeta,
                onValueChange = { if (it.length <= 16) numeroTarjeta = it },
                label = { Text("Número de Tarjeta") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("1234 5678 1234 5678") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = fechaExpiracion,
                    onValueChange = { if (it.length <= 5) fechaExpiracion = it },
                    label = { Text("Fecha (MM/YY)") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("12/25") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = cvv,
                    onValueChange = { if (it.length <= 3) cvv = it },
                    label = { Text("CVV") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("123") }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nombreTarjeta,
                onValueChange = { nombreTarjeta = it },
                label = { Text("Nombre en la Tarjeta") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (numeroTarjeta.length < 16 || fechaExpiracion.isEmpty() || cvv.length < 3 || nombreTarjeta.isEmpty()) {
                        Toast.makeText(contexto, "Por favor complete los datos de la tarjeta", Toast.LENGTH_SHORT).show()
                    } else {
                        alExitoPago(juego, numeroTarjeta.takeLast(4), nombreTarjeta)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pagar y Confirmar Arriendo")
            }
        }
    }
}

@Composable
fun PantallaLogin(modificador: Modifier, alExitoLogin: (User) -> Unit, alIrRegistro: () -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppBD.getDatabase(ctx)}

    Column(modifier = modificador, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Logo()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Bienvenido a Cycle", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = usuario, onValueChange = { usuario = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth(0.8f))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = contrasena, onValueChange = { contrasena = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(0.8f))
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            if (usuario.isBlank() || contrasena.isBlank()) {
                Toast.makeText(ctx, "Complete los campos", Toast.LENGTH_SHORT).show()
            }else {
                scope.launch(Dispatchers.IO){
                    val user = db.UsuarioDao().login(usuario, contrasena)
                    withContext(Dispatchers.Main){
                        if (user != null) {
                            alExitoLogin(user)
                        }else{
                            Toast.makeText(ctx,"Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }, modifier = Modifier.fillMaxWidth(0.8f)) { Text("Ingresar") }
        TextButton(onClick = alIrRegistro) { Text("Crear cuenta") }
    }
}

@Composable
fun PantallaRegistro(modificador: Modifier, alIrLogin: () -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppBD.getDatabase(ctx)}

    Column(modifier = modificador, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Logo()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Registro", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = usuario, onValueChange = { usuario = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth(0.8f))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(0.8f))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = contrasena, onValueChange = { contrasena = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(0.8f))
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            if (usuario.isBlank() || email.isBlank() || contrasena.isBlank()) {
                Toast.makeText(ctx, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(ctx, "Ingrese un correo válido (ej: usuario@gmail.com)", Toast.LENGTH_SHORT).show()
            } else {
                scope.launch(Dispatchers.IO){
                    val nuevoUsuario = User(usuario = usuario, email = email, contrasena = contrasena)
                    db.UsuarioDao().registrarUsuario(nuevoUsuario)
                    withContext(Dispatchers.Main){
                        Toast.makeText(ctx, "¡Registrado!", Toast.LENGTH_SHORT).show()
                        alIrLogin()
                    }
                }
            }
        }, modifier = Modifier.fillMaxWidth(0.8f)) { Text("Registrarse") }
        TextButton(onClick = alIrLogin) { Text("Ya tengo cuenta") }
    }
}

