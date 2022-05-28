package com.example.parcel_checker_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_display_parcels.*
import kotlinx.android.synthetic.main.activity_parcel_route.*

class ParcelRouteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parcel_route)
        val parcelData = intent.getStringArrayListExtra("parcelData")
        if(parcelData != null){
            loadParcelEvents(parcelData)
        }
    }

    private fun init(eventsList: List<ParcelEvent>){
        ParcelRouteRecyclerView.visibility = View.VISIBLE
        ParcelRouteRecyclerView.layoutManager = LinearLayoutManager(this)
        val eventAdapter = EventAdapter(eventsList)
        ParcelRouteRecyclerView.adapter = eventAdapter;
    }
    /**
     * Funkcja odpowiada za ładowanie wszystkich statusów danej przesyłki
     */
    private fun loadParcelEvents(data:ArrayList<String>){
        val databaseHandler = DBHandler(this)
        val showEventsThread = Thread(
            Runnable {
                val eventsList: List<ParcelEvent>? = QueryUtils.requestEventData(data[2])
                if(eventsList != null){
                    databaseHandler.updateParcel(ParcelModelClass(data[0].toInt(), data[1], data[2], eventsList[0].czas + " | " + eventsList[0].nazwa))
                    this.runOnUiThread(java.lang.Runnable {
                        init(eventsList)
                    })
                    Thread.sleep(5000)
                }
            }
        )
        showEventsThread.start()
    }


}

