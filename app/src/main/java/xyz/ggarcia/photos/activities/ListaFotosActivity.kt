package xyz.ggarcia.photos.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import xyz.ggarcia.photos.R
import xyz.ggarcia.photos.data.DatabaseHelper
import xyz.ggarcia.photos.data.Foto
import xyz.ggarcia.photos.utils.Imagenes
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListaFotosActivity : AppCompatActivity() {

    //Declaración de variables
    lateinit var spinner: Spinner
    lateinit var imageView: ImageView
    lateinit var textViewComentario: TextView
    lateinit var textViewUbicacion: TextView
    lateinit var textViewFecha: TextView
    lateinit var db: DatabaseHelper

    var usuarioId: Long = -1L
    var lista = ArrayList<Foto>()
    var fotoSeleccionada: Foto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_fotos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Enlazar con la vista
        spinner = findViewById(R.id.spinner)
        imageView = findViewById(R.id.imageView2)
        textViewComentario = findViewById(R.id.textViewComentario)
        textViewUbicacion = findViewById(R.id.textViewUbicacion)
        textViewFecha = findViewById(R.id.textViewFecha)

        db = DatabaseHelper(this)
        usuarioId = intent.getLongExtra("usuarioId", -1L)

        listarLasFotos()
    }

    private fun listarLasFotos() {
        //Consulta la lista en la base de datos
        lista = db.obtenerFotos(usuarioId)

        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val listaDeNombre = ArrayList<String>()

        //Posición 0 = "Seleccione", por eso abajo se usa position - 1
        if (lista.isEmpty()) {
            listaDeNombre.add("No hay fotos guardadas")
        } else {
            listaDeNombre.add("Seleccione una foto")
        }
        for (foto in lista) {
            listaDeNombre.add(formato.format(Date(foto.fecha)) + " - " + foto.comentario)
        }

        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            listaDeNombre
        )
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    limpiar()
                } else {
                    mostrarFoto(lista[position - 1])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                limpiar()
            }
        }
    }

    //Muestra la foto seleccionada con su comentario y ubicación GPS
    private fun mostrarFoto(foto: Foto) {
        fotoSeleccionada = foto

        val bitmap = if (File(foto.ruta).exists()) Imagenes.cargar(foto.ruta) else null
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        textViewComentario.text = "Comentario: " + foto.comentario

        if (foto.latitud != null && foto.longitud != null) {
            textViewUbicacion.text =
                "📍 Latitud: ${foto.latitud}\n📍 Longitud: ${foto.longitud}"
        } else {
            textViewUbicacion.text = "Ubicación no disponible"
        }

        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        textViewFecha.text = "Fecha: " + formato.format(Date(foto.fecha))
    }

    private fun limpiar() {
        fotoSeleccionada = null
        imageView.setImageResource(R.drawable.ic_launcher_foreground)
        textViewComentario.text = "Sin comentario"
        textViewUbicacion.text = "Ubicación no disponible"
        textViewFecha.text = "Fecha no disponible"
    }

    //Al tocar la foto: se abre en el visor de imágenes del teléfono
    fun abrirFotoZoom(view: View) {
        val foto = fotoSeleccionada ?: return
        val archivo = File(foto.ruta)
        if (!archivo.exists()) {
            Toast.makeText(this, "No se encontró el archivo de la foto", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                archivo
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No hay una app para ver imágenes", Toast.LENGTH_SHORT).show()
        }
    }

    //Botón "Regresar"
    fun regresar(view: View) {
        finish()
    }
}