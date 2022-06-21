package com.cory.hourcalculator.adapters

import android.animation.LayoutTransition
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.R

class RoadmapAdapter(val context: Context,
                     private val dataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title = itemView.findViewById<TextView>(R.id.tvTitle)!!

        fun bind(position: Int) {
            val dataItem = dataList[position]
            title.text = dataItem["title"]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.known_issues_list_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val layout2 = holder.itemView.findViewById<RelativeLayout>(R.id.relativeLayoutUpdate)
        val layoutTransition2 = layout2.layoutTransition
        layoutTransition2.enableTransitionType(LayoutTransition.CHANGING)
        val dataItem = dataList[holder.adapterPosition]

        (holder as RoadmapAdapter.ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}