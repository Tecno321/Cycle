package com.example.cycle

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cycle.model.Juego
import com.example.cycle.model.RegistroArriendo
import com.example.cycle.model.User
import com.example.cycle.ui.theme.CycleTheme
import com.example.cycle.view.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CycleTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AplicacionCycle()
                }
            }
        }
    }
}

@Composable
fun AplicacionCycle() {
    var esPantallaLogin by rememberSaveable { mutableStateOf(true) }
    var sesionIniciada by rememberSaveable { mutableStateOf(false) }
    var usuarioLogeado by remember { mutableStateOf<User?>(null)}
    var juegoSeleccionado by remember { mutableStateOf<Juego?>(null) }
    var vistaActual by remember { mutableStateOf("CATALOGO") } 
    
    val historialArriendos = remember { mutableStateListOf<RegistroArriendo>() }
    val contexto = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        if (sesionIniciada && usuarioLogeado != null) {
            when (vistaActual) {
                "PERFIL" -> PantallaPerfilUsuario(
                    usuario = usuarioLogeado!!,
                    alVolver = { vistaActual = "CATALOGO" },
                    alCerrarSesion = { sesionIniciada = false
                        usuarioLogeado = null
                        vistaActual = "CATALOGO" },
                    arriendosTotales = historialArriendos.size,
                    onUsuarioActualizado = { usuarioLogeado = it } // Sincronizamos el estado global
                )
                "MIS_ARRIENDOS" -> PantallaMisArriendos(
                    historial = historialArriendos,
                    alVolver = { vistaActual = "CATALOGO" },
                    alCancelarReserva = { registro ->
                        historialArriendos.remove(registro)
                        Toast.makeText(contexto, "Reserva cancelada con éxito", Toast.LENGTH_SHORT).show()
                    }
                )
                "PAGO" -> {
                    if (juegoSeleccionado != null) {
                        PantallaPago(
                            juego = juegoSeleccionado!!,
                            alVolver = { vistaActual = "CATALOGO" },
                            alExitoPago = { juego, ultimos4, nombre ->
                                historialArriendos.add(0, RegistroArriendo(
                                    nombreJuego = juego.nombre,
                                    urlImagen = juego.urlImagen,
                                    fecha = "Hoy",
                                    estado = "Listo para retiro",
                                    precio = juego.precio,
                                    ultimos4Tarjeta = ultimos4,
                                    nombreTarjeta = nombre
                                ))
                                juegoSeleccionado = null
                                vistaActual = "MIS_ARRIENDOS"
                                Toast.makeText(contexto, "¡Pago y Reserva Exitosa!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        vistaActual = "CATALOGO"
                    }
                }
                else -> {
                    if (juegoSeleccionado != null) {
                        PantallaDetalleJuego(
                            juego = juegoSeleccionado!!,
                            alVolver = { juegoSeleccionado = null },
                            alConfirmarReserva = { _ ->
                                vistaActual = "PAGO"
                            }
                        )
                    } else {
                        PantallaCatalogoJuegos(
                            alCerrarSesion = { sesionIniciada = false },
                            alClickJuego = { juegoSeleccionado = it },
                            alClickMisArriendos = { vistaActual = "MIS_ARRIENDOS" },
                            alClickPerfil = { vistaActual = "PERFIL" }
                        )
                    }
                }
            }
        } else {
            Crossfade(targetState = esPantallaLogin, label = "TransicionPantalla") { mostrarLogin ->
                if (mostrarLogin) {
                    PantallaLogin(
                        modificador = Modifier.fillMaxSize().padding(16.dp),
                        alExitoLogin = { user -> usuarioLogeado = user
                            sesionIniciada = true },
                        alIrRegistro = { esPantallaLogin = false }
                    )
                } else {
                    PantallaRegistro(
                        modificador = Modifier.fillMaxSize().padding(16.dp),
                        alIrLogin = { esPantallaLogin = true }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VistaPreviaAplicacionCycle() { CycleTheme { AplicacionCycle() } }
