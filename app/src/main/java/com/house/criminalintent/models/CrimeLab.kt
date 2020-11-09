package com.house.criminalintent.models

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.house.criminalintent.database.CrimeBaseHelper
import com.house.criminalintent.database.CrimeCursorWrapper
import com.house.criminalintent.database.CrimeDbSchema
import com.house.criminalintent.database.CrimeDbSchema.*
import java.util.*

class CrimeLab private constructor(context: Context) {

    companion object {
        private var crimeLab: CrimeLab? = null

        fun get(context: Context): CrimeLab {
            if (crimeLab == null) {
                crimeLab = CrimeLab(context)
            }
            return crimeLab!!
        }

        private fun getContentValues(crime: Crime): ContentValues {
            val values = ContentValues()
            values.put(CrimeTable.Cols.UUID, crime.id.toString())
            values.put(CrimeTable.Cols.TITLE, crime.title)
            values.put(CrimeTable.Cols.DATE, crime.date.time)
            return values
        }
    }

    private var context: Context? = null
    private var database: SQLiteDatabase? = null

    var crimes: MutableList<Crime>
        get() {
            val crimes = mutableListOf<Crime>()
            val cursor = queryCrimes(null, null)
            cursor.use { cursor ->
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    crimes.add(cursor.getCrime())
                    cursor.moveToNext()
                }
            }
            return crimes
        }
        private set

    init {
        this.context = context.applicationContext
        database = CrimeBaseHelper(context).writableDatabase

        crimes = mutableListOf()
    }

    fun addCrime(crime: Crime) = database!!.insert(CrimeTable.NAME, null, getContentValues(crime))

    fun updateCrime(crime: Crime) {
        val uuidString = crime.id.toString()
        val values = getContentValues(crime)

        database!!.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?", arrayOf(uuidString))
    }

    private fun queryCrimes(whereClause: String?, whereArgs: Array<String>?): CrimeCursorWrapper {
        val cursor = database!!.query(
            CrimeTable.NAME,
            null, // with null all the columns will be selected
            whereClause,
            whereArgs,
            null,
            null,
            null)
        return CrimeCursorWrapper(cursor)
    }

    fun getCrime(id: UUID): Crime? {
        val cursor = queryCrimes(CrimeTable.Cols.UUID + " = ?", arrayOf(id.toString()))
        cursor.use { cursor ->
            if (cursor.count == 0) {
                return null
            }
            cursor.moveToFirst()
            return cursor.getCrime()
        }
    }
}