package com.darius.relay_vpn.domain

import androidx.compose.ui.util.fastJoinToString
import com.darius.relay_vpn.data.ApiService
import com.darius.relay_vpn.data.model.Recipes
import com.darius.relay_vpn.domain.model.Recipe
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.darius.relay_vpn.data.model.Recipe as NetworkRecipe

class KtorRecipeRepo(
    private val apiService: ApiService,
) : RecipesRepo {

    override suspend fun getRecipes(): Flow<List<Recipe>> =
        flowOf(
            apiService
                .query("recipes")
                .body<Recipes>()
                .recipes
                .map { it.toDomainModel() }
        )

    override suspend fun getRecipe(id: Int): Recipe =
        apiService
            .query("recipes")
            .body<Recipes>()
            .recipes
            .find { it.id == id }
            ?.toDomainModel() ?: throw IllegalArgumentException("Recipe not found")

    private fun NetworkRecipe.toDomainModel() =
        Recipe(
            id = id,
            name = name,
            ingredients = ingredients.fastJoinToString(),
            instructions = instructions.fastJoinToString(),
        )
}
