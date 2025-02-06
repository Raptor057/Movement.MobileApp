package com.essency.essencystockmovement.data.interfaces

import com.essency.essencystockmovement.data.model.AppUser
import com.essency.essencystockmovement.data.model.UserMovementData

// IAppUserRepository.kt
interface IAppUserRepository {
    fun insert(appUser: AppUser): Long
    fun getAll(): List<AppUser>
    fun getById(id: Int): AppUser?
    fun getByUserName(userName: String): AppUser? // Nuevo método agregado
    fun getUserMovementData(username: String, type: String): UserMovementData? // Nuevo método agregado
    fun update(appUser: AppUser): Int
    fun deleteById(id: Int): Int
    fun login(username: String, password: String): Boolean
}
