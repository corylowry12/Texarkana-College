package com.cory.hourcalculator.adapters

import android.animation.LayoutTransition
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.R

class PatchNotesNewFeaturesAdapter(
    val context: Context,
    private val dataList: Array<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title = itemView.findViewById<TextView>(R.id.tvTitle)!!

        fun bind(position: Int) {

            title.text = dataList.elementAt(position).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.patch_notes_list_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val layout2 = holder.itemView.findViewById<RelativeLayout>(R.id.relativeLayoutPatchNotes)
        val layoutTransition2 = layout2.layoutTransition
        layoutTransition2.enableTransitionType(LayoutTransition.CHANGING)

        (holder as PatchNotesNewFeaturesAdapter.ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}