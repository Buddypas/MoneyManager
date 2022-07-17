package com.inFlow.moneyManager.db.entities

import androidx.room.*

@Entity(
    tableName = "wallets", indices = [Index(
        value = ["walletName"],
        unique = true
    )]
)

data class Wallet(
    @PrimaryKey(autoGenerate = true) val walletId: Int,
    val walletName: String
)

@Dao
interface WalletsDao {
    @Query("SELECT * FROM wallets")
    fun getAll(): List<Wallet>

    @Query("SELECT * FROM wallets WHERE walletId=:id")
    fun getById(id: Int): Wallet

    @Query("SELECT * FROM wallets WHERE walletName=:name")
    fun getByName(name: String): Wallet

    @Update
    fun updateWallets(vararg wallets: Wallet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg wallets: Wallet)

    @Delete
    fun delete(wallet: Wallet)
}