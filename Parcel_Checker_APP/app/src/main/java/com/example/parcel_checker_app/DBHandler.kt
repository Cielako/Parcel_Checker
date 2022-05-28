package com.example.parcel_checker_app
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
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

    /**
     * Funkcja dodająca przesyłkę do bazy danych
     */
    fun addParcel(par: ParcelModelClass): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, par.p_name) // ParcelModelClass Name
        contentValues.put(KEY_NUM, par.p_num) // ParcelModelClass Num
        contentValues.put(KEY_STATUS, par.p_status) // ParcelModelClass Status

        // Wstawiamy informacje o przesyłce używając zapytania (insert query)
        val success = db.insert(TABLE_PARCELS, null, contentValues)
        //2 argument to String zawierający nullColumnHack

        db.close() // zamykamy połaczenie
        return success
    }

    /**
     * Funkcja odpowiadająca za pobranie informajci o przesyłce z bazy danych
     */
    @SuppressLint("Range")
    fun viewParcel(): ArrayList<ParcelModelClass>{
        val parcelList: ArrayList<ParcelModelClass> = ArrayList<ParcelModelClass>()
            val selectQuery = "SELECT * FROM $TABLE_PARCELS"
            val db = this.readableDatabase
            var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return  ArrayList()
        }
        var id: Int
        var name : String
        var num: String
        var status : String

        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                num = cursor.getString(cursor.getColumnIndex(KEY_NUM))
                status = cursor.getString(cursor.getColumnIndex(KEY_STATUS))
                val parcel = ParcelModelClass(p_id = id, p_name = name, p_num = num, p_status = status )
                parcelList.add(parcel)
            }while (cursor.moveToNext())
        }
        return parcelList
    }

    /**
     * Funkcja odpowiada za edycję nazwy przesyłki w bazię danych
     */
    fun updateParcel(par: ParcelModelClass): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, par.p_name)
        contentValues.put(KEY_STATUS, par.p_status)

        val succes = db.update(TABLE_PARCELS, contentValues, KEY_ID + "=" + par.p_id, null)
        db.close()
        return succes
    }
    fun deleteParcel(par:ParcelModelClass):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, par.p_id)
        val success = db.delete(TABLE_PARCELS, KEY_ID + "=" + par.p_id,null )

        db.close()
        return success
    }
}