package com.example.parcel_checker_app

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_display_parcels.*
import kotlinx.android.synthetic.main.dialog_update.*

class DisplayParcelsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_parcels)

        loadParcels()
    }

    /**
     * Funkcja odpowiada za ładowanie przesyłek do RecyclerView
     */
    private fun loadParcels() {
        if(getParcelsList().size > 0){
            ParcelRecyclerView.visibility = View.VISIBLE
            ParcelRecyclerView.layoutManager = LinearLayoutManager(this)
            val parcelAdapter = ParcelAdapter(this, getParcelsList())
            ParcelRecyclerView.adapter = parcelAdapter
        }
        else{
            ParcelRecyclerView.visibility = View.GONE
        }
    }

    /**
     * Funkcja odpowiada za modyfikację nazwy danej przesyłki
     */
    fun updateParcelDialog(parcelModelClass: ParcelModelClass){
        val updateDialog = Dialog(this, R.style.Theme_AppCompat_Dialog)
        updateDialog.setCancelable(false)
        updateDialog.setContentView(R.layout.dialog_update)

        updateDialog.pUpdateName.setText(parcelModelClass.p_name)
        updateDialog.tvUpdate.setOnClickListener(View.OnClickListener {
            val name = updateDialog.pUpdateName.text.toString()
            val databaseHandler: DBHandler = DBHandler(this)
            if(!name.isEmpty()) {
                val status = databaseHandler.updateParcel(ParcelModelClass(parcelModelClass.p_id, name, parcelModelClass.p_num, parcelModelClass.p_status))
                if(status > -1){
                    Toast.makeText(applicationContext, "Zmienionio nazwę", Toast.LENGTH_SHORT).show()
                    loadParcels()
                    updateDialog.dismiss()
                }
            }  else{
                Toast.makeText(applicationContext, "Nazwa przeysłki nie może być pusta", Toast.LENGTH_SHORT).show()
            }
        })
        updateDialog.tvCancel.setOnClickListener(View.OnClickListener {
            updateDialog.dismiss();
        })
        updateDialog.show()
    }

    /**
     * Funkcja odpowiada za usuwanie numerów przesyłek
     */
    fun deleteParcelAlertDialog(parcelModelClass: ParcelModelClass) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Usuń przesyłkę")
        builder.setMessage("Czy jesteś pewien, że chcesz usunąć ${parcelModelClass.p_name} ?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Tak") { dialogInterface, which ->
            val databaseHandler: DBHandler = DBHandler(this)
            val status =
                databaseHandler.deleteParcel(ParcelModelClass(parcelModelClass.p_id, "", "", ""))
            if (status > -1) {
                Toast.makeText(
                    applicationContext,
                    "Przesyłka usunięta pomyślnie",
                    Toast.LENGTH_SHORT
                ).show()
                loadParcels()
            }
            dialogInterface.dismiss()
        }
            builder.setNegativeButton("Nie") { dialogInterface, which ->
                dialogInterface.dismiss()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
    }


    /**
     * Funkcja odpowiada za pobranie wszystkich przesyłek z bazy danych
     */
    private fun getParcelsList(): ArrayList<ParcelModelClass> {
        val databaseHandler: DBHandler = DBHandler(this)
        val parcelList: ArrayList<ParcelModelClass> = databaseHandler.viewParcel()

        return parcelList
    }

}