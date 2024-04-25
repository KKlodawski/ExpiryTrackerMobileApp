package com.example.prmproj1.model

import android.media.Image
import androidx.annotation.DrawableRes
import java.time.LocalDate
import java.util.Date

enum class Category {
    GROCERIES,
    MEDICINES,
    COSMETICS
}

data class item(
    val title: String,
    val expireDate: LocalDate,
   // @DrawableRes
   // val imageId: Int,
    val category: Category,
    val quantity: Int

) {
    fun isExpired() : Boolean {
        return expireDate > LocalDate.now()
    }
}
