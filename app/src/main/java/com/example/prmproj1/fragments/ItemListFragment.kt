package com.example.prmproj1.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prmproj1.R
import com.example.prmproj1.adapters.ItemListAdapter
import com.example.prmproj1.data.ItemRepository
import com.example.prmproj1.data.RepositoryLocator
import com.example.prmproj1.databinding.FragmentItemListBinding
import com.example.prmproj1.model.Category
import com.example.prmproj1.model.FormType
import com.google.android.material.snackbar.Snackbar

class ItemListFragment : Fragment() {
    lateinit var itemRepository: ItemRepository
    lateinit var binding: FragmentItemListBinding
    lateinit var itemListAdapter: ItemListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemRepository = RepositoryLocator.itemRepository
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentItemListBinding.inflate(layoutInflater, container, false)
            .also {
                binding = it
            }.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        itemListAdapter = ItemListAdapter {
            findNavController()
                .navigate(
                    R.id.action_itemListFragment_to_itemFormFragment,
                    bundleOf("type" to FormType.Edit(it))
                )
        }
        itemListAdapter.setOnItemLongClickListener { item, i ->
            Log.d("test", "${item} | ${i}")
            if (item.isExpired()) {
                Snackbar.make(
                    binding.root,
                    "Are sure to delete this item?",
                    Snackbar.LENGTH_SHORT
                ).setAction("Yes") {
                    itemListAdapter.removeItem(itemRepository, i, item)
                }.show()
            } else {
                itemListAdapter.removeItem(itemRepository, i, item)
            }
        }
        binding.itemsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemListAdapter
        }

        itemListAdapter.itemList = itemRepository.getItemList()

        binding.spinnerCategoryFilter.apply {
            adapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayOf("None") + Category.entries.map { it.name }
            )
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (parent != null) {
                        val selectedItem = parent.getItemAtPosition(position) as String
                        if(selectedItem != "None") itemListAdapter.filterCategory = Category.valueOf(selectedItem)
                        else itemListAdapter.filterCategory = null
                        itemListAdapter.filterItems()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    return
                }

            }

        }

        binding.spinnerExpiredFilter.apply {
            adapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayOf("None", "Yes", "No")
            )
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (parent != null) {
                        val selectedItem = parent.getItemAtPosition(position) as String
                        when (selectedItem) {
                            "Yes" -> itemListAdapter.filterExpired = false
                            "No" -> itemListAdapter.filterExpired = true
                            else -> itemListAdapter.filterExpired = null
                        }
                        itemListAdapter.filterItems()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    return
                }

            }
        }

        binding.itemAddButton.apply {
            setOnClickListener {
                findNavController().navigate(
                    R.id.action_itemListFragment_to_itemFormFragment,
                )

            }
        }

        binding.countTextView.apply {
            text = "no. items\n${itemListAdapter.itemCount}"
        }
        itemListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                binding.countTextView.text = "no. items\n${itemListAdapter.itemCount}"
            }
        })

    }

    fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if (destination.id == R.id.itemListFragment) {
            itemListAdapter.itemList = itemRepository.getItemList()
        }
    }

    override fun onStart() {
        super.onStart()
        findNavController().addOnDestinationChangedListener(::onDestinationChanged)
    }

    override fun onStop() {
        findNavController().removeOnDestinationChangedListener(::onDestinationChanged)
        super.onStop()
    }

}