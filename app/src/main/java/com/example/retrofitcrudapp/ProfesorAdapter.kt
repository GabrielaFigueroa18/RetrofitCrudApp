package com.example.retrofitcrudapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProfesorAdapter(
    private val profesores: List<Profesor>,
    private val onItemClick: (Profesor) -> Unit
) : RecyclerView.Adapter<ProfesorAdapter.ProfesorViewHolder>() {

    class ProfesorViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val nombre: TextView =
            itemView.findViewById(R.id.txtNombreProfesor)

        val edad: TextView =
            itemView.findViewById(R.id.txtEdadProfesor)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfesorViewHolder {

        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesor, parent, false)

        return ProfesorViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: ProfesorViewHolder,
        position: Int
    ) {
        val profesor = profesores[position]

        holder.nombre.text =
            "${profesor.nombre} ${profesor.apellido}"

        holder.edad.text =
            "Edad: ${profesor.edad} años"

        holder.itemView.setOnClickListener {
            onItemClick(profesor)
        }
    }

    override fun getItemCount(): Int {
        return profesores.size
    }
}
