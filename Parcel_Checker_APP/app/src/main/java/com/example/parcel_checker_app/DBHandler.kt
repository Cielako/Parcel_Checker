package com.example.parcel_checker_app
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Tworzymy logikę naszej klasy, poprzez rozszerzenie klasy bazowej SQLiteOpenHelper
class DBHandler(context: Context):SQLiteOpenHelper(context,DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "ParcelDatabase"

        private val TABLE_PARCELS = "ParcelTable"

        private val KEY_ID = "_id"
        private val KEY_NAME = "name"
        private val KEY_NUM = "number"
        private val KEY_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //Tworzymy tabelę z polami
        val CREATE_PARCELS_TABLE = ("CREATE TABLE " + TABLE_PARCELS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"+ KEY_NUM + " TEXT,"
                + KEY_STATUS + " TEXT" + ")")
        db?.execSQL(CREATE_PARCELS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_PARCELS")
        onCreate(db)
    }

    // Funkcja odpowiadająca za wstawianie danych
    fun addParcel(par: ParcelModelClass): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, par.p_name) // ParcelModelClass Name
        contentValues.put(KEY_NUM, par.p_num) // ParcelModelClass Num
        contentValues.put(KEY_STATUS, par.p_status) // ParcelModelClass Status
        println("tescik")

        // Wstawiamy informacje o przesyłce używając zapytania (insert query)
        val success = db.insert(TABLE_PARCELS, null, contentValues)
        //2 argument to String zawierający nullColumnHack

        db.close() // zamykamy połaczenie
        return success
    }
}