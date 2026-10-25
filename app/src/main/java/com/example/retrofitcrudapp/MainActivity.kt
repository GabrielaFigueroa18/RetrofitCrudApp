
package com.example.retrofitcrudapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlumnoAdapter
    private lateinit var api: AlumnoApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val fabAgregar: FloatingActionButton =
            findViewById(R.id.fab_agregar)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializar la API
        api = RetrofitClient.instance

        // Botón para agregar alumno
        fabAgregar.setOnClickListener {
            val intent = Intent(
                this@MainActivity,
                CrearAlumnoActivity::class.java
            )
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        if (::api.isInitialized && ::recyclerView.isInitialized) {
            cargarDatos()
        }
    }

    // ==========================================
    // OBTENER ALUMNOS DESDE LA API
    // ==========================================

    private fun cargarDatos() {

        Log.d("API_ERROR", "Iniciando consulta de alumnos")

        api.obtenerAlumnos().enqueue(
            object : Callback<List<Alumno>> {

                override fun onResponse(
                    call: Call<List<Alumno>>,
                    response: Response<List<Alumno>>
                ) {

                    Log.d(
                        "API_ERROR",
                        "Código HTTP: ${response.code()}"
                    )

                    if (response.isSuccessful) {

                        val alumnos = response.body()

                        if (alumnos != null) {

                            Log.d(
                                "API_ERROR",
                                "Alumnos recibidos: ${alumnos.size}"
                            )

                            adapter = AlumnoAdapter(alumnos)
                            recyclerView.adapter = adapter

                            adapter.setOnItemClickListener(
                                object : AlumnoAdapter.OnItemClickListener {

                                    override fun onItemClick(
                                        alumno: Alumno
                                    ) {
                                        mostrarOpciones(alumno)
                                    }
                                }
                            )

                            if (alumnos.isEmpty()) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "No hay alumnos registrados",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {

                            Toast.makeText(
                                this@MainActivity,
                                "No se recibieron alumnos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {

                        val error = response.errorBody()?.string()

                        Log.e(
                            "API_ERROR",
                            "Error HTTP ${response.code()}: $error"
                        )

                        Toast.makeText(
                            this@MainActivity,
                            "Error HTTP: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<Alumno>>,
                    t: Throwable
                ) {

                    Log.e(
                        "API_ERROR",
                        "Error al obtener alumnos",
                        t
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    // ==========================================
    // OPCIONES DE ALUMNO
    // ==========================================

    private fun mostrarOpciones(alumno: Alumno) {

        val opciones = arrayOf(
            "Modificar alumno",
            "Eliminar alumno"
        )

        AlertDialog.Builder(this)
            .setTitle(alumno.nombre)
            .setItems(opciones) { _, index ->

                when (index) {
                    0 -> modificarAlumno(alumno)
                    1 -> confirmarEliminacion(alumno)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ==========================================
    // MODIFICAR ALUMNO
    // ==========================================

    private fun modificarAlumno(alumno: Alumno) {

        val intent = Intent(
            this@MainActivity,
            ActualizarAlumnoActivity::class.java
        )

        intent.putExtra("alumno_id", alumno.id)
        intent.putExtra("nombre", alumno.nombre)
        intent.putExtra("apellido", alumno.apellido)
        intent.putExtra("edad", alumno.edad)

        startActivity(intent)
    }

    // ==========================================
    // CONFIRMAR ELIMINACIÓN
    // ==========================================

    private fun confirmarEliminacion(alumno: Alumno) {

        AlertDialog.Builder(this)
            .setTitle("Eliminar alumno")
            .setMessage(
                "¿Seguro que deseas eliminar a ${alumno.nombre} ${alumno.apellido}?"
            )
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarAlumno(alumno)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ==========================================
    // ELIMINAR ALUMNO
    // ==========================================

    private fun eliminarAlumno(alumno: Alumno) {

        Log.d(
            "API_ERROR",
            "Eliminando alumno: ${alumno.id}"
        )

        api.eliminarAlumno(alumno.id).enqueue(
            object : Callback<Void> {

                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@MainActivity,
                            "Alumno eliminado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        cargarDatos()

                    } else {

                        val error = response.errorBody()?.string()

                        Log.e(
                            "API_ERROR",
                            "Error al eliminar: ${response.code()} - $error"
                        )

                        Toast.makeText(
                            this@MainActivity,
                            "Error al eliminar alumno: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<Void>,
                    t: Throwable
                ) {

                    Log.e(
                        "API_ERROR",
                        "Error al eliminar alumno",
                        t
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }
}
