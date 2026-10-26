package xyz.ggarcia.photos

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import xyz.ggarcia.photos.activities.MenuActivity
import xyz.ggarcia.photos.activities.RegistroActivity
import xyz.ggarcia.photos.data.DatabaseHelper

class MainActivity : AppCompatActivity() {

    //Declaración de variables
    lateinit var editTextCorreo: EditText
    lateinit var editTextContrasenia: EditText
    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Enlazar con la vista
        editTextCorreo = findViewById(R.id.editTextCorreo)
        editTextContrasenia = findViewById(R.id.editTextContrasenia)
        db = DatabaseHelper(this)
    }

    //Botón "Iniciar sesión": compara correo y contraseña con la base de datos local
    fun iniciarSesion(view: View) {
        val correo = editTextCorreo.text.toString().trim()
        val contrasenia = editTextContrasenia.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            editTextCorreo.error = "Correo no válido"
            return
        }
        if (contrasenia.isEmpty()) {
            editTextContrasenia.error = "Ingrese su contraseña"
            return
        }

        val usuarioId = db.iniciarSesion(correo, contrasenia)
        if (usuarioId == -1L) {
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show()
            return
        }

        //Ir a la pantalla principal enviando el id del usuario
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("usuarioId", usuarioId)
        startActivity(intent)
        finish()
    }

    //Botón "Crear cuenta nueva"
    fun crearCuenta(view: View) {
        startActivity(Intent(this, RegistroActivity::class.java))
    }
}