package com.house.criminalintent.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.house.criminalintent.database.CrimeDbSchema.*

class CrimeBaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    companion object {
        private const val VERSION = 1
        private const val DATABASE_NAME = "crimeBase.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("create table ${CrimeTable.NAME}" +
                "(_id integer primary key autoincrement," +
                "${CrimeTable.Cols.UUID}," +
                "${CrimeTable.Cols.TITLE}," +
                "${CrimeTable.Cols.DATE}," +
                "${CrimeTable.Cols.SOLVED})")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        
    }

}