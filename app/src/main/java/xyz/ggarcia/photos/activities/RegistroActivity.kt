package xyz.ggarcia.photos.activities

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import xyz.ggarcia.photos.R
import xyz.ggarcia.photos.data.DatabaseHelper

class RegistroActivity : AppCompatActivity() {

    //Declaración de variables
    lateinit var editTextNombre: EditText
    lateinit var editTextApellidoPaterno: EditText
    lateinit var editTextApellidoMaterno: EditText
    lateinit var editTextCorreo: EditText
    lateinit var editTextContrasenia: EditText
    lateinit var editTextConfirmar: EditText
    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Enlazar con la vista
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextApellidoPaterno = findViewById(R.id.editTextApellidoPaterno)
        editTextApellidoMaterno = findViewById(R.id.editTextApellidoMaterno)
        editTextCorreo = findViewById(R.id.editTextCorreo)
        editTextContrasenia = findViewById(R.id.editTextContrasenia)
        editTextConfirmar = findViewById(R.id.editTextConfirmar)
        db = DatabaseHelper(this)
    }

    //Botón "Crear mi cuenta"
    fun registrar(view: View) {
        val nombre = editTextNombre.text.toString().trim()
        val apellidoPaterno = editTextApellidoPaterno.text.toString().trim()
        val apellidoMaterno = editTextApellidoMaterno.text.toString().trim()
        val correo = editTextCorreo.text.toString().trim()
        val contrasenia = editTextContrasenia.text.toString()
        val confirmar = editTextConfirmar.text.toString()

        //Validaciones
        if (nombre.isEmpty()) {
            editTextNombre.error = "Requerido"; return
        }
        if (apellidoPaterno.isEmpty()) {
            editTextApellidoPaterno.error = "Requerido"; return
        }
        if (apellidoMaterno.isEmpty()) {
            editTextApellidoMaterno.error = "Requerido"; return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            editTextCorreo.error = "Correo no válido"; return
        }
        if (contrasenia.length < 6) {
            editTextContrasenia.error = "Mínimo 6 caracteres"; return
        }
        if (contrasenia != confirmar) {
            editTextConfirmar.error = "Las contraseñas no coinciden"; return
        }
        if (db.existeCorreo(correo)) {
            editTextCorreo.error = "Este correo ya está registrado"; return
        }

        //Guardar en la base de datos local
        val id = db.registrarUsuario(nombre, apellidoPaterno, apellidoMaterno, correo, contrasenia)
        if (id == -1L) {
            Toast.makeText(this, "No se pudo registrar el usuario", Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(this, "Cuenta creada. Ahora inicie sesión.", Toast.LENGTH_LONG).show()
        finish() //Regresa al inicio de sesión
    }

    //Botón "Ya tengo cuenta · Iniciar sesión"
    fun regresar(view: View) {
        finish()
    }
}