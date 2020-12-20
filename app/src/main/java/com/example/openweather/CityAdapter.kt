package com.example.openweather

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class CityAdapter (context: Context, listCities: ArrayList<Cities>, private val cityClickListener: CityClickListener) :
    RecyclerView.Adapter<CityViewHolder>(), Filterable {
    private val context: Context
    private var listCities: ArrayList<Cities>
    private val mArrayList: ArrayList<Cities>
    private val mDatabase: SqliteDatabase
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.city_list_layout, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val cities = listCities[position]


        holder.container.setOnClickListener {
            cityClickListener.onCityClickListener(cities)
        }
        holder.name.setText(cities.name)
        holder.country.setText(cities.country)

        holder.editCity.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                editTaskDialog(cities)
            }
        })

        holder.deleteCity.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                //delete row from database
                // mDatabase.deleteContact(contacts.id)
                deliteTaskDialog(cities)

            }
        })
    }


    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                // this@ContactAdapter.
                listCities = if (charString.isEmpty()) {

                    //this@ContactAdapter.
                    mArrayList
                } else {
                    val filteredList: ArrayList<Cities> = ArrayList()
                    for (cities in mArrayList) {
                        if (cities.name.toLowerCase(Locale.ROOT).contains(charString)) {
                            filteredList.add(cities)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = listCities
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults,
            ) {
                listCities = filterResults.values as ArrayList<Cities>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return listCities.size
    }

    private fun editTaskDialog(cities: Cities?) {
        val inflater = LayoutInflater.from(context)
        val subView: View = inflater.inflate(R.layout.add_city_layout, null)
        val nameField = subView.findViewById(R.id.add_city) as EditText
        val countryField = subView.findViewById(R.id.add_country) as EditText
        if (cities != null) {
            nameField.setText(cities.name)
            countryField.setText(cities.country)
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Edit city")
        builder.setView(subView)
        builder.create()
        builder.setPositiveButton("EDIT CITY") { dialog, which ->
            val name = nameField.text.toString()
            val country = countryField.text.toString()
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(
                    context,
                    "Something went wrong. Check your input values",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Editing contact...",
                    Toast.LENGTH_LONG
                ).show()
                mDatabase.updateCities(Cities(cities!!.id, name, country))
                //refresh the activity
                (context as Activity).finish()
                context.startActivity((context as Activity).intent)
            }
        }
        builder.setNegativeButton("CANCEL") { dialog, which ->
            Toast.makeText(
                context,
                "Task cancelled",
                Toast.LENGTH_LONG
            ).show()
        }
        builder.show()
    }



    private fun deliteTaskDialog(cities: Cities?) {

        if (cities == null) {

        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Delite city")
        //builder.setView(delitesubView)
        builder.create()
        builder.setPositiveButton("DELITE CITY") { dialog, which ->

            Toast.makeText(
                context,
                "Deliting contact...",
                Toast.LENGTH_LONG
            ).show()

            mDatabase.deleteCities(cities?.id!!)

            //refresh the activity
            (context as Activity).finish()
            context.startActivity((context as Activity).intent)
        }

        builder.setNegativeButton("CANCEL") { dialog, which ->
            Toast.makeText(
                context,
                "Task cancelled",
                Toast.LENGTH_LONG
            ).show()
        }
        builder.show()
    }




    init {
        this.context = context
        this.listCities = listCities
        mArrayList = listCities
        mDatabase = SqliteDatabase(context)
    }
}
