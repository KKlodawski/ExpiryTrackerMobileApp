package com.example.prmproj1.fragments

import android.R.layout
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
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
import com.example.prmproj1.model.item
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

private const val TYPE_KEY = "type"
class ItemFormFragment : Fragment() {
    private lateinit var type: FormType
    private lateinit var binding: FragmentItemFormBinding
    private lateinit var repository: ItemRepository

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
                if(saveItem()) findNavController().popBackStack()
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

    private fun saveItem(): Boolean {
        if(binding.editTitle.text.toString().isEmpty() ||
            binding.editExpireDate.text.toString().isEmpty() ||
            binding.editCategory.selectedItem.toString().equals("Select option")
            ) return false

        val itm = item(
            binding.editTitle.text.toString(),
            LocalDate.parse(
                binding.editExpireDate.text.toString(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            ),
            Category.valueOf(binding.editCategory.selectedItem.toString()),
            binding.editQuantity.text.toString().toIntOrNull()
        )

        if(itm.expireDate < LocalDate.now()) return false

        repository.add(itm)
        return true
    }

    private fun showPopUp(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    }
}