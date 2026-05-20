package com.darius.relay_vpn.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Recipes(
    @SerialName("recipes")
    val recipes: List<Recipe>,
)
