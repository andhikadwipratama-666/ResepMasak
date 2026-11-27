package com.example.resepmasak.viewmodel

import androidx.lifecycle.ViewModel
import com.example.resepmasak.data.LocalRecipeRepository
import com.example.resepmasak.data.RecipeItem
import com.example.resepmasak.data.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

class RecipeViewModel(
    private val repository: LocalRecipeRepository = LocalRecipeRepository()
) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _authMessage = MutableStateFlow<String?>(null)
    val authMessage: StateFlow<String?> = _authMessage.asStateFlow()

    val categories = listOf("Semua", "Utama", "Camilan", "Minuman", "Penutup")

    // Menggabungkan data resep mentah dengan filter pencarian & kategori
    val filteredRecipes: StateFlow<List<RecipeItem>> = combine(
        repository.recipesFlow,
        _searchQuery,
        _selectedCategory
    ) { recipes, query, category ->
        // Memanggil fungsi READ utama dari repository
        val baseList = if (recipes.isNotEmpty()) repository.getAllRecipes() else emptyList()
        baseList.filter { item ->
            val matchesCategory = category == "Semua" || item.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.ingredients.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.getAllRecipes()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun clearAuthMessage() {
        _authMessage.value = null
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        val result = repository.loginUser(email, password)
        result.onSuccess { user ->
            _currentUser.value = user
            _authMessage.value = null
            onSuccess()
        }.onFailure { error ->
            _authMessage.value = error.message ?: "Gagal masuk."
        }
    }

    fun register(fullName: String, email: String, password: String, onSuccess: () -> Unit) {
        val result = repository.registerUser(fullName, email, password)
        result.onSuccess {
            _authMessage.value = "Akun berhasil dibuat. Silakan masuk."
            onSuccess()
        }.onFailure { error ->
            _authMessage.value = error.message ?: "Gagal mendaftar."
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        _currentUser.value = null
        _searchQuery.value = ""
        _selectedCategory.value = "Semua"
        onLoggedOut()
    }

    fun getRecipeById(id: Int): RecipeItem? {
        return repository.getAllRecipes().firstOrNull { it.id == id }
    }

    fun addRecipe(
        title: String,
        category: String,
        cookTimeMinutes: Int,
        ingredients: String,
        steps: String
    ) {
        repository.addRecipe(title, category, cookTimeMinutes, ingredients, steps)
    }

    fun updateRecipe(item: RecipeItem) {
        repository.updateRecipe(item)
    }

    fun deleteRecipe(id: Int) {
        repository.deleteRecipe(id)
    }
}