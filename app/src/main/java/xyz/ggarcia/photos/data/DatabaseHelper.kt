package xyz.ggarcia.photos.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import xyz.ggarcia.photos.utils.Seguridad

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "photos.db", null, 1) {

    //Se ejecuta solo la primera vez que se crea la base de datos
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido_paterno TEXT NOT NULL,
                apellido_materno TEXT NOT NULL,
                correo TEXT NOT NULL UNIQUE COLLATE NOCASE,
                contrasenia TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE fotos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                ruta TEXT NOT NULL,
                comentario TEXT NOT NULL,
                latitud REAL,
                longitud REAL,
                fecha INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS fotos")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    // ===================== USUARIOS =====================

    //Revisa si el correo ya está registrado
    fun existeCorreo(correo: String): Boolean {
        val cursor = readableDatabase.rawQuery(
            "SELECT id FROM usuarios WHERE correo = ?",
            arrayOf(correo.trim())
        )
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    //Inserta el usuario. Regresa el id nuevo o -1 si hubo error
    fun registrarUsuario(
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        correo: String,
        contrasenia: String
    ): Long {
        val valores = ContentValues()
        valores.put("nombre", nombre.trim())
        valores.put("apellido_paterno", apellidoPaterno.trim())
        valores.put("apellido_materno", apellidoMaterno.trim())
        valores.put("correo", correo.trim().lowercase())
        valores.put("contrasenia", Seguridad.hashContrasenia(contrasenia))
        return writableDatabase.insert("usuarios", null, valores)
    }

    //Compara correo y contraseña. Regresa el id del usuario o -1 si no coincide
    fun iniciarSesion(correo: String, contrasenia: String): Long {
        val cursor = readableDatabase.rawQuery(
            "SELECT id, contrasenia FROM usuarios WHERE correo = ?",
            arrayOf(correo.trim())
        )
        var id = -1L
        if (cursor.moveToFirst()) {
            if (Seguridad.verificarContrasenia(contrasenia, cursor.getString(1))) {
                id = cursor.getLong(0)
            }
        }
        cursor.close()
        return id
    }

    //Nombre completo para el mensaje de bienvenida
    fun obtenerNombreCompleto(id: Long): String {
        val cursor = readableDatabase.rawQuery(
            "SELECT nombre, apellido_paterno, apellido_materno FROM usuarios WHERE id = ?",
            arrayOf(id.toString())
        )
        var nombre = ""
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2)
        }
        cursor.close()
        return nombre
    }

    // ===================== FOTOS =====================

    //Guarda la foto con su ubicación GPS y comentario. Regresa el id o -1 si hubo error
    fun guardarFoto(
        usuarioId: Long,
        ruta: String,
        comentario: String,
        latitud: Double?,
        longitud: Double?
    ): Long {
        val valores = ContentValues()
        valores.put("usuario_id", usuarioId)
        valores.put("ruta", ruta)
        valores.put("comentario", comentario.trim())
        if (latitud != null) valores.put("latitud", latitud) else valores.putNull("latitud")
        if (longitud != null) valores.put("longitud", longitud) else valores.putNull("longitud")
        valores.put("fecha", System.currentTimeMillis())
        return writableDatabase.insert("fotos", null, valores)
    }

    //Consulta la lista de fotos del usuario (la más reciente primero)
    fun obtenerFotos(usuarioId: Long): ArrayList<Foto> {
        val lista = ArrayList<Foto>()
        val cursor = readableDatabase.rawQuery(
            "SELECT id, usuario_id, ruta, comentario, latitud, longitud, fecha " +
                    "FROM fotos WHERE usuario_id = ? ORDER BY fecha DESC",
            arrayOf(usuarioId.toString())
        )
        while (cursor.moveToNext()) {
            lista.add(
                Foto(
                    id = cursor.getLong(0),
                    usuarioId = cursor.getLong(1),
                    ruta = cursor.getString(2),
                    comentario = cursor.getString(3),
                    latitud = if (cursor.isNull(4)) null else cursor.getDouble(4),
                    longitud = if (cursor.isNull(5)) null else cursor.getDouble(5),
                    fecha = cursor.getLong(6)
                )
            )
        }
        cursor.close()
        return lista
    }
}