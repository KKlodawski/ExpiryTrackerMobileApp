package com.example.prmproj1.data

import com.example.prmproj1.model.item

interface ItemRepository {
    fun getItemList() : List<item>
    fun add(itm: item)
    fun getItemById(id: Int): item
    fun edit(id: Int, itm: item)
    fun remove(itm: item)
}