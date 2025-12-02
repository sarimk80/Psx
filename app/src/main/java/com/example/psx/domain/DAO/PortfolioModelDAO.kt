package com.example.psx.domain.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.psx.domain.model.PortfolioModel
import kotlinx.coroutines.flow.Flow


@Dao
interface PortfolioModelDAO {

    @Query("SELECT * FROM portfoliomodel")
    fun getAllData(): Flow<List<PortfolioModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolio(portfolioModel: PortfolioModel)

    @Delete
    suspend fun deleteModel(portfolioModel: PortfolioModel)

}