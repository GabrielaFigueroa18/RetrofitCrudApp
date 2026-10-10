package com.example.retrofitcrudapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ActualizarAlumnoActivity : AppCompatActivity() {

    private lateinit var api: AlumnoApi

    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var edadEditText: EditText
    private lateinit var actualizarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_alumno)

        nombreEditText = findViewById(R.id.nombreEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        edadEditText = findViewById(R.id.edadEditText)
        actualizarButton = findViewById(R.id.actualizarButton)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://66240d4504457d4aaf9b8530.mockapi.io/api/")
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

        api = retrofit.create(AlumnoApi::class.java)

        val alumnoId = intent.getStringExtra("alumno_id")
            ?.toIntOrNull()
            ?: intent.getIntExtra("alumno_id", -1)

        val nombre = intent.getStringExtra("nombre") ?: ""
        val apellido = intent.getStringExtra("apellido") ?: ""
        val edad = intent.getIntExtra("edad", 0)

        Log.d("API", "ID recibido: $alumnoId")

        nombreEditText.setText(nombre)
        apellidoEditText.setText(apellido)

        if (edad > 0) {
            edadEditText.setText(edad.toString())
        }

        actualizarButton.setOnClickListener {

            if (alumnoId == -1) {
                Toast.makeText(
                    this,
                    "ID del alumno no válido",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val nombreNuevo =
                nombreEditText.text.toString().trim()

            val apellidoNuevo =
                apellidoEditText.text.toString().trim()

            val edadTexto =
                edadEditText.text.toString().trim()

            if (
                nombreNuevo.isEmpty() ||
                apellidoNuevo.isEmpty() ||
                edadTexto.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Complete todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val edadNueva = edadTexto.toIntOrNull()

            if (edadNueva == null || edadNueva <= 0) {
                Toast.makeText(
                    this,
                    "Ingrese una edad válida",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val alumnoActualizado = Alumno(
                alumnoId,
                nombreNuevo,
                apellidoNuevo,
                edadNueva
            )

            api.actualizarAlumno(
                alumnoId,
                alumnoActualizado
            ).enqueue(object : Callback<Alumno> {

                override fun onResponse(
                    call: Call<Alumno>,
                    response: Response<Alumno>
                ) {
                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@ActualizarAlumnoActivity,
                            "Alumno actualizado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(
                            this@ActualizarAlumnoActivity,
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

                        var mensaje = "Error al actualizar el alumno"

                        try {
                            if (!error.isNullOrEmpty()) {
                                val json = JSONObject(error)
                                mensaje = json.optString(
                                    "message",
                                    mensaje
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("API", "Error al leer respuesta", e)
                        }

                        Toast.makeText(
                            this@ActualizarAlumnoActivity,
                            mensaje,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<Alumno>,
                    t: Throwable
                ) {
                    Log.e("API", "Error de conexión", t)

                    Toast.makeText(
                        this@ActualizarAlumnoActivity,
                        "Error de conexión: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }
}
