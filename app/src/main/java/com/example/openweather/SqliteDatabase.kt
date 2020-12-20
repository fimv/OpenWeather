package com.example.openweather

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class SqliteDatabase(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_CITIES_TABLE =
            "CREATE	TABLE " + TABLE_CITIES + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME + " TEXT," + COLUMN_COUNTRY + " TEXT" + ")"
        db.execSQL(CREATE_CITIES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES)
        onCreate(db)
    }

    fun listCities(): ArrayList<Cities> {
        val sql = "select * from " + TABLE_CITIES
        val db = this.readableDatabase
        val storeCities: ArrayList<Cities> = ArrayList()
        val cursor: Cursor = db.rawQuery(sql, null)
        if (cursor.moveToFirst()) {
            do {
                val id: Int = cursor.getString(0).toInt()
                val name: String = cursor.getString(1)
                val country: String = cursor.getString(2)
                storeCities.add(Cities(id, name, country))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return storeCities
    }

    fun addCities(cities: Cities) {
        val values = ContentValues()
        values.put(COLUMN_NAME, cities.name)
        values.put(COLUMN_COUNTRY, cities.country)
        val db = this.writableDatabase
        db.insert(TABLE_CITIES, null, values)
    }

    fun updateCities(cities: Cities) {
        val values = ContentValues()
        values.put(COLUMN_NAME, cities.name)       //.getName())
        values.put(COLUMN_COUNTRY, cities.country)                    //.getPhno())
        val db = this.writableDatabase
        db.update(
            TABLE_CITIES,
            values,
            COLUMN_ID + "	= ?",
            arrayOf(java.lang.String.valueOf(cities.id))      //.getId()
        )
    }

    fun findContacts(name: String?): Cities? {
        val query = "Select * FROM " + TABLE_CITIES + " WHERE " + COLUMN_NAME + " = " + "name"
        val db = this.writableDatabase
        var cities: Cities? = null
        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val id: Int = cursor.getString(0).toInt()
            val contactsName: String = cursor.getString(1)
            val contactsNo: String = cursor.getString(2)
            cities = Cities(id, contactsName, contactsNo)
        }
        cursor.close()
        return cities
    }

    fun deleteCities(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_CITIES, COLUMN_ID + "	= ?", arrayOf(id.toString()))
    }

    companion object {
        private const val DATABASE_VERSION = 5
        private const val DATABASE_NAME = "city"
        private const val TABLE_CITIES = "cities"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_NAME = "cityname"
        private const val COLUMN_COUNTRY = "countryname"
    }
}