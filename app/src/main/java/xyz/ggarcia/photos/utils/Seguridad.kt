package xyz.ggarcia.photos.utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

//La contraseña no se guarda tal cual: se guarda "sal:hash" (SHA-256)
object Seguridad {

    fun hashContrasenia(contrasenia: String): String {
        val sal = ByteArray(16)
        SecureRandom().nextBytes(sal)
        val hash = calcular(sal, contrasenia)
        val encoder = Base64.getEncoder()
        return encoder.encodeToString(sal) + ":" + encoder.encodeToString(hash)
    }

    fun verificarContrasenia(contrasenia: String, guardado: String): Boolean {
        val partes = guardado.split(":")
        if (partes.size != 2) return false
        val decoder = Base64.getDecoder()
        val sal = decoder.decode(partes[0])
        val hashGuardado = decoder.decode(partes[1])
        return MessageDigest.isEqual(hashGuardado, calcular(sal, contrasenia))
    }

    private fun calcular(sal: ByteArray, contrasenia: String): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(sal)
        return md.digest(contrasenia.toByteArray(Charsets.UTF_8))
    }
}