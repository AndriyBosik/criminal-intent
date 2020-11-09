package com.house.criminalintent.models

import java.util.*

class Crime(id: UUID) {

    var id: UUID
        private set

    var date: Date
    var title: String = ""
    var solved: Boolean = false

    init {
        this.id = id
        date = Date()
    }

    constructor(): this(UUID.randomUUID())
}