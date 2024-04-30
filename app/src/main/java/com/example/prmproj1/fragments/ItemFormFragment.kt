package com.example.prmproj1.fragments

import android.R.layout
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.prmproj1.data.ItemRepository
import com.example.prmproj1.data.RepositoryLocator
import com.example.prmproj1.databinding.FragmentItemFormBinding
import com.example.prmproj1.model.Category
import com.example.prmproj1.model.FormType
import com.example.prmproj1.model.UnitType
import com.example.prmproj1.model.item
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

private const val TYPE_KEY = "type"
class ItemFormFragment : Fragment() {
    private lateinit var type: FormType
    private lateinit var binding: FragmentItemFormBinding
    private lateinit var repository: ItemRepository
    private var itemToEdit: item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = RepositoryLocator.itemRepository
        arguments?.let {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable(TYPE_KEY, FormType::class.java)
            } else {
                it.getSerializable(TYPE_KEY) as? FormType
            } ?: FormType.New

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentItemFormBinding.inflate(layoutInflater, container,false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding.confirmationButton){
            setOnClickListener {
                if(saveItem((type as? FormType.Edit)?.id)) findNavController().popBackStack()
                else showPopUp("Unable to confirm")
            }
        }
        binding.editExpireDate.apply {
            requireView()
            isFocusable = false
            setOnClickListener {
                showDatePickerDialog(this)
            }
        }
        binding.editCategory.apply {
            adapter = ArrayAdapter<String>(
                requireContext(),
                layout.simple_spinner_dropdown_item,
                Category.entries.map { it.name }
            )
            (adapter as ArrayAdapter<String>).insert("Select option", 0)
        }
        binding.editUnitType.apply {
            adapter = ArrayAdapter<String>(
                requireContext(),
                layout.simple_spinner_dropdown_item,
                UnitType.entries.map { it.name }
            )
            (adapter as ArrayAdapter<String>).insert("Select option", 0 )
        }
        (type as? FormType.Edit)?.let {
            itemToEdit = repository.getItemById(it.id).also {
                with(binding.editTitle) {
                    setText(it.title)
                }
                with(binding.editQuantity) {
                    if (it.quantity != null) setText(it.quantity.toString())
                }
                with(binding.editExpireDate) {
                    setText(it.expireDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString())
                }
                with(binding.editCategory) {
                    for (i in 0 until this.count){
                        if (this.getItemAtPosition(i).toString() == it.category.toString()) {
                            setSelection(i)
                            break
                        }
                    }
                }
                with(binding.editUnitType) {
                    for(i in 0 until this.count) {
                        if(this.getItemAtPosition(i).toString() == it.unitType.toString()) {
                            setSelection(i)
                            break
                        }
                    }
                }
            }


        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth = if(selectedMonth + 1 < 10) "0${selectedMonth +1}" else "${selectedMonth}"
                val selectedDate = "${selectedDay}/${formattedMonth}/${selectedYear}"
                editText.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun saveItem(id: Int?): Boolean {
        try {
            val title = binding.editTitle.text.toString()
            val expireDate = binding.editExpireDate.text.toString()
            val category = binding.editCategory.selectedItem.toString()
            val quantity = binding.editQuantity.text.toString()
            val unitType: UnitType? = try {
                UnitType.valueOf(binding.editUnitType.selectedItem.toString())
            } catch (e: IllegalArgumentException) {
                null
            }

            if (title.isEmpty() ||
                expireDate.isEmpty() ||
                (quantity.isEmpty() && unitType != null) ||
                (quantity.isNotEmpty() && unitType == null)
            ) return false

            val parsedDate: LocalDate = LocalDate.parse(
                expireDate,
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            )

            if (parsedDate < LocalDate.now()) return false

            val itm = itemToEdit?.copy(
                title,
                parsedDate,
                Category.valueOf(category),
                quantity.toIntOrNull(),
                unitType
            ) ?: item(
                title,
                parsedDate,
                Category.valueOf(category),
                quantity.toIntOrNull(),
                unitType
            )

            if(id == null) repository.add(itm)
            else repository.edit(id, itm)
            return true
        } catch (e: Throwable) {
            Log.d("test", "SaveItem(): ${e.message.toString()}")
        }
        return false
    }

    private fun showPopUp(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    }
}