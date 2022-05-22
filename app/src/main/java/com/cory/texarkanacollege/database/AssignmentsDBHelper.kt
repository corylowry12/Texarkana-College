package com.cory.texarkanacollege.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class AssignmentsDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            "CREATE TABLE $TABLE_NAME " +
                    "($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_CLASS_NAME TEXT, $COLUMN_ASSIGNMENT_NAME TEXT, $COLUMN_ASSIGNMENT_DUE_DATE TEXT, $COLUMN_NOTES TEXT, $COLUMN_STATUS TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${GradesDBHelper.TABLE_NAME}")
        onCreate(db)
    }

    fun insertRow(
        assignmentName: String,
        dueDate: String,
        notes: String,
        status: String,
        className: String
    ) {
        val values = ContentValues()
        values.put(COLUMN_ASSIGNMENT_NAME, assignmentName)
        values.put(COLUMN_ASSIGNMENT_DUE_DATE, dueDate)
        values.put(COLUMN_NOTES, notes)
        values.put(COLUMN_STATUS, status)
        values.put(COLUMN_CLASS_NAME, className)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun update(
        id: String,
        className: String,
        classTime: String
    ) {
        val values = ContentValues()
        values.put(COLUMN_ASSIGNMENT_NAME, className)
        values.put(COLUMN_ASSIGNMENT_DUE_DATE, classTime)

        val db = this.writableDatabase

        db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(id))

    }

    fun getCount(): Int {
        val db = this.readableDatabase
        return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM $TABLE_NAME", null).toInt()

    }

    fun deleteRow(row_id: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(row_id))
        db.close()

    }

    fun getUpcoming(): Cursor {

        val db = this.writableDatabase
        val formatter = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
        val dateFormatted = formatter.format(Date())
        return db.query(
            TABLE_NAME,
            arrayOf(
                COLUMN_ID,
                COLUMN_NOTES,
                COLUMN_STATUS,
                COLUMN_ASSIGNMENT_DUE_DATE,
                COLUMN_ASSIGNMENT_NAME
            ),
            "$COLUMN_ASSIGNMENT_DUE_DATE >= '$dateFormatted' AND $COLUMN_STATUS != 'done'",
            null,
            null,
            null,
            null
        )
    }

    fun getPastDue(): Cursor {

        val db = this.writableDatabase
        val formatter = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
        val dateFormatted = formatter.format(Date())
        return db.query(
            TABLE_NAME,
            arrayOf(
                COLUMN_ID,
                COLUMN_NOTES,
                COLUMN_STATUS,
                COLUMN_ASSIGNMENT_DUE_DATE,
                COLUMN_ASSIGNMENT_NAME
            ),
            "$COLUMN_ASSIGNMENT_DUE_DATE < '$dateFormatted' AND $COLUMN_STATUS != 'done'",
            null,
            null,
            null,
            null
        )

    }

    fun getDone(): Cursor {

        val db = this.writableDatabase

        return db.query(
            TABLE_NAME,
            arrayOf(
                COLUMN_ID,
                COLUMN_NOTES,
                COLUMN_STATUS,
                COLUMN_ASSIGNMENT_DUE_DATE,
                COLUMN_ASSIGNMENT_NAME
            ),
            "$COLUMN_STATUS == 'done'",
            null,
            null,
            null,
            null
        )

    }

    fun getAllRow(context: Context): Cursor? {
        val db = this.writableDatabase

        return db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_ASSIGNMENT_DUE_DATE asc", null)
    }

    fun deleteAll() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.execSQL("delete from $TABLE_NAME")
        db.close()
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "assignments.db"
        const val TABLE_NAME = "assignments"

        const val COLUMN_ID = "id"
        const val COLUMN_CLASS_NAME = "assignmentClassName"
        const val COLUMN_ASSIGNMENT_NAME = "assignmentName"
        const val COLUMN_ASSIGNMENT_DUE_DATE = "assignmentDueDate"
        const val COLUMN_NOTES = "assignmentNotes"
        const val COLUMN_STATUS = "assignmentStatus"
    }
}
