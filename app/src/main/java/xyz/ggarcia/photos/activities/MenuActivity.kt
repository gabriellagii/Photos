package xyz.ggarcia.photos.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import xyz.ggarcia.photos.MainActivity
import xyz.ggarcia.photos.R
import xyz.ggarcia.photos.data.DatabaseHelper
import xyz.ggarcia.photos.utils.Imagenes
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MenuActivity : AppCompatActivity() {

    //Declaración de variables
    lateinit var textViewNombre: TextView
    lateinit var layoutDatosFoto: LinearLayout
    lateinit var imageViewFoto: ImageView
    lateinit var textViewUbicacion: TextView
    lateinit var editTextComentario: EditText
    lateinit var buttonGuardar: Button
    lateinit var db: DatabaseHelper
    lateinit var fusedLocation: FusedLocationProviderClient

    var usuarioId: Long = -1L
    var rutaFoto: String? = null
    var fotoPendiente = false   //true = foto tomada pero aún no guardada en la BD
    var latitud: Double? = null
    var longitud: Double? = null

    //Evento que abre la cámara y regresa si se tomó la foto
    private val tomarFotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        val ruta = rutaFoto
        if (isSuccess && ruta != null && File(ruta).length() > 0) {
            fotoPendiente = true
            editTextComentario.setText("")
            mostrarFoto()
            obtenerUbicacion()
        } else {
            //Se canceló: borrar el archivo vacío
            ruta?.let { File(it).delete() }
            rutaFoto = null
            Toast.makeText(this, "No se tomó la foto", Toast.LENGTH_SHORT).show()
        }
    }

    //Evento que pide permiso de ubicación y después abre la cámara
    private val permisoUbicacionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.none { it }) {
            Toast.makeText(
                this, "Sin permiso de ubicación la foto no tendrá GPS", Toast.LENGTH_LONG
            ).show()
        }
        abrirCamara()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Enlazar con la vista
        textViewNombre = findViewById(R.id.textViewNombre)
        layoutDatosFoto = findViewById(R.id.layoutDatosFoto)
        imageViewFoto = findViewById(R.id.imageViewFoto)
        textViewUbicacion = findViewById(R.id.textViewUbicacion)
        editTextComentario = findViewById(R.id.editTextComentario)
        buttonGuardar = findViewById(R.id.buttonGuardar)

        db = DatabaseHelper(this)
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        //Id del usuario que inició sesión (viene del login)
        usuarioId = intent.getLongExtra("usuarioId", -1L)
        textViewNombre.text = db.obtenerNombreCompleto(usuarioId)

        //Tocar la ubicación vuelve a intentar obtener el GPS
        textViewUbicacion.setOnClickListener {
            if (fotoPendiente) obtenerUbicacion()
        }

        //Recuperar datos si la pantalla se recreó (rotación o al volver de la cámara)
        if (savedInstanceState != null) {
            rutaFoto = savedInstanceState.getString("rutaFoto")
            fotoPendiente = savedInstanceState.getBoolean("fotoPendiente")
            if (savedInstanceState.containsKey("latitud")) latitud = savedInstanceState.getDouble("latitud")
            if (savedInstanceState.containsKey("longitud")) longitud = savedInstanceState.getDouble("longitud")
            if (fotoPendiente) {
                mostrarFoto()
                mostrarUbicacion()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("rutaFoto", rutaFoto)
        outState.putBoolean("fotoPendiente", fotoPendiente)
        latitud?.let { outState.putDouble("latitud", it) }
        longitud?.let { outState.putDouble("longitud", it) }
    }

    // ===================== BOTONES =====================

    //Botón "Tomar foto"
    fun tomarFoto(view: View) {
        if (tienePermisoUbicacion()) {
            abrirCamara()
        } else {
            permisoUbicacionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    //Botón "Guardar foto": registra en la BD la ruta, comentario y ubicación GPS
    fun guardarFoto(view: View) {
        val ruta = rutaFoto ?: return
        val comentario = editTextComentario.text.toString().trim()
        if (comentario.isEmpty()) {
            editTextComentario.error = "Escriba un comentario"
            return
        }

        val id = db.guardarFoto(usuarioId, ruta, comentario, latitud, longitud)
        if (id == -1L) {
            Toast.makeText(this, "Error al guardar la foto", Toast.LENGTH_LONG).show()
            return
        }

        fotoPendiente = false
        rutaFoto = null
        latitud = null
        longitud = null
        editTextComentario.setText("")
        layoutDatosFoto.visibility = View.GONE
        Toast.makeText(this, "Foto guardada", Toast.LENGTH_SHORT).show()
    }

    //Botón "Ver mis fotos"
    fun verFotos(view: View) {
        val intent = Intent(this, ListaFotosActivity::class.java)
        intent.putExtra("usuarioId", usuarioId)
        startActivity(intent)
    }

    //Botón "Cerrar sesión"
    fun cerrarSesion(view: View) {
        //Si hay una foto sin guardar se elimina
        if (fotoPendiente) rutaFoto?.let { File(it).delete() }

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // ===================== CÁMARA =====================

    //Crea el archivo vacío en la carpeta de la app donde la cámara guardará la foto
    //Carpeta: Android/data/xyz.ggarcia.photos/files/Pictures
    fun crearArchivo(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val almacen = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (almacen == null) {
            Toast.makeText(this, "No se encontró la carpeta de fotos", Toast.LENGTH_LONG).show()
            return null
        }
        if (!almacen.exists()) almacen.mkdirs()

        return try {
            File.createTempFile("FOTO_${timeStamp}_", ".jpg", almacen)
        } catch (e: Exception) {
            Log.e("FOTOS", "Error al crear archivo", e)
            Toast.makeText(this, "Error al crear archivo: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
    }

    private fun abrirCamara() {
        //Si había una foto tomada sin guardar, se borra
        if (fotoPendiente) rutaFoto?.let { File(it).delete() }
        fotoPendiente = false
        layoutDatosFoto.visibility = View.GONE
        latitud = null
        longitud = null

        try {
            val archivo = crearArchivo() ?: return
            rutaFoto = archivo.absolutePath
            val uri: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                archivo
            )
            tomarFotoLauncher.launch(uri)
        } catch (e: Exception) {
            Log.e("FOTOS", "Error al abrir la cámara", e)
            Toast.makeText(this, "Error al abrir la cámara: ${e.message}", Toast.LENGTH_LONG).show()
            rutaFoto = null
        }
    }

    //Muestra la foto en la pantalla principal
    private fun mostrarFoto() {
        val ruta = rutaFoto ?: return
        val bitmap = Imagenes.cargar(ruta)
        if (bitmap != null) {
            imageViewFoto.setImageBitmap(bitmap)
        } else {
            imageViewFoto.setImageResource(android.R.drawable.ic_menu_report_image)
        }
        layoutDatosFoto.visibility = View.VISIBLE
    }

    // ===================== UBICACIÓN GPS =====================

    private fun tienePermisoUbicacion(): Boolean {
        val fino = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val aproximado = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fino == PackageManager.PERMISSION_GRANTED || aproximado == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission") //El permiso se valida con tienePermisoUbicacion()
    private fun obtenerUbicacion() {
        if (!tienePermisoUbicacion()) {
            textViewUbicacion.text = "Ubicación: sin permiso"
            return
        }

        textViewUbicacion.text = "Obteniendo ubicación GPS..."
        buttonGuardar.isEnabled = false

        fusedLocation.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            if (location != null) {
                latitud = location.latitude
                longitud = location.longitude
                mostrarUbicacion()
                buttonGuardar.isEnabled = true
            } else {
                //Si no hay ubicación actual, se usa la última conocida
                fusedLocation.lastLocation.addOnCompleteListener { tarea ->
                    val ultima = if (tarea.isSuccessful) tarea.result else null
                    latitud = ultima?.latitude
                    longitud = ultima?.longitude
                    mostrarUbicacion()
                    buttonGuardar.isEnabled = true
                }
            }
        }.addOnFailureListener { e ->
            Log.e("FOTOS", "Error al obtener ubicación", e)
            mostrarUbicacion()
            buttonGuardar.isEnabled = true
        }
    }

    private fun mostrarUbicacion() {
        if (latitud != null && longitud != null) {
            textViewUbicacion.text = "📍 Latitud: $latitud\n📍 Longitud: $longitud"
        } else {
            textViewUbicacion.text =
                "Ubicación no disponible. Active el GPS y toque aquí para reintentar."
        }
    }
}