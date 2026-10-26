package xyz.ggarcia.photos.data

//Representa un registro de la tabla "fotos"
data class Foto(
    val id: Long,
    val usuarioId: Long,
    val ruta: String,
    val comentario: String,
    val latitud: Double?,
    val longitud: Double?,
    val fecha: Long
)