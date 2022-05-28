package com.example.parcel_checker_app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_update.*

class ParcelAdapter(val context: Context, val items: ArrayList<ParcelModelClass>):RecyclerView.Adapter<ParcelAdapter.ParcelViewholder>() {

    class ParcelViewholder(view: View): RecyclerView.ViewHolder(view) {
        val parcelNameTextView : TextView = itemView.findViewById(R.id.parcelName_TextView)
        val parcelNumTextView : TextView = itemView.findViewById(R.id.parcelNum_TextView)
        val currStatusTextView: TextView = itemView.findViewById(R.id.currStatus_TextView)
        val parcelEdit: ImageView = itemView.findViewById(R.id.edit_ImageView)
        val parcelDelete: ImageView = itemView.findViewById(R.id.delete_ImageView)
        val parcelRoute: ImageView = itemView.findViewById(R.id.displayRoute_ImageView)
    }
    /**
     *  Rozwija układ elementów listy.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParcelViewholder {
        val  inflater = LayoutInflater.from(context)
        return ParcelViewholder(inflater.inflate(
               R.layout.list_parcel,
               parent,
               false
            )
        )
    }
    /**
     * Wiąże każdy widok z ViewHolder z układem elementu listy, a ostatecznie z theRecyclerView.
     */
    override fun onBindViewHolder(holder: ParcelViewholder, position: Int) {
        val item = items.get(position)
        holder.parcelNameTextView.text = item.p_name
        holder.parcelNumTextView.text = item.p_num
        holder.currStatusTextView.text = item.p_status
        holder.parcelEdit.setOnClickListener{view ->
            if(context is DisplayParcelsActivity){
                context.updateParcelDialog(item)
            }
        }
        holder.parcelDelete.setOnClickListener{view ->
            if(context is DisplayParcelsActivity){
                context.deleteParcelAlertDialog(item)
            }
        }

        holder.parcelRoute.setOnClickListener{
            val parcelRouteIntent = Intent(context, ParcelRouteActivity::class.java)
            val dane = ArrayList<String>(4)
            dane.addAll(listOf<String>(item.p_id.toString(),item.p_name,item.p_num,item.p_status))
            parcelRouteIntent.putStringArrayListExtra("parcelData", dane)
            context.startActivity(parcelRouteIntent)
        }
    }



    override fun getItemCount(): Int {
        return  items.size
    }
}