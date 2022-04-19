package com.example.parcel_checker_app;

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class EventAdapter(private val mEventData:List<ParcelEvent>):
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val nazwaEventTextView : TextView = itemView.findViewById(R.id.nazwa_TextView)
        val nazwaJEventTextView : TextView = itemView.findViewById(R.id.nazwaJednostki_textView)
        val dataTextView: TextView = itemView.findViewById(R.id.data_TextView)
    }

    /**
     *  Rozwija układ elementów listy.
     */

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EventViewHolder(inflater.inflate(R.layout.list_item, parent, false))
    }

    /**
     * Wiąże każdy widok z ViewHolder z układem elementu listy, a ostatecznie z theRecyclerView.
     */
    override fun onBindViewHolder(holder: EventAdapter.EventViewHolder, position: Int) {
        holder.nazwaEventTextView.text = mEventData[position].nazwa
        holder.nazwaJEventTextView.text = mEventData[position].nazwaJednostka
        holder.dataTextView.text = mEventData[position].czas
    }

    override fun getItemCount(): Int {
        return  mEventData.size
    }

}