package com.example.psx.di

import com.example.psx.data.network.dot.api.StockApi
import com.example.psx.data.network.repo.StockRepository
import com.example.psx.domain.repo.StockRepo
import com.example.psx.domain.usecase.GainersUseCase
import com.example.psx.domain.usecase.SectorUseCase
import com.google.gson.internal.GsonBuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providerStockApi():StockApi{
        return Retrofit.Builder()
            .baseUrl("https://psxterminal.com/")
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