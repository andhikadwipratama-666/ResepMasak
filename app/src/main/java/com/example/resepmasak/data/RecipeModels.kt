package com.example.resepmasak.data

data class UserAccount(
    val fullName: String,
    val email: String,
    val password: String
)

data class RecipeItem(
    val id: Int,
    val title: String,
    val category: String,
    val cookTimeMinutes: Int,
    val ingredients: String,
    val steps: String
)