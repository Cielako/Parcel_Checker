package com.example.parcel_checker_app

import android.app.LoaderManager
import android.content.AsyncTaskLoader

import android.content.Loader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<ParcelEvent>> {

    private var parcelNum = ""
    // private val LOG_TAG = MainActivity::class.java.simpleName

    // Loader ID constant.
    private val LOADER_ID = 1

    override fun onResume() {
        super.onResume()
        if (parcelNum.length > 0) {
            query_input.setText(parcelNum)
            readParcelEvents()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        // Runs UI initializations/
        init()
    }

    private fun init() {
        // Ustawiamy RecyclerView, żeby miał stały rozmiar i przewijało się go liniowo w dół
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // Ustawiamy adapeter, żeby można było wypełnić listę danymi
        recyclerView.adapter = EventAdapter(mutableListOf<ParcelEvent>())

        // Ustawiamy działanie przycisku żeby odczytywał dane
        search_button.setOnClickListener {
            readParcelEvents()
        }

    }

    private fun readParcelEvents() {
        // Uruchamia / Restartuje Loader żeby rozpocząć wątek w tle
        loaderManager.restartLoader(LOADER_ID, null, this)

        // Chowa obecny widok do momentu wczytania nowych danych
        recyclerView.visibility = View.INVISIBLE
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<List<ParcelEvent>> {
        return object : AsyncTaskLoader<List<ParcelEvent>>(this) {

            override fun onStartLoading() {
                super.onStartLoading()
                // Zmuszamy loader do załadowania
                forceLoad()
            }

            override fun loadInBackground(): List<ParcelEvent>? {
                // Zwraca null i automatycznie wychodzi z wątku

                if (query_input.text.toString().isEmpty()) {
                    return null
                }
                // zwraca odpowiedź w postaci danych XML poprzez metodę request Event Data
                return QueryUtils.requestEventData(query_input.text.toString())
            }
        }
    }

    override fun onLoadFinished(loader: Loader<List<ParcelEvent>>, data: List<ParcelEvent>?) {

        // Wyświetla RecyclerView, w innym wypadku nie wyświetla nic
        if (data != null) {
            recyclerView.visibility = View.VISIBLE
            recyclerView.adapter = EventAdapter(data)
        }
    }

    override fun onLoaderReset(loader: Loader<List<ParcelEvent>>) {
        // Usuwa obecne dane, ponieważ loader został zresetowany
        recyclerView.adapter = EventAdapter(mutableListOf<ParcelEvent>())
    }
}