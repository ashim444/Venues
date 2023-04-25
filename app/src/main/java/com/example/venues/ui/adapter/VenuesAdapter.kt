package com.example.venues.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.venues.R
import com.example.venues.data.local.models.Venue


class VenuesAdapter(context: Context, venues: MutableList<Venue>) :
    ArrayAdapter<Venue>(context, 0, venues) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val currentView =
            convertView ?: LayoutInflater.from(context).inflate(R.layout.venu_item, parent, false)

        currentView.findViewById<TextView>(R.id.venue_name).text = getItem(position)?.name
        currentView.findViewById<TextView>(R.id.venue_address).text = getItem(position)?.location?.address
        val distance = getItem(position)?.location?.distance ?: 0
        if(distance != 0){
            currentView.findViewById<TextView>(R.id.venue_distance).text = String.format("%.2f KM", distance.toDouble() / 1000.0)
        }
        return currentView
    }
}