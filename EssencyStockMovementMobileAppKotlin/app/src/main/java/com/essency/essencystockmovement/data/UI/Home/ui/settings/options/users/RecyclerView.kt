package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.users

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.model.AppUser

class UsersAdapter(private var users: List<AppUser>) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userIDTextView: TextView = itemView.findViewById(R.id.textViewUserID)
        val userNameTextView: TextView = itemView.findViewById(R.id.textViewUserName)
        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        val lastNameTextView: TextView = itemView.findViewById(R.id.textViewLastName)
        val isAdminTextView: TextView = itemView.findViewById(R.id.textViewIsAdmin)
        val enableTextView: TextView = itemView.findViewById(R.id.textViewEnable)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        val context = holder.itemView.context
        holder.userIDTextView.text = context.getString(R.string.settings_users_user_id) + ": " + user.id.toString()
        holder.userNameTextView.text = context.getString(R.string.settings_users_user) + ": " + user.userName
        holder.nameTextView.text = context.getString(R.string.settings_users_user_name) + ": " + user.name
        holder.lastNameTextView.text = context.getString(R.string.settings_users_user_lastname) + ": " + user.lastName

        // Usar cadenas desde resources para IsAdmin
        holder.isAdminTextView.text = if (user.isAdmin) {
            context.getString(R.string.settings_users_user_admin_status) + ": " + context.getString(R.string.administrator)
        } else {
            context.getString(R.string.settings_users_user_admin_status) + ": " + context.getString(R.string.user)
        }

        // Usar cadenas desde resources para Enable
        holder.enableTextView.text = if (user.enable) {
            context.getString(R.string.settings_users_user_active) + ": " + context.getString(R.string.active)
        } else {
            context.getString(R.string.settings_users_user_active) + ": " + context.getString(R.string.inactive)
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<AppUser>) {
        users = newUsers
        notifyDataSetChanged()
    }
}