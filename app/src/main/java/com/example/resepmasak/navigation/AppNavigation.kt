package com.example.resepmasak.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.resepmasak.data.RecipeItem
import com.example.resepmasak.ui.screen.DetailScreen
import com.example.resepmasak.ui.screen.HomeScreen
import com.example.resepmasak.ui.screen.LoginScreen
import com.example.resepmasak.ui.screen.RecipeFormScreen
import com.example.resepmasak.ui.screen.RegisterScreen
import com.example.resepmasak.viewmodel.RecipeViewModel

private object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val DETAIL = "detail/{recipeId}"
    const val FORM = "form?recipeId={recipeId}"

    fun detailRoute(recipeId: Int) = "detail/$recipeId"
    fun formRoute(recipeId: Int? = null) =
        if (recipeId != null) "form?recipeId=$recipeId" else "form"
}

@Composable
fun AppNavigation(
    recipeViewModel: RecipeViewModel = viewModel()
) {
    val navController = rememberNavController()
    val currentUser by recipeViewModel.currentUser.collectAsState()
    val recipes by recipeViewModel.filteredRecipes.collectAsState()
    val searchQuery by recipeViewModel.searchQuery.collectAsState()
    val selectedCategory by recipeViewModel.selectedCategory.collectAsState()
    val authMessage by recipeViewModel.authMessage.collectAsState()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN,
        enterTransition = {
            slideInHorizontally(
                animationSpec = tween(320),
                initialOffsetX = { fullWidth -> fullWidth / 4 }
            ) + fadeIn(animationSpec = tween(320))
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(260),
                targetOffsetX = { fullWidth -> -fullWidth / 6 }
            ) + fadeOut(animationSpec = tween(260))
        },
        popEnterTransition = {
            slideInHorizontally(
                animationSpec = tween(320),
                initialOffsetX = { fullWidth -> -fullWidth / 4 }
            ) + fadeIn(animationSpec = tween(320))
        },
        popExitTransition = {
            slideOutHorizontally(
                animationSpec = tween(260),
                targetOffsetX = { fullWidth -> fullWidth / 4 }
            ) + fadeOut(animationSpec = tween(260))
        }
    ) {
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                errorOrInfoMessage = authMessage,
                onClearMessage = { recipeViewModel.clearAuthMessage() },
                onLoginSubmit = { email, password ->
                    recipeViewModel.login(email, password) {
                        navController.navigate(AppRoutes.HOME) {
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    recipeViewModel.clearAuthMessage()
                    navController.navigate(AppRoutes.REGISTER)
                }
            )
        }

        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                errorMessage = authMessage,
                onClearMessage = { recipeViewModel.clearAuthMessage() },
                onRegisterSubmit = { fullName, email, password ->
                    recipeViewModel.register(fullName, email, password) {
                        navController.popBackStack()
                    }
                },
                onBackToLogin = {
                    recipeViewModel.clearAuthMessage()
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.HOME) {
            HomeScreen(
                userName = currentUser?.fullName ?: "Chef",
                recipes = recipes,
                categories = recipeViewModel.categories,
                searchQuery = searchQuery,
                selectedCategory = selectedCategory,
                onSearchChange = { recipeViewModel.updateSearchQuery(it) },
                onCategorySelect = { recipeViewModel.selectCategory(it) },
                onRecipeClick = { recipeId ->
                    navController.navigate(AppRoutes.detailRoute(recipeId))
                },
                onAddRecipeClick = {
                    navController.navigate(AppRoutes.formRoute())
                },
                onDeleteRecipeClick = { recipeId ->
                    recipeViewModel.deleteRecipe(recipeId)
                },
                onLogoutClick = {
                    recipeViewModel.logout {
                        navController.navigate(AppRoutes.LOGIN) {
                            popUpTo(AppRoutes.HOME) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = AppRoutes.DETAIL,
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: -1
            val recipe = recipes.firstOrNull { it.id == recipeId }
                ?: recipeViewModel.getRecipeById(recipeId)

            DetailScreen(
                recipe = recipe,
                onBack = { navController.popBackStack() },
                onEditClick = { id ->
                    navController.navigate(AppRoutes.formRoute(id))
                },
                onDeleteClick = { id ->
                    recipeViewModel.deleteRecipe(id)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = AppRoutes.FORM,
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: -1
            val existingRecipe = if (recipeId != -1) {
                recipes.firstOrNull { it.id == recipeId }
                    ?: recipeViewModel.getRecipeById(recipeId)
            } else {
                null
            }

            RecipeFormScreen(
                existingRecipe = existingRecipe,
                selectableCategories = recipeViewModel.categories,
                onBack = { navController.popBackStack() },
                onSaveRecipe = { title, category, cookTimeMinutes, ingredients, steps ->
                    if (existingRecipe != null) {
                        recipeViewModel.updateRecipe(
                            RecipeItem(
                                id = existingRecipe.id,
                                title = title,
                                category = category,
                                cookTimeMinutes = cookTimeMinutes,
                                ingredients = ingredients,
                                steps = steps
                            )
                        )
                    } else {
                        recipeViewModel.addRecipe(
                            title = title,
                            category = category,
                            cookTimeMinutes = cookTimeMinutes,
                            ingredients = ingredients,
                            steps = steps
                        )
                    }
                    navController.popBackStack()
                }
            )
        }
    }
}