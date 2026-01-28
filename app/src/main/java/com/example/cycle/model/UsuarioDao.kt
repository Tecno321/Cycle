package com.example.cycle.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {
    @Insert
    suspend fun registrarUsuario(user: User)

    @Query("SELECT * FROM usuarios WHERE usuario = :nombreUsuario AND contrasena = :pass LIMIT 1")
    suspend fun login(nombreUsuario: String, pass: String): User?

    @Query("SELECT * FROM usuarios WHERE id = :userId LIMIT 1")
    suspend fun obtenerUsuarioPorId(userId: Int): User?

    @Query("UPDATE usuarios SET fotoUri = :uri WHERE id = :userId")
    suspend fun actualizarFotoPerfil(userId: Int, uri: String)
}
