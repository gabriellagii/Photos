package xyz.ggarcia.photos.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface

//Carga la foto reducida (las fotos del celular son muy grandes y pueden cerrar la app)
//y la gira según la orientación con la que la guardó la cámara
object Imagenes {

    fun cargar(ruta: String, tamanoMax: Int = 1080): Bitmap? {
        val opciones = BitmapFactory.Options()
        opciones.inJustDecodeBounds = true
        BitmapFactory.decodeFile(ruta, opciones)
        if (opciones.outWidth <= 0 || opciones.outHeight <= 0) return null

        var escala = 1
        while (opciones.outWidth / (escala * 2) >= tamanoMax ||
            opciones.outHeight / (escala * 2) >= tamanoMax
        ) {
            escala *= 2
        }

        val opcionesReducidas = BitmapFactory.Options()
        opcionesReducidas.inSampleSize = escala
        val bitmap = BitmapFactory.decodeFile(ruta, opcionesReducidas) ?: return null

        val grados = try {
            when (ExifInterface(ruta).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } catch (e: Exception) {
            0f
        }

        if (grados == 0f) return bitmap
        val matriz = Matrix()
        matriz.postRotate(grados)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
    }
}