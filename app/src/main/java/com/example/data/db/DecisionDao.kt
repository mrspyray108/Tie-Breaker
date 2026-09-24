package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DecisionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DecisionDao {
    @Query("SELECT * FROM decisions ORDER BY createdAt DESC")
    fun getAllDecisions(): Flow<List<DecisionEntity>>

    @Query("SELECT * FROM decisions WHERE id = :id")
    suspend fun getDecisionById(id: Long): DecisionEntity?

    @Query("SELECT * FROM decisions WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteDecisions(): Flow<List<DecisionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecision(decision: DecisionEntity): Long

    @Update
    suspend fun updateDecision(decision: DecisionEntity)

    @Delete
    suspend fun deleteDecision(decision: DecisionEntity)

    @Query("DELETE FROM decisions WHERE id = :id")
    suspend fun deleteDecisionById(id: Long)
}
