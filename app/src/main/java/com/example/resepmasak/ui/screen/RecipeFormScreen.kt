package com.example.resepmasak.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.resepmasak.data.RecipeItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeFormScreen(
    existingRecipe: RecipeItem?,
    selectableCategories: List<String>,
    onBack: () -> Unit,
    onSaveRecipe: (
        title: String,
        category: String,
        cookTimeMinutes: Int,
        ingredients: String,
        steps: String
    ) -> Unit
) {
    val isEditMode = existingRecipe != null
    val categoryChoices = selectableCategories.filterNot { it == "Semua" }

    var title by remember(existingRecipe?.id) {
        mutableStateOf(existingRecipe?.title.orEmpty())
    }
    var category by remember(existingRecipe?.id) {
        mutableStateOf(existingRecipe?.category ?: categoryChoices.firstOrNull() ?: "Utama")
    }
    var cookTimeText by remember(existingRecipe?.id) {
        mutableStateOf((existingRecipe?.cookTimeMinutes ?: 15).toString())
    }
    var ingredients by remember(existingRecipe?.id) {
        mutableStateOf(existingRecipe?.ingredients.orEmpty())
    }
    var steps by remember(existingRecipe?.id) {
        mutableStateOf(existingRecipe?.steps.orEmpty())
    }
    var formError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Resep" else "Tambah Resep Baru",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    formError = null
                },
                label = { Text("Nama Masakan") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Kategori",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categoryChoices, key = { it }) { itemCategory ->
                        val selected = category == itemCategory
                        FilterChip(
                            selected = selected,
                            onClick = { category = itemCategory },
                            label = { Text(itemCategory) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            OutlinedTextField(
                value = cookTimeText,
                onValueChange = { input ->
                    cookTimeText = input.filter { it.isDigit() }
                    formError = null
                },
                label = { Text("Estimasi Waktu Masak (menit)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ingredients,
                onValueChange = {
                    ingredients = it
                    formError = null
                },
                label = { Text("Bahan-bahan") },
                minLines = 3,
                maxLines = 6,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = steps,
                onValueChange = {
                    steps = it
                    formError = null
                },
                label = { Text("Langkah Pembuatan") },
                minLines = 4,
                maxLines = 8,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(visible = formError != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = formError.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    val minutes = cookTimeText.toIntOrNull() ?: 0
                    if (title.isBlank() || ingredients.isBlank() || steps.isBlank()) {
                        formError = "Nama masakan, bahan, dan langkah wajib diisi."
                        return@Button
                    }
                    if (minutes <= 0) {
                        formError = "Waktu masak minimal 1 menit."
                        return@Button
                    }
                    onSaveRecipe(title, category, minutes, ingredients, steps)
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (isEditMode) "Simpan Perubahan" else "Simpan Resep",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}