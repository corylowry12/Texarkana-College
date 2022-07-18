package com.cory.texarkanacollege.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ClassesDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY, "
                    + COLUMN_CLASS_NAME + " TEXT, "
                    + COLUMN_CLASS_ID + " TEXT, "
                    + COLUMN_CLASS_TIME + " TEXT );")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_CLASS_NAME TEXT DEFAULT \"\" NOT NULL")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        try {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_CLASS_TIME TEXT DEFAULT \"\" NOT NULL")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        try {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_CLASS_ID TEXT DEFAULT \"\" NOT NULL")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertRow(
        className: String,
        classTime: String
    ) {
        val values = ContentValues()
        values.put(COLUMN_CLASS_NAME, className)
        values.put(COLUMN_CLASS_TIME, classTime)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun insertRestoreRow(
        classID: String,
        className: String,
        classTime: String
    ) {
        val values = ContentValues()
        values.put(COLUMN_CLASS_NAME, className)
        values.put(COLUMN_CLASS_TIME, classTime)
        values.put(COLUMN_CLASS_ID, classID)

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
        values.put(COLUMN_CLASS_NAME, className)
        values.put(COLUMN_CLASS_TIME, classTime)

        val db = this.writableDatabase

        db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(id))

    }

    fun classIDUpdate(
        id: String,
        primaryKey: String
    ) {
        val values = ContentValues()
        values.put(COLUMN_CLASS_ID, id)

        val db = this.writableDatabase

        db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(primaryKey))

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

    fun getAllRow(): Cursor? {
        val db = this.writableDatabase

       // return db.rawQuery("SELECT $COLUMN_ID, $COLUMN_CLASS_NAME, $COLUMN_CLASS_TIME FROM $TABLE_NAME ORDER BY $COLUMN_CLASS_NAME asc", null)

        return db.query(
            TABLE_NAME,
            arrayOf(
                COLUMN_ID,
                COLUMN_CLASS_NAME,
                COLUMN_CLASS_TIME
            ),
            null,
            null,
            null,
            null,
            COLUMN_CLASS_NAME
        )
    }

    fun deleteAll() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.execSQL("delete from $TABLE_NAME")
        db.close()
    }

    companion object {
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "classes.db"
        const val TABLE_NAME = "classes"

        const val COLUMN_ID = "id"
        const val COLUMN_CLASS_ID = "classID"
        const val COLUMN_CLASS_NAME = "className"
        const val COLUMN_CLASS_TIME = "classTime"
    }
}
