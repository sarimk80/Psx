package com.pizza.psx.domain.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class PortfolioModel(
    @PrimaryKey()
    val symbol: String,
    val volume: Int = 1,
)

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = PortfolioModel::class,
            parentColumns = ["symbol"],
            childColumns = ["portfolioSymbol"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("portfolioSymbol")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val portfolioSymbol: String, // Foreign key
    val date: Long?,
    val price: Double?,
    val volume: Int?
)

data class PortfolioWithTransactions(
    @Embedded
    val portfolio: PortfolioModel,
    @Relation(
        parentColumn = "symbol",
        entityColumn = "portfolioSymbol",
        entity = Transaction::class
    )
    val transactions: List<Transaction>
)
