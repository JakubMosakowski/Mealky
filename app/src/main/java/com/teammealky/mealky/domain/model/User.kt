package com.teammealky.mealky.domain.model


data class User(
        val id: Int,
        val email: String?,
        val password: String?,
        //todo username and email should not be null, need change in api
        val username: String?
) {
    companion object {
        fun defaultUser(): User {
            return User(0, null, null, "default_user")
        }
    }
}