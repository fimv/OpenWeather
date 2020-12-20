package com.example.openweather
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MainActivity : AppCompatActivity(), CityClickListener {
    private var mDatabase: SqliteDatabase? = null
    private var allCities: ArrayList<Cities> = ArrayList()
    private var mAdapter: CityAdapter? = null

    lateinit var responseTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        responseTextView = findViewById(R.id.resp_text_view)
        findViewById<Button>(R.id.gps_button).setOnClickListener {
            val intent = Intent(this, GpsActivity::class.java)
            startActivity(intent)
        }

                //val fLayout =
                findViewById<View>(R.id.activity_to_do) as FrameLayout
        val cityView = findViewById<View>(R.id.product_list) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        cityView.layoutManager = linearLayoutManager
        cityView.setHasFixedSize(true)

        mDatabase = SqliteDatabase(this)
        allCities = mDatabase!!.listCities()

        if (allCities.size > 0) {
            cityView.visibility = View.VISIBLE
            mAdapter = CityAdapter(this, allCities, this)
            cityView.adapter = mAdapter
        } else {
            cityView.visibility = View.GONE
            Toast.makeText(
                this,
                "There is no cities in the database. Start adding now",
                Toast.LENGTH_LONG
            ).show()

            val newCity = Cities("Nizhny Novgorod", "Russia")
            mDatabase!!.addCities(newCity)
        }



        val searchView = findViewById<View>(R.id.searchView) as SearchView
        // searchView.
        search(searchView)



        val add = findViewById<View>(R.id.add_button) as Button
        add.setOnClickListener (
            object : View.OnClickListener {
                override fun onClick(view: View?)
                {addTaskDialog()
                }
            })



        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener (
            object : View.OnClickListener {
                override fun onClick(view: View?) {
                    addTaskDialog()
                }
            })
    }

    private fun addTaskDialog() {
        val inflater = LayoutInflater.from(this)
        val subView: View = inflater.inflate(R.layout.add_city_layout, null)
        //val subView: View = inflater.inflate(R.layout.add_city_layout, null)
        val nameField = subView.findViewById(R.id.add_city) as EditText
        val countryField = subView.findViewById(R.id.add_country) as EditText
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Add new CITY")
        builder.setView(subView)
        builder.create()


        builder.setPositiveButton("ADD CITY") { dialog, which ->
            val name = nameField.text.toString()
            val country = countryField.text.toString()

            if (name == "") {
                Toast.makeText(
                    this,
                    "You forgot to enter name! Check your input values",
                    Toast.LENGTH_LONG
                ).show()
                addTaskDialog()
            }

            else {

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(
                        this,
                        "Something went wrong. Check your input values",
                        Toast.LENGTH_LONG
                    ).show()
                } else {

                    // Change the app background color
                    subView.setBackgroundColor(Color.RED)

                    Toast.makeText(applicationContext, "Ok, we adding the contact.", Toast.LENGTH_SHORT).show()

                }
                val newCity = Cities(name, country)
                mDatabase!!.addCities(newCity)
                finish()
                startActivity(intent)
            }
        }


        builder.setNegativeButton("CANCEL") { dialog, which ->
            Toast.makeText(
                this,
                "Task cancelled",
                Toast.LENGTH_LONG
            ).show()
        }
        // Display the alert dialog on app interface
        //builder.show()
        builder.show()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDatabase != null) {
            mDatabase!!.close()
        }
    }



    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.i(TAG, "Llego al querysubmit")
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (mAdapter != null) mAdapter!!.filter.filter(newText)
                Log.i(TAG, "Llego al querytextchange")
                return true
            }
        })
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {

                addTaskDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }




    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCityClickListener(cities: Cities) {

        val httpState = "q=${cities.name}"
        Toast.makeText(this, "city: ${cities.name}   country: ${cities.country}", Toast.LENGTH_LONG).show()

        request(httpState)
    }


    fun request(httpState: String) {
        var weather: WeatherRequest? = null
       weather = WeatherRequest()

        //запускаем корутину
        GlobalScope.launch {
            //получаем результат выполнения функции makeRequest()
           val result: String = weather.makeRequest(httpState)
           // val result: String = makeRequest(httpState)

           // println("makeText")
            val  jsonStr: String = weather.jsonElement(result)
         //   val  jsonStr: String = jsonElement(result)
            //при помощи метода withContext в который передаем диспетчер основного потока Dispatchers.Main
            //передаем полученные данные в наш Text View
            withContext(Dispatchers.Main) {

                responseTextView.text = jsonStr
            }
        }
    }


}

