package com.example.parcel_checker_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_parcel.*

class AddParcelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_parcel)
        parcel_Add_Button.setOnClickListener{view ->
            addRecord()
        }
    }
    /**
     *  Metoda do zapisu przesyłek w bazie danych
     */
    private fun addRecord() {
        val name = parcelName_Add.text.toString()
        val number = parcelNum_Add.text.toString()
        val databaseHandler = DBHandler(this)
        if (!name.isEmpty() && !number.isEmpty()) {
            val insertStatus =
                databaseHandler.addParcel(ParcelModelClass(0, name, number, "test"))
            if (insertStatus > -1) {
                Toast.makeText(applicationContext, "pomyślnie dodano przesyłkę", Toast.LENGTH_LONG).show()
                parcelName_Add.text.clear()
                parcelNum_Add.text.clear()
                this.finish()
            }
        } else {
            Toast.makeText(
                applicationContext,
                "nazwa lub numer przesyłki nie mogą być puste",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}