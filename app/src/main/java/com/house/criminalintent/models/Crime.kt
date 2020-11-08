package com.house.criminalintent.models

import java.util.*

class Crime {

    var id: UUID
        private set

    var date: Date
    var title: String = ""
    var solved: Boolean = false

    init {
        id = UUID.randomUUID()
        date = Date()
    }
}