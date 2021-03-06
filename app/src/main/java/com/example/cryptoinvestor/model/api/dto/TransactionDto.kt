package com.example.cryptoinvestor.model.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

/*
    Data klasse som beskriver opbyggelsen af en transaction
 */

@JsonClass(generateAdapter = true)
data class TransactionDto(
    @Json(name = "Action")
    val action: String,

    @Json(name= "Currency_Name")
    val Currency_Name: String,

    @Json(name = "symbol")
    val symbol: String,

    @Json(name = "Price")
    val price: Float,

    @Json(name = "Quantity")
    val quantity: Float,

    @Json(name = "Time")
    val Time: Date
)

fun TransactionDto.toModel(): TransactionDto =
    TransactionDto(action, Currency_Name, symbol, price, quantity, Time)

