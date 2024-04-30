package com.example.prmproj1.data

import android.annotation.SuppressLint
import com.example.prmproj1.model.Category
import com.example.prmproj1.model.UnitType
import com.example.prmproj1.model.item
import java.time.LocalDate

object ItemRepositoryObjects : ItemRepository {
    @SuppressLint("ResourceType")
    private val itemList = mutableListOf(
        item("Chleb", LocalDate.now().plusDays(-1), Category.GROCERIES, 10, UnitType.unit),
        item("Masło", LocalDate.now().plusDays(2), Category.GROCERIES, 200, UnitType.g),
        item("Pasta do zębów", LocalDate.now().plusDays(120), Category.COSMETICS, 1, UnitType.unit),
        item("Aspiryna",LocalDate.now().plusDays(300), Category.MEDICINES, null, null),
        item("Apap",LocalDate.now().plusDays(-2), Category.MEDICINES, 6, UnitType.pcs)
    )

    override fun getItemList(): List<item>  {
        itemList.sortBy{ item -> item.expireDate }
        return itemList
    }

    override fun add(itm: item) {
        itemList.add(itm)
    }

    override fun getItemById(id: Int): item = itemList[id]
    override fun edit(id: Int, itm: item) { itemList[id] = itm }
    override fun remove(itm: item) {
        itemList.remove(itm)
    }


}