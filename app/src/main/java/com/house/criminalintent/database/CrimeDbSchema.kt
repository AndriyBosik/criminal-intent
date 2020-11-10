package com.house.criminalintent.database

class CrimeDbSchema {

    class CrimeTable {
        companion object {
            const val NAME = "crimes"
        }

        class Cols {
            companion object {
                const val UUID = "uuid"
                const val TITLE = "title"
                const val DATE = "date"
                const val SOLVED = "solved"
                const val SUSPECT = "suspect"
            }
        }
    }

}