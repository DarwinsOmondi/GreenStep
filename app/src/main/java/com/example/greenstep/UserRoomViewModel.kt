package com.example.greenstep

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenstep.roomDatabase.UserDataBase
import com.example.greenstep.roomDatabase.UserModel
import com.example.greenstep.roomDatabase.UserRepository
import kotlinx.coroutines.launch

class UserRoomViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository
    val allUsers: kotlinx.coroutines.flow.Flow<List<UserModel>>

    init {
        val dao = UserDataBase.getUserDataBase(application).userDao()
        repository = UserRepository(dao)
        allUsers = repository.allUsers
    }

    fun addUser(name: String, imgURL: String) {
        viewModelScope.launch {
            repository.insertUser(UserModel(userName = name))
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            repository.deleteUser()
        }
    }
}