package com.house.criminalintent.models

import android.content.Context
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
    }

    var crimes: MutableList<Crime>
        private set

    init {
        crimes = mutableListOf()
    }

    fun addCrime(crime: Crime) = crimes.add(crime)

    fun getCrime(id: UUID): Crime = crimes.first { crime -> crime.id == id }

}