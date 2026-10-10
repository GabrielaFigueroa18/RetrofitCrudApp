package com.example.retrofitcrudapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrearAlumnoActivity : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var edadEditText: EditText
    private lateinit var crearButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_alumno)

        nombreEditText = findViewById(R.id.editTextNombre)
        apellidoEditText = findViewById(R.id.editTextApellido)
        edadEditText = findViewById(R.id.editTextEdad)
        crearButton = findViewById(R.id.btnGuardar)

        crearButton.setOnClickListener {

            val nombre = nombreEditText.text.toString().trim()
            val apellido = apellidoEditText.text.toString().trim()
            val edadTexto = edadEditText.text.toString().trim()

            if (
                nombre.isEmpty() ||
                apellido.isEmpty() ||
                edadTexto.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Complete todos los campos",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val edad = edadTexto.toIntOrNull()

            if (edad == null || edad <= 0) {
                Toast.makeText(
                    this,
                    "Ingrese una edad válida",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val alumno = Alumno(
                0,
                nombre,
                apellido,
                edad
            )

            crearButton.isEnabled = false

            RetrofitClient.instance.crearAlumno(alumno)
                .enqueue(object : Callback<Alumno> {

                    override fun onResponse(
                        call: Call<Alumno>,
                        response: Response<Alumno>
                    ) {
                        crearButton.isEnabled = true

                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@CrearAlumnoActivity,
                                "Alumno creado exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(
                                this@CrearAlumnoActivity,
                                MainActivity::class.java
                            )

                            startActivity(intent)
                            finish()

                        } else {

                            val error = response.errorBody()?.string()

                            Log.e(
                                "API",
                                "Error HTTP ${response.code()}: $error"
                            )

                            Toast.makeText(
                                this@CrearAlumnoActivity,
                                "Error al crear el alumno (${response.code()})",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Alumno>, t: Throwable) {
                        Log.e("ERROR_API", "Error al conectar con la API", t)

                        Toast.makeText(
                            this@CrearAlumnoActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }
}
