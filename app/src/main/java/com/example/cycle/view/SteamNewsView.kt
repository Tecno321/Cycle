package com.example.cycle.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cycle.model.NewsItem
import com.example.cycle.model.SteamApiService
import kotlinx.coroutines.launch

@Composable
fun SeccionNoticiasSteam() {
    val scope = rememberCoroutineScope()
    var noticias by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val api = SteamApiService.create()
                val ids = listOf(440, 570, 730)
                val todasLasNoticias = mutableListOf<NewsItem>()
                
                ids.forEach { id ->
                    try {
                        val response = api.getNewsForApp(appId = id, count = 2)
                        todasLasNoticias.addAll(response.appnews.newsitems)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                noticias = todasLasNoticias.shuffled()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cargando = false
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Novedades de Steam",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (cargando) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else if (noticias.isEmpty()) {
            Text("No se pudieron cargar las noticias")
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(noticias) { item ->
                    NoticiaCard(item)
                }
            }
        }
    }
}

@Composable
fun NoticiaCard(item: NewsItem) {
    var expandida by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(280.dp)
            .animateContentSize()
            .clickable { expandida = !expandida },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = if (expandida) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "AppID: ${item.appid}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))
            
            val contenidoLimpio = remember(item.contents) {
                item.contents.replace(Regex("<[^>]*>"), "")
            }

            Text(
                text = contenidoLimpio,
                fontSize = 12.sp,
                maxLines = if (expandida) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )
            
            if (!expandida) {
                Text(
                    text = "Leer más...",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
