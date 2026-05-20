package com.darius.relay_vpn.domain

import com.darius.relay_vpn.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipesRepo {
    suspend fun getRecipes(): Flow<List<Recipe>>
    suspend fun getRecipe(id: Int): Recipe
}
