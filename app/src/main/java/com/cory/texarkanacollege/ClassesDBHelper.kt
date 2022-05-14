package com.cory.texarkanacollege

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ClassesDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            "CREATE TABLE $TABLE_NAME " +
                    "($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_CLASS_NAME TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertRow(
        className: String,
    ) {
        val values = ContentValues()
        values.put(COLUMN_CLASS_NAME, className)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun update(
        id: String,
        className: String,
    ) {
        val values = ContentValues()
        values.put(COLUMN_CLASS_NAME, className)

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

    fun getRow(row_id: String): Cursor {

        val db = this.writableDatabase

        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID=$row_id", null)

    }

    fun getAllRow(context: Context): Cursor? {
        val db = this.writableDatabase

        return db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_CLASS_NAME asc", null)
    }

    fun deleteAll() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.execSQL("delete from $TABLE_NAME")
        db.close()
    }

    companion object {
        const val DATABASE_VERSION = 3
        const val DATABASE_NAME = "classes.db"
        const val TABLE_NAME = "classes"

        const val COLUMN_ID = "id"
        const val COLUMN_CLASS_NAME = "className"
    }
}
