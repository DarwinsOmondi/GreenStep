package com.example.greenstep.roomDatabase


class UserRepository(private val dao: UserDao) {
    val allUsers = dao.getUser()
    suspend fun insertUser(user: UserModel) = dao.insertUser(user)
    suspend fun deleteUser() = dao.deleteUser()
}