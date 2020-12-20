package com.example.openweather


import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView


class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var container: LinearLayout
    var name: TextView
    var country: TextView
    var deleteCity: ImageView
    var editCity: ImageView

    init {
        container = itemView.findViewById(R.id.item_container)
        name = itemView.findViewById(R.id.city_name)
        country = itemView.findViewById(R.id.country_name)
        deleteCity = itemView.findViewById(R.id.delete_city) as ImageView
        editCity = itemView.findViewById(R.id.edit_city) as ImageView
    }
}