package com.example.resepmasak.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocalRecipeRepository {

    private val _users = MutableStateFlow(
        listOf(
            UserAccount(
                fullName = "Chef Pemula",
                email = "koki@mail.com",
                password = "123"
            )
        )
    )

    private var nextRecipeId = 4

    private val _recipes = MutableStateFlow(
        listOf(
            RecipeItem(
                id = 1,
                title = "Nasi Goreng Kampung Pedas",
                category = "Utama",
                cookTimeMinutes = 15,
                ingredients = "1 piring nasi dingin, 2 butir telur, 3 bawang merah, 2 bawang putih, 5 cabai rawit, kecap manis, garam.",
                steps = "1. Haluskan bumbu dan cabai.\n2. Tumis bumbu hingga harum, masukkan telur dan orak-arik.\n3. Masukkan nasi dan kecap, aduk rata hingga matang."
            ),
            RecipeItem(
                id = 2,
                title = "Ayam Kecap Bawang Bombay",
                category = "Utama",
                cookTimeMinutes = 30,
                ingredients = "500 gr potongan ayam, 1 bawang bombay, 3 siung bawang putih, 4 sdm kecap manis, lada, garam, air secukupnya.",
                steps = "1. Tumis bawang putih dan bawang bombay hingga layu.\n2. Masukkan ayam hingga berubah warna.\n3. Tambahkan kecap, air, garam, dan lada. Masak hingga meresap."
            ),
            RecipeItem(
                id = 3,
                title = "Es Teh Lemon Segar",
                category = "Minuman",
                cookTimeMinutes = 5,
                ingredients = "2 kantong teh hitam, 200 ml air panas, 1/2 buah lemon, 2 sdm gula atau madu, es batu.",
                steps = "1. Seduh teh dengan air panas selama 3 menit lalu larutkan gula.\n2. Peras lemon ke dalam larutan teh.\n3. Tuang ke gelas berisi es batu dan sajikan."
            )
        )
    )

    val recipesFlow: StateFlow<List<RecipeItem>> = _recipes.asStateFlow()

    // READ: Mengambil seluruh daftar resep (sering diuji dosen saat UTS)
    fun getAllRecipes(): List<RecipeItem> {
        return _recipes.value
    }

    // CREATE: Menambah resep baru
    fun addRecipe(
        title: String,
        category: String,
        cookTimeMinutes: Int,
        ingredients: String,
        steps: String
    ) {
        val newItem = RecipeItem(
            id = nextRecipeId++,
            title = title.trim(),
            category = category.trim().ifEmpty { "Utama" },
            cookTimeMinutes = cookTimeMinutes.coerceAtLeast(1),
            ingredients = ingredients.trim(),
            steps = steps.trim()
        )
        _recipes.update { current -> listOf(newItem) + current }
    }

    // UPDATE: Mengubah resep yang sudah ada berdasarkan ID
    fun updateRecipe(updated: RecipeItem) {
        _recipes.update { current ->
            current.map { item -> if (item.id == updated.id) updated else item }
        }
    }

    // DELETE: Menghapus resep berdasarkan ID
    fun deleteRecipe(id: Int) {
        _recipes.update { current ->
            current.filterNot { it.id == id }
        }
    }

    // REGISTER: Mendaftarkan akun baru secara lokal
    fun registerUser(fullName: String, email: String, password: String): Result<Unit> {
        val cleanEmail = email.trim().lowercase()
        if (fullName.isBlank() || cleanEmail.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Semua kolom wajib diisi."))
        }
        val exists = _users.value.any { it.email.equals(cleanEmail, ignoreCase = true) }
        if (exists) {
            return Result.failure(IllegalArgumentException("Email sudah terdaftar."))
        }
        _users.update { current ->
            current + UserAccount(
                fullName = fullName.trim(),
                email = cleanEmail,
                password = password
            )
        }
        return Result.success(Unit)
    }

    // LOGIN: Memeriksa kecocokan email & password
    fun loginUser(email: String, password: String): Result<UserAccount> {
        val cleanEmail = email.trim().lowercase()
        val found = _users.value.firstOrNull {
            it.email.equals(cleanEmail, ignoreCase = true) && it.password == password
        }
        return if (found != null) {
            Result.success(found)
        } else {
            Result.failure(IllegalArgumentException("Email atau kata sandi salah."))
        }
    }
}