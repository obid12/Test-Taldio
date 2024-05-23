package com.obidia.todolistapp.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.obidia.todolistapp.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(data: NoteEntity): Long

    @Update
    fun updateNote(data: NoteEntity)

    @Delete
    fun deleteNote(data: NoteEntity)

    @Query("SELECT * FROM note_table where isFinish = :isFinish")
    fun getAllNotes(isFinish: Boolean): Flow<List<NoteEntity>>

    @Query("SELECT * FROM note_table where title LIKE '%' || :text || '%' ")
    fun getSearchNotes(text: String): Flow<List<NoteEntity>>

    @Delete
    fun deleteListNote(list: List<NoteEntity>)
}