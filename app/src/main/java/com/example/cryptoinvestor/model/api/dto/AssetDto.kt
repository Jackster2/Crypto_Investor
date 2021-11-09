package com.example.cryptoinvestor.model.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssetDto(
    val id: String,
    val name: String,
    @Json(name = "priceUsd")
    val price: Float,
    @Json(name = "volumeUsd24Hr")
    val volume24Hr: Float,
    @Json(name = "changePercent24Hr")
    val change24Hr: Float,
)

fun AssetDto.toModel(): AssetDto =
    AssetDto(id, name, price, volume24Hr, change24Hr)
