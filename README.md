📸 PHOTOS

Aplicación Android para capturar, almacenar y consultar fotografías con ubicación GPS

<!-- <p align="center">
  <img src="docs/photos-presentacion.png" alt="Presentación de la aplicación Photos" width="850">
</p> -->

<p align="center">
  <img src="https://img.shields.io/badge/Plataforma-Android-3DDC84?logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Lenguaje-Kotlin-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Base%20de%20datos-SQLite-003B57?logo=sqlite&logoColor=white" alt="SQLite">
  <img src="https://img.shields.io/badge/Repositorio-GitHub-181717?logo=github&logoColor=white" alt="GitHub">
</p>

📋 Descripción del proyecto

Photos es una aplicación móvil desarrollada en Kotlin para Android que permite registrar usuarios, iniciar sesión, capturar fotografías y consultar las imágenes almacenadas.

Cada fotografía puede asociarse con un comentario, una fecha y las coordenadas GPS disponibles. La información se almacena localmente mediante SQLite y los archivos de imagen se guardan en el directorio de la aplicación.

🎯 Funcionalidades principales

👤

Registro de usuarios

Registro de nombre, apellidos, correo y contraseña.

🔐

Inicio de sesión

Validación de credenciales con SQLite.

📷

Captura de fotografías

Uso de la cámara del dispositivo.

📍

Ubicación GPS

Obtención de latitud y longitud cuando está disponible.

💾

Almacenamiento

Guardado de imágenes y sus datos asociados.

🖼️

Consulta de fotografías

Visualización de imágenes, comentarios y ubicación.

🔎

Detalle de fotografía

Selección y ampliación de imágenes.

🚪

Cerrar sesión

Salida de la cuenta actual.

🛠️ Tecnologías utilizadas

🤖 Android Studio

💜 Kotlin

🗄️ SQLite

📍 Google Play Services Location

📸 Cámara de Android

🔒 SHA-256 con sal para el tratamiento de contraseñas

🌐 Git y GitHub

📱 Pantallas de la aplicación

Inicio de sesión

Registro

Validación de correo y contraseña.

Formulario de creación de cuenta.

Menú principal

Galería

Captura, vista previa, comentario y ubicación.

Consulta de fotografías y sus detalles.

Para mostrar capturas reales en esta sección, agrega las imágenes a docs/ y utiliza etiquetas Markdown como ![Inicio de sesión](docs/login.png).

🗃️ Base de datos

La aplicación utiliza photos.db, administrada por DatabaseHelper.kt.

Tabla usuarios

id: identificador.

nombre: nombre del usuario.

apellido_paterno: apellido paterno.

apellido_materno: apellido materno.

correo: correo electrónico.

contrasenia: hash de contraseña con sal.

Tabla fotos

id: identificador de la fotografía.

usuario_id: usuario propietario.

ruta: ubicación del archivo.

comentario: descripción de la imagen.

latitud: coordenada geográfica.

longitud: coordenada geográfica.

fecha: fecha del registro en milisegundos Unix.

🧩 Estructura del proyecto

<img width="487" height="616" alt="estructura_photos" src="https://github.com/user-attachments/assets/25bf6a01-c4c5-40fb-80d3-96b99cb79a49" />


🔑 Permisos de Android

📷 Cámara: captura de fotografías.

📍 ACCESS_FINE_LOCATION: ubicación precisa.

📍 ACCESS_COARSE_LOCATION: ubicación aproximada.

La ubicación puede no estar disponible si el usuario no concede los permisos o el dispositivo no obtiene coordenadas.

🚀 Instalación

Clona el repositorio:

git clone https://github.com/gabriellagii/Photos.git

Abre la carpeta Photos en Android Studio.

Espera a que finalice la sincronización de Gradle.

Conecta un dispositivo Android o inicia un emulador.

Ejecuta la aplicación con Run.

🖌️ Bocetos en Balsamiq
1. Inicio de sesión.
<p align="center"><img width="338" height="682" alt="photos_1" src="https://github.com/user-attachments/assets/ed67c725-94ac-4afb-9895-d1d2f1006232" /></p>


2. Registro de usuario.
<p align="center"><img width="337" height="687" alt="photos_2" src="https://github.com/user-attachments/assets/4c5c0816-9a84-41dc-9240-680e008238e5" /></p>

3. Pantalla principal
<p align="center"><img width="336" height="687" alt="photos_3" src="https://github.com/user-attachments/assets/ea55f833-d2d4-4a7b-a2bb-e0064379cf08" /></p>

4. Foto nueva
<p align="center"><img width="338" height="686" alt="photos_4" src="https://github.com/user-attachments/assets/c61678b0-5759-4504-be30-d2a4094b3394" /></p>

5. Ver fotos
<p align="center"><img width="336" height="686" alt="photos_5" src="https://github.com/user-attachments/assets/a57258f6-a344-4eb1-b080-3cf7e966b1ce" /></p>


📂 Repositorio

🔗 GitHub: https://github.com/gabriellagii/Photos


<p align="center">
  📸 <strong>PHOTOS</strong> — Captura, almacena y consulta tus fotografías.
</p>
