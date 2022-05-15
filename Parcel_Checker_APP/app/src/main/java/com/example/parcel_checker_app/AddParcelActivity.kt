package com.example.parcel_checker_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_parcel.*
import kotlinx.android.synthetic.main.activity_main.*

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
        var last_event: String = "";
        val databaseHandler = DBHandler(this)
        println("${Thread.currentThread()} has run.")

        val newParcelThread = Thread(
            Runnable {
                val eventsList: List<ParcelEvent>? = QueryUtils.requestEventData(parcelNum_Add.text.toString())
                if(eventsList != null){
                    last_event = eventsList[0].czas.toString() + " | " + eventsList[0].nazwa.toString()
                    val insertStatus = databaseHandler.addParcel(ParcelModelClass(0, name, number, last_event))
                    if (insertStatus > -1) {
                        runOnUiThread(Runnable {
                            Toast.makeText(this, "Pomyślnie dodano przesyłkę", Toast.LENGTH_SHORT).show()
                        })
                        parcelName_Add.text.clear()
                        parcelNum_Add.text.clear()
                        println("Pomyślnie dodano przesyłkę")
                    }
                    this.finish()
                    Thread.sleep(5000)

                }
                else{
                    runOnUiThread(Runnable {
                        Toast.makeText(this, "Błędny numer przesyłki", Toast.LENGTH_LONG).show()
                    })
                }
            }
        )

        if (!name.isEmpty() && !number.isEmpty()) {
            newParcelThread.start()
            println("Thread id:${newParcelThread.id} | IsAlive: ${newParcelThread.isAlive}")
        } else {
            Toast.makeText(
                applicationContext,
                "nazwa lub numer przesyłki nie mogą być puste",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}