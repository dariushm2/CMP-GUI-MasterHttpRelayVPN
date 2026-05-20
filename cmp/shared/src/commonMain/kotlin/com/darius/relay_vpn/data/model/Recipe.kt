package com.darius.relay_vpn.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("ingredients")
    val ingredients: List<String>,
    @SerialName("instructions")
    val instructions: List<String>,
)
