package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.repository.AppUserRepository
import com.essency.essencystockmovement.databinding.FragmentSettingsUsersGetBinding

class GetUsersFragment : Fragment() {

    private var _binding: FragmentSettingsUsersGetBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: AppUserRepository
    private lateinit var adapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsUsersGetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializa la base de datos y el repositorio
        val dbHelper = MyDatabaseHelper(requireContext())
        userRepository = AppUserRepository(dbHelper)

        setupRecyclerView()

        // Carga los usuarios desde la base de datos
        loadUsers()

        return root
    }

    private fun setupRecyclerView() {
        adapter = UsersAdapter(emptyList())
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUsers.adapter = adapter
    }

    private fun loadUsers() {
        try {
            val users = userRepository.getAll()
            if (users.isNotEmpty()) {
                adapter.updateUsers(users)
            } else {
                Toast.makeText(requireContext(), "No hay usuarios registrados.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al cargar usuarios: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
