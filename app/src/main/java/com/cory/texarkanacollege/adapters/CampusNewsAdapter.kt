package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.fragments.CampusNewsInfoFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class CampusNewsAdapter(val context: Context,
                        private val dataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var dialog: BottomSheetDialog

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

    fun dismissBottomSheet() {
        try {
            dialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.campus_news_list_item, parent, false))
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = dataList[holder.adapterPosition]

        holder.itemView.findViewById<CardView>(R.id.cardViewCampusNews).setOnLongClickListener {

            dialog = BottomSheetDialog(context)
            val campusNewsOptionLayout =
                LayoutInflater.from(context).inflate(R.layout.campus_news_bottom_sheet, null)
            dialog.setContentView(campusNewsOptionLayout)

            if (context.resources.getBoolean(R.bool.isTablet)) {
                val bottomSheet =
                    dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheetBehavior.skipCollapsed = true
                bottomSheetBehavior.isHideable = false
                bottomSheetBehavior.isDraggable = false
            }

            val copyButton = campusNewsOptionLayout.findViewById<Button>(R.id.copyButton)
            val shareButton = campusNewsOptionLayout.findViewById<Button>(R.id.shareButton)
            val openButton = campusNewsOptionLayout.findViewById<Button>(R.id.openButton)
            val cancelButton = campusNewsOptionLayout.findViewById<Button>(R.id.cancelButton)

            copyButton.setOnClickListener {
                val clipBoard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("URL", dataItem["link"])
                clipBoard.setPrimaryClip(clip)
                Toast.makeText(
                    context,
                    "Link Copied to Clipboard",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }

            shareButton.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, dataItem["name"])
                shareIntent.putExtra(Intent.EXTRA_TEXT, dataItem["link"])
                context.startActivity(Intent.createChooser(shareIntent, "Share Link"))
                dialog.dismiss()
            }

            openButton.setOnClickListener {
                val openIntent = Intent(Intent.ACTION_VIEW, Uri.parse(dataItem["link"]))
                //Toast.makeText(context, dataItem["link"].toString(), Toast.LENGTH_SHORT).show()
                //openIntent.data = Uri.parse(dataItem["link"])
                context.startActivity(openIntent)
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

            true
        }

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