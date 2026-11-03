package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DoppelkopfDao {
    @Query("SELECT * FROM game_sessions ORDER BY updatedAt DESC")
    fun getAllSessionsFlow(): Flow<List<GameSessionEntity>>

    @Query("SELECT * FROM game_sessions WHERE sessionId = :sessionId LIMIT 1")
    fun getSessionFlow(sessionId: Long): Flow<GameSessionEntity?>

    @Query("SELECT * FROM game_sessions WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getSession(sessionId: Long): GameSessionEntity?

    @Query("SELECT * FROM game_sessions ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatestSession(): GameSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GameSessionEntity): Long

    @Update
    suspend fun updateSession(session: GameSessionEntity)

    @Query("DELETE FROM game_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)

    @Query("SELECT * FROM game_rounds WHERE sessionId = :sessionId ORDER BY roundNumber ASC")
    fun getRoundsFlow(sessionId: Long): Flow<List<RoundEntity>>

    @Query("SELECT * FROM game_rounds WHERE sessionId = :sessionId ORDER BY roundNumber DESC LIMIT 1")
    suspend fun getLastRound(sessionId: Long): RoundEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: RoundEntity): Long

    @Query("DELETE FROM game_rounds WHERE id = :id")
    suspend fun deleteRoundById(id: Long)

    @Query("DELETE FROM game_rounds WHERE sessionId = :sessionId")
    suspend fun clearRoundsForSession(sessionId: Long)
}
