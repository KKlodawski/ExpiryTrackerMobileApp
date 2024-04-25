package com.example.prmproj1

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prmproj1.adapters.ItemListAdapter
import com.example.prmproj1.data.ItemRepository
import com.example.prmproj1.data.ItemRepositoryObjects
import com.example.prmproj1.databinding.ActivityMainBinding
import com.example.prmproj1.model.Category

class MainActivity : AppCompatActivity() {
    lateinit var itemRepository: ItemRepository
    lateinit var binding: ActivityMainBinding
    lateinit var itemListAdapter: ItemListAdapter
    lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        itemRepository = ItemRepositoryObjects
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)



        itemListAdapter = ItemListAdapter()
        binding.itemsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemListAdapter

        }

        itemListAdapter.itemList = itemRepository.getItemList()

        binding.spinnerCategoryFilter.apply {
            adapter = ArrayAdapter<String>(
                this@MainActivity,
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
                    return;
                }

            }

        }

        binding.spinnerExpiredFilter.apply {
            adapter = ArrayAdapter<String>(
                this@MainActivity,
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
                        if(selectedItem == "Yes") itemListAdapter.filterExpired = false
                        else if(selectedItem == "No") itemListAdapter.filterExpired = true
                        else itemListAdapter.filterExpired = null
                        itemListAdapter.filterItems()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    return;
                }

            }
        }

        binding.countTextView.apply {
            text = "Ilość\n" + itemListAdapter.getOriginalItemCount().toString()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}