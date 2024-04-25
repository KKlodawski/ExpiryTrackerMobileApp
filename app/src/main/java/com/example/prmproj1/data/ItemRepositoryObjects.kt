package com.example.prmproj1.data

import android.annotation.SuppressLint
import com.example.prmproj1.model.Category
import com.example.prmproj1.model.item
import java.time.LocalDate
import java.util.Calendar

object ItemRepositoryObjects : ItemRepository {
    @SuppressLint("ResourceType")
    private val itemList = mutableListOf(
        item("Chlep", LocalDate.now().plusDays(-1), Category.GROCERIES, 10),
        item("Masło", LocalDate.now().plusDays(2), Category.GROCERIES, 10),
        item("Pasta", LocalDate.now().plusDays(120), Category.COSMETICS, 2),
        item("Aspiryna",LocalDate.now().plusDays(300), Category.MEDICINES, 10),
        item("Apap",LocalDate.now().plusDays(-2), Category.MEDICINES, 2)
    )

    override fun getItemList(): List<item>  {
        itemList.sortBy{ item -> item.expireDate }
        return itemList
    }

}