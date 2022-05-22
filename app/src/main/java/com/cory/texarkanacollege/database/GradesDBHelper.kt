package com.cory.texarkanacollege.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GradesDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            "CREATE TABLE $TABLE_NAME " +
                    "($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_CLASS_ID TEXT, $COLUMN_NAME TEXT, $COLUMN_GRADE TEXT, $COLUMN_WEIGHT TEXT, $COLUMN_DATE TEXT, $COLUMN_IMAGE TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertRow(
        id: String,
        name: String,
        grade: String,
        weight: String,
        date: String,
        image: String
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

    fun getCount(id: String): Int {
        val db = this.readableDatabase
        val query = db!!.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME WHERE $COLUMN_CLASS_ID = $id", null)
        query.moveToFirst()
        val count = query.getInt(0)
        return count
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

    fun deleteAllGradesForOneClass(row_id: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_CLASS_ID =?", arrayOf(row_id))
        db.close()

    }

    fun getGrades(row_id: String): Cursor {

        val db = this.writableDatabase

        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_CLASS_ID=$row_id ORDER BY $COLUMN_DATE DESC", null)

    }

    fun getImage(row_id: String): Cursor {

        val db = this.writableDatabase

        return db.rawQuery("SELECT $COLUMN_IMAGE FROM $TABLE_NAME WHERE $COLUMN_ID=$row_id ORDER BY $COLUMN_DATE DESC", null)

    }

    fun getImage(row_id: String, key: String): Cursor {

        val db = this.writableDatabase

        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_CLASS_ID=$row_id AND $COLUMN_ID=$key", null)

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
        const val DATABASE_VERSION = 1
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