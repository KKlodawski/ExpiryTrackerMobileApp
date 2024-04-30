package com.example.prmproj1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.prmproj1.data.ItemRepository
import com.example.prmproj1.databinding.ItemContentBinding
import com.example.prmproj1.model.Category
import com.example.prmproj1.model.item
import java.time.format.DateTimeFormatter

class ItemObject(private val itemViewBinding: ItemContentBinding) : RecyclerView.ViewHolder(itemViewBinding.root) {
        fun onBind(itemObject: item, onItemClick: () -> Unit) = with(itemViewBinding) {
            itemTitle.text = itemObject.title
            itemExpireDate.text = itemObject.expireDate.format(DateTimeFormatter.ISO_DATE)
            itemCategory.text = itemObject.category.toString()
            itemQuantity.text =
                if(itemObject.quantity == null && itemObject.unitType == null)
                    "Unspecified" else "${itemObject.quantity.toString()}${itemObject.unitType.toString()}"
            itemExpired.text = if (itemObject.isExpired()) "No" else "Yes"
            root.setOnClickListener {
                if (itemObject.isExpired()) {
                    onItemClick()
                } else {
                    Toast.makeText(root.context, "Unable to edit expired item!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
}

class ItemListAdapter(private val onItemClick : (Int) -> Unit) : RecyclerView.Adapter<ItemObject>() {

    private lateinit var onItemLongClickListener : ((item, Int) -> Unit)
    fun setOnItemLongClickListener(onItemLongClickListener: (item, Int) -> Unit) {
        this.onItemLongClickListener = onItemLongClickListener
    }

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

    override fun onBindViewHolder(holder: ItemObject, position: Int) {
        holder.onBind(filteredItemList[position]) { onItemClick(position) }
        holder.itemView.setOnLongClickListener {
            //onItemLongClickListener.invoke(itemList[position], position)
            onItemLongClickListener.invoke(filteredItemList[position], position)
            true
        }
    }

    fun filterItems() {
        filteredItemList = itemList.filter { item ->
            (filterCategory == null || item.category == filterCategory) &&
            (filterExpired == null || item.isExpired() == filterExpired)
        }
        notifyDataSetChanged()
    }

    fun removeItem(itemRepository: ItemRepository, position: Int, itm: item) {
        itemRepository.remove(itm)
        this.notifyItemRemoved(position)
        this.filterItems()
    }

}