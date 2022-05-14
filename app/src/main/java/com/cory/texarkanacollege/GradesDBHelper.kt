package com.cory.texarkanacollege

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GradesDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            "CREATE TABLE $TABLE_NAME " +
                    "($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_CLASS_ID TEXT, $COLUMN_NAME TEXT, $COLUMN_GRADE TEXT, $COLUMN_WEIGHT TEXT, $COLUMN_DATE TEXT, $COLUMN_IMAGE BLOB)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertRow(
        id: String,
        name: String,
        grade: String,
        weight: String,
        date: String,
        image: ByteArray
    ) {
        val values = ContentValues()
        values.put(COLUMN_CLASS_ID, id)
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_GRADE, grade)
        values.put(COLUMN_WEIGHT, weight)
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_IMAGE, image)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun update(
        id: String,
        className: String,
    ) {
        val values = ContentValues()
        values.put(COLUMN_GRADE, className)

        val db = this.writableDatabase

        db.update(TABLE_NAME, values, "$COLUMN_CLASS_ID=?", arrayOf(id))

    }

    fun getCount(): Int {
        val db = this.readableDatabase
        return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM $TABLE_NAME", null).toInt()

    }

    fun deleteRow(row_id: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_CLASS_ID =?", arrayOf(row_id))
        db.close()

    }

    fun deleteSingleRow(row_id: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID =?", arrayOf(row_id))
        db.close()

    }

    fun getGrades(row_id: String): Cursor {

        val db = this.writableDatabase

        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_CLASS_ID=$row_id", null)

    }

    fun getSingleGrade(row_id: String): Cursor {

        val db = this.writableDatabase

        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID=$row_id", null)

    }

    fun getAllRow(context: Context): Cursor? {
        val db = this.writableDatabase

        return db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_GRADE asc", null)
    }

    fun deleteAll() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.execSQL("delete from $TABLE_NAME")
        db.close()
    }

    companion object {
        const val DATABASE_VERSION = 3
        const val DATABASE_NAME = "grades.db"
        const val TABLE_NAME = "grades"

        const val COLUMN_ID = "id"
        const val COLUMN_CLASS_ID = "class_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_GRADE = "grade"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_DATE = "date"
        const val COLUMN_IMAGE = "image"
    }
}