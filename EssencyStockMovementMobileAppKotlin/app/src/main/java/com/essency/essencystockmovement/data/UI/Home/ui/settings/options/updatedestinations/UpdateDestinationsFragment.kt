package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.updatedestinations

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.model.MovementTypeDestination
import com.essency.essencystockmovement.data.repository.MovementTypeRepository
import com.essency.essencystockmovement.databinding.FragmentEditDestinationBinding

class UpdateDestinationsFragment : BaseFragment() {

    private var _binding: FragmentEditDestinationBinding? = null
    private val binding get() = _binding!!
    private lateinit var movementTypeRepository: MovementTypeRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditDestinationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root

    }
    private fun reloadList() {
        val updatedList = movementTypeRepository.getDestinationInMovementTypesByID()
        (binding.recyclerDestinationList.adapter as? DestinationAdapter)?.updateData(updatedList)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dbHelper = MyDatabaseHelper(requireContext())
        movementTypeRepository = MovementTypeRepository(dbHelper)


        val destinations = movementTypeRepository.getDestinationInMovementTypesByID()
        val adapter = DestinationAdapter(destinations) { selected ->
            showEditDialog(selected)
        }

        binding.recyclerDestinationList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDestinationList.adapter = adapter
    }

//    private fun showEditDialog(item: MovementTypeDestination) {
//        val editText = EditText(requireContext()).apply {
//            inputType = InputType.TYPE_CLASS_TEXT
//            setText(item.destination)
//        }
//
//        AlertDialog.Builder(requireContext())
//            .setTitle("Edit Destination")
//            .setView(editText)
//            .setPositiveButton("Save") { _, _ ->
//                val newDestination = editText.text.toString().trim()
//                if (newDestination.isNotEmpty()) {
//                    movementTypeRepository.updateDestinationInMovementTypeByID(newDestination, item.id)
//                    reloadList() //Actualiza la lista
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }

private fun showEditDialog(item: MovementTypeDestination) {
    val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_destination, null)
    val editText = dialogView.findViewById<EditText>(R.id.editDestinationInput)
    val buttonOk = dialogView.findViewById<Button>(R.id.buttonOkUpdate)

    editText.setText(item.destination)

    val dialog = AlertDialog.Builder(requireContext())
        .setTitle("Edit Destination")
        .setView(dialogView)
        .create()

    buttonOk.setOnClickListener {
        val newDestination = editText.text.toString().trim()
        if (newDestination.isNotEmpty()) {
            movementTypeRepository.updateDestinationInMovementTypeByID(newDestination, item.id)
            reloadList()
            dialog.dismiss()
        }
        Toast.makeText(requireContext(), "Update", Toast.LENGTH_SHORT).show()
    }
    dialog.show()
    }
}