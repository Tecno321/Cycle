package com.example.cycle.model

data class RegistroArriendo(val nombreJuego: String, val urlImagen: String, val fecha: String, val estado: String,
                            val precio: String, val ultimos4Tarjeta: String, val nombreTarjeta: String,
                            val codigoQR: String = "REF-${(1000..9999).random()}")
