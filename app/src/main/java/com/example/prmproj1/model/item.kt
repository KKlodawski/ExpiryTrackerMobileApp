package com.example.prmproj1.model

import java.time.LocalDate

enum class Category {
    GROCERIES,
    MEDICINES,
    COSMETICS
}

enum class UnitType {
    pcs,
    unit,
    g,
    kg,
    ml,
    l,


}

data class item(
    val title: String,
    val expireDate: LocalDate,
    val category: Category,
    val quantity: Int?,
    val unitType: UnitType?

) {
    fun isExpired() : Boolean {
        return expireDate >= LocalDate.now()
    }
}
