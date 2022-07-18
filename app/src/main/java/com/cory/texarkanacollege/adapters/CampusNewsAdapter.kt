package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.fragments.CampusNewsInfoFragment

class CampusNewsAdapter(val context: Context,
                        private val dataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name = itemView.findViewById<TextView>(R.id.row_campus_news_name)!!
        val pageNumber = itemView.findViewById<TextView>(R.id.pageNumberTextView)
        val separator = itemView.findViewById<View>(R.id.separator)

        fun bind(position: Int) {

            val dataItem = dataList[position]

            name.text = dataItem["name"]
            if (position > 0) {
            if (dataItem["pageNumber"]!!.toInt() > dataList[position - 1]["pageNumber"]!!.toInt()) {
                pageNumber.visibility = View.VISIBLE
                separator.visibility = View.VISIBLE
                pageNumber.text = "Page Number: ${dataItem["pageNumber"]}"
            }
            else {
                pageNumber.visibility = View.GONE
                separator.visibility = View.GONE
            }
            }
            else {
                pageNumber.visibility = View.VISIBLE
                separator.visibility = View.VISIBLE
                pageNumber.text = "Page Number: ${dataItem["pageNumber"]}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.campus_news_list_item, parent, false))
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder.itemView.findViewById<CardView>(R.id.cardViewCampusNews).setOnClickListener {
            val runnable = Runnable {
                (context as MainActivity).hideKeyboardCampusNews()

            }

            MainActivity().runOnUiThread(runnable)

            try {
                val fragment = CampusNewsInfoFragment()

                val dataItem = dataList[position]

                val args = Bundle()
                args.putString("link", dataItem["link"])
                args.putString("imgLink", dataItem["imgLink"])
                fragment.arguments = args

                val manager =
                    (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                manager.setCustomAnimations(
                    R.anim.slide_in_fragment,
                    R.anim.fade_out_fragment,
                    R.anim.fade_in_fragment,
                    R.anim.slide_out_fragment
                )
                manager.add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                manager.commit()
            }
            catch (e: Exception) {
                Toast.makeText(context, "There was an error completing your request", Toast.LENGTH_SHORT).show()
            }
        }
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}