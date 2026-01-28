package com.example.cycle.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cycle.model.Juego

@Composable
fun PantallaCatalogoJuegos(alCerrarSesion: () -> Unit, alClickJuego: (Juego) -> Unit, alClickMisArriendos: () -> Unit, alClickPerfil: () -> Unit) {
    val juegos = remember {
        listOf(
            Juego(1, "Super Mario Odyssey", "$3.500", 3500, "Aventura", "https://m.media-amazon.com/images/I/71XZsDkAuNL._AC_UF894,1000_QL80_.jpg", "Arrienda esta joya de Nintendo.", "7 Días", "Providencia, Stgo Centro"),
            Juego(2, "The Legend of Zelda", "$4.500", 4500, "RPG", "https://cdn.europosters.eu/image/1300/canvas-print-the-legend-of-zelda-breath-of-the-wild-view-i111060.jpg", "Mundo abierto épico.", "7 Días", "Las Condes, Vitacura"),
            Juego(3, "Mario Kart 8", "$3.000", 3000, "Carreras", "https://images-cdn.ubuy.co.id/66c0fe8ef5d6160a260a0d90-pyramid-america-mario-kart-8-deluxe.jpg", "¡Perfecto para jugar con amigos!", "3 Días", "Maipú, La Florida"),
            Juego(4, "FC 24 (FIFA)", "$5.000", 5000, "Deportes", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQh_J4VwaFN4cj1vUTXkmrC6mSCsi5mvTEzHw&s", "La última tecnología en fútbol.", "5 Días", "Mall Vespucio, Mall Oeste"),
            Juego(5, "Minecraft", "$2.500", 2500, "Sandbox", "https://cdnx.jumpseller.com/deus-digital/image/26018632/thumb/719/719?1659480453", "Diversión infinita.", "10 Días", "Viña del Mar, Concepción"),
            Juego(6, "Elden Ring", "$4.000", 4000, "Acción", "https://cdn01.pinkoi.com/product/24rsWqPA/0/1/640x530.jpg", "Un desafío para valientes.", "7 Días", "Santiago Centro")
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Videojuegos disponibles", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Row {
                IconButton(onClick = alClickPerfil) { Icon(Icons.Default.Person, "Perfil") }
                IconButton(onClick = alClickMisArriendos) { Icon(Icons.AutoMirrored.Filled.List, "Mis Arriendos") }
                IconButton(onClick = alCerrarSesion) { Icon(Icons.AutoMirrored.Filled.ExitToApp, "Salir") }
            }
        }

        SeccionNoticiasSteam()

        Spacer(modifier = Modifier.height(8.dp))
        Text("Catálogo de Arriendo", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(12.dp), modifier = Modifier.weight(1f)) {
            items(juegos) { juego ->
                Card(modifier = Modifier.padding(4.dp), onClick = { alClickJuego(juego) }) {
                    Column {
                        AsyncImage(model = juego.urlImagen, contentDescription = null, modifier = Modifier.fillMaxWidth().height(150.dp), contentScale = ContentScale.Crop)
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(juego.nombre, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(juego.precio, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { alClickJuego(juego) }, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
                                Text("Arrendar", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaDetalleJuego(juego: Juego, alVolver: () -> Unit, alConfirmarReserva: (Juego) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.padding(8.dp)) { IconButton(onClick = alVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
        AsyncImage(model = juego.urlImagen, contentDescription = null, modifier = Modifier.fillMaxWidth().height(300.dp))
        Column(modifier = Modifier.padding(16.dp)) {
            Text(juego.nombre, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("${juego.precio} (CLP)", fontSize = 24.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Sobre el Juego", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(juego.descripcion, modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary)
                Text("Periodo de Arriendo", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
            }
            Text(juego.periodoArriendo, modifier = Modifier.padding(start = 32.dp))

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                Text("Puntos de Retiro y Devolución", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
            }
            Text(juego.puntosRetiro, modifier = Modifier.padding(start = 32.dp))

            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { alConfirmarReserva(juego) }, modifier = Modifier.fillMaxWidth()) { Text("Confirmar Reserva") }
        }
    }
}
