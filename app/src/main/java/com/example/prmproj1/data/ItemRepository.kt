package com.example.prmproj1.data

import com.example.prmproj1.model.item

interface ItemRepository {
    fun getItemList() : List<item>
}