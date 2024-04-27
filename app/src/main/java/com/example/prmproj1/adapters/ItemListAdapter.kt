package com.example.prmproj1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prmproj1.databinding.ItemContentBinding
import com.example.prmproj1.model.Category
import com.example.prmproj1.model.item
import java.time.format.DateTimeFormatter

class ItemObject(val itemViewBinding: ItemContentBinding) : RecyclerView.ViewHolder(itemViewBinding.root) {
        fun onBind(itemObject: item) = with(itemViewBinding) {
            itemTitle.text = itemObject.title
            itemExpireDate.text = itemObject.expireDate.format(DateTimeFormatter.ISO_DATE)
            itemCategory.text = itemObject.category.toString()
            itemQuantity.text = if(itemObject.quantity == null) "Unspecified" else itemObject.quantity.toString()
            itemExpired.text = if (itemObject.isExpired()) "No" else "Yes"
        }
}

class ItemListAdapter : RecyclerView.Adapter<ItemObject>() {
    var itemList: List<item> = mutableListOf()
    var filteredItemList: List<item> = emptyList()
    var filterCategory: Category? = null
    var filterExpired: Boolean? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemObject {
        val layoutInflater =  LayoutInflater.from(parent.context)
        val binding = ItemContentBinding.inflate(layoutInflater, parent, false)

        return ItemObject(binding)
    }

    override fun getItemCount(): Int {
        return filteredItemList.size
    }

    fun getOriginalItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemObject, position: Int) {
        holder.onBind(filteredItemList[position])
    }

    fun filterItems() {
        filteredItemList = itemList.filter { item ->
            (filterCategory == null || item.category == filterCategory) &&
            (filterExpired == null || item.isExpired() == filterExpired)
        }
        notifyDataSetChanged()
    }


}