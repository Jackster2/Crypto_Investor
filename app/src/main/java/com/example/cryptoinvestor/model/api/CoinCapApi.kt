package com.example.cryptoinvestor.model.api

import com.example.cryptoinvestor.model.api.dto.AssetDto
import com.example.cryptoinvestor.model.api.dto.AssetHistoryDTO
import com.example.cryptoinvestor.view.activities.MainActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import timber.log.Timber

interface CoinCapApi {
    @GET("rates")
    fun getRates(): Call<List<AssetDto>>

    @GET("rates/{id}")
    fun getRateAsString(@Path("id") id: String): Call<String>

    @GET("v2/assets/{id}")
    suspend fun getAsset(@Path("id") id: String): Response<AssetDto>

    /*
    Den her skal hente historikken for en asset
     */
    @GET("v2/assets/{id}/history?interval=m1")
    suspend fun getAssetHistory(@Path("id") id: String): Response<List<AssetHistoryDTO>>

    @GET("v2/assets/?limit=100")
    suspend fun getHundredAssets(): Response<List<AssetDto>>

    companion object {
        fun build(): CoinCapApi =
            Retrofit.Builder()
                //tilføjelse af v2 virker ikke her?
                .baseUrl("https://api.coincap.io/")
                .client(OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        chain.request().newBuilder()
                            .addHeader(
                                "Authorization",
                                "Bearer 2df561e8-7ba7-4ea3-85bf-f7a2b620d19b"
                            )
                            .build()
                            .let { chain.proceed(it) }
                    }
                    .addInterceptor(
                        HttpLoggingInterceptor({ Timber.d(it) })
                            .also { it.level = HttpLoggingInterceptor.Level.BODY }
                    )
                    .build()
                )
                .addConverterFactory(WrapperConverter())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create()
    }
}