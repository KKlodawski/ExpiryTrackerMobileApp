package com.example.prmproj1.model

import java.time.LocalDate

enum class Category {
    GROCERIES,
    MEDICINES,
    COSMETICS
}

data class item(
    val title: String,
    val expireDate: LocalDate,
    val category: Category,
    val quantity: Int?

) {
    fun isExpired() : Boolean {
        return expireDate >= LocalDate.now()
    }
}
