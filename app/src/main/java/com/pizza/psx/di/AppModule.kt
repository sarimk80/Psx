package com.pizza.psx.di

import com.pizza.psx.data.network.dot.api.StockApi
import com.pizza.psx.data.network.repo.StockRepository
import com.pizza.psx.domain.repo.StockRepo
import com.pizza.psx.domain.usecase.GainersUseCase
import com.pizza.psx.domain.usecase.SectorUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    val client = OkHttpClient.Builder()
        .connectTimeout(50, TimeUnit.SECONDS) // Set connection timeout to 30 seconds
        .readTimeout(40, TimeUnit.SECONDS)    // Set read timeout to 20 seconds
        .writeTimeout(40, TimeUnit.SECONDS)   // Set write timeout to 25 seconds
        .callTimeout(60, TimeUnit.SECONDS)    // Set overall call timeout to 40 seconds
        .build()

    @Provides
    @Singleton
    fun providerStockApi():StockApi{
        return Retrofit.Builder()
            .baseUrl("https://psxterminal.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StockApi::class.java)
    }

    @Provides
    @Singleton
    fun providerStockRepo(api:StockApi):StockRepo{
        return  StockRepository(api)
    }

    fun getGainer(repo:StockRepo): GainersUseCase {
        return  GainersUseCase(repo)
    }

    fun getSector(repo:StockRepo):SectorUseCase{
        return  SectorUseCase(repo)
    }


}