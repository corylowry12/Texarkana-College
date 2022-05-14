package com.cory.texarkanacollege

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.math.RoundingMode

class ClassesAdapter(val context: Context,
                     private val dataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title = itemView.findViewById<TextView>(R.id.row_class)!!

        fun bind(position: Int) {

            val dataItem = dataList[position]

            title.text = "Class Name: " + dataItem["className"]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.classes_list_item, parent, false))
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

      val classAverage = holder.itemView.findViewById<TextView>(R.id.row_class_average)
        val gradesDBHandler = GradesDBHelper(context, null)

        val gradesCount = gradesDBHandler.getCount()

        var grades = 0.0
        var weights = 0.0
        val dataItem = dataList[holder.adapterPosition]

        val cursor = GradesDBHelper(context, null).getGrades(dataItem["id"].toString())
        cursor!!.moveToFirst()

        while (!cursor.isAfterLast) {
            val map = HashMap<String, String>()
            map["id"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_ID))
            map["grade"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_GRADE))
            map["weight"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_WEIGHT))
            grades += map["grade"]!!.toDouble() * map["weight"]!!.toDouble()
            weights += map["weight"]!!.toDouble()

            cursor.moveToNext()

        }
        if (grades > 0.0 && weights > 0.0) {
            val average =
                (grades / weights).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
            classAverage.text = "Class Average: " + average.toString()
        }
        else {
            classAverage.text = "Class Average: 0"
        }

        holder.itemView.setOnClickListener {

            val itemPositionData = ItemID(context)
            itemPositionData.setPosition(dataItem["id"]!!.toInt())
            val gradeFragment = GradeFragment()

            val manager =
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            manager.replace(R.id.fragment_container, gradeFragment)
                .addToBackStack(null)
            manager.commit()
        }

        holder.itemView.setOnLongClickListener {

            val dialog = BottomSheetDialog(context)
            val addGradeView = LayoutInflater.from(context).inflate(R.layout.options_bottom_sheet, null)
            dialog.setCancelable(false)
            dialog.setContentView(addGradeView)

            val editButton = dialog.findViewById<Button>(R.id.editButton)
            val deleteButton = dialog.findViewById<Button>(R.id.deleteButton)
            val deleteAllButton = dialog.findViewById<Button>(R.id.deleteAllButton)
            val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

            deleteButton?.setOnClickListener {
                val datalist = dataList[holder.adapterPosition]
                val id = datalist["id"]

                val classesDBHelper = ClassesDBHelper(context, null)
                val gradesDBHelper = GradesDBHelper(context, null)

                val mapGrades = HashMap<String, String>()
                val cursorGrades = gradesDBHandler.getGrades(id.toString())
                cursorGrades.moveToFirst()
                if (cursorGrades.count > 0) {

                    while (!cursorGrades.isAfterLast) {

                        mapGrades["id"] =
                            cursorGrades.getString(cursorGrades.getColumnIndex(GradesDBHelper.COLUMN_CLASS_ID))
                        mapGrades["grades"] =
                            cursorGrades.getString(cursorGrades.getColumnIndex(GradesDBHelper.COLUMN_GRADE))

                        gradesDBHelper.deleteRow(mapGrades["id"].toString())
                        cursorGrades.moveToNext()
                    }
                }

                val mapClasses = HashMap<String, String>()
                val cursorClasses = classesDBHelper.getRow(id.toString())
                cursorClasses.moveToFirst()
                if (cursorClasses.count > 0) {

                    while (!cursorClasses.isAfterLast) {

                        mapClasses["id"] =
                            cursorClasses.getString(cursorClasses.getColumnIndex(ClassesDBHelper.COLUMN_ID))
                        mapClasses["grades"] =
                            cursorClasses.getString(cursorClasses.getColumnIndex(ClassesDBHelper.COLUMN_CLASS_NAME))


                        classesDBHelper.deleteRow(mapClasses["id"].toString())
                        cursorClasses.moveToNext()
                    }
                }

                dataList.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
            }
            cancelButton?.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            return@setOnLongClickListener true
        }

        (holder as ClassesAdapter.ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}