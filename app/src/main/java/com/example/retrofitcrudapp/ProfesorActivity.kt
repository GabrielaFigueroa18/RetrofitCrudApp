
package com.example.retrofitcrudapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfesorActivity : AppCompatActivity() {

    private lateinit var recyclerProfesores: RecyclerView
    private lateinit var api: ProfesorApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profesor)

        recyclerProfesores = findViewById(R.id.recyclerProfesores)
        recyclerProfesores.layoutManager = LinearLayoutManager(this)

        api = ProfesorRetrofitClient.instance

        val fabAgregar: FloatingActionButton =
            findViewById(R.id.fabAgregarProfesor)

        fabAgregar.setOnClickListener {
            Toast.makeText(
                this,
                "Próximamente: registrar profesor",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::api.isInitialized) {
            cargarProfesores()
        }
    }

    private fun cargarProfesores() {

        api.obtenerProfesores().enqueue(
            object : Callback<List<Profesor>> {

                override fun onResponse(
                    call: Call<List<Profesor>>,
                    response: Response<List<Profesor>>
                ) {
                    if (response.isSuccessful) {

                        val profesores = response.body().orEmpty()

                        recyclerProfesores.adapter =
                            ProfesorAdapter(profesores) { profesor ->

                                Toast.makeText(
                                    this@ProfesorActivity,
                                    "Seleccionaste a ${profesor.nombre}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        Log.d(
                            "PROFESOR_API",
                            "Profesores recibidos: ${profesores.size}"
                        )

                    } else {

                        Toast.makeText(
                            this@ProfesorActivity,
                            "Error HTTP: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<Profesor>>,
                    t: Throwable
                ) {

                    Log.e(
                        "PROFESOR_API",
                        "Error al cargar profesores",
                        t
                    )

                    Toast.makeText(
                        this@ProfesorActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }
}
