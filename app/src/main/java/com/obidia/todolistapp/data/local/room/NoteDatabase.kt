package com.obidia.todolistapp.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.obidia.todolistapp.data.local.entity.NoteEntity

@Database(entities = [NoteEntity::class], version = 3, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
  abstract fun noteDao(): NoteDao
}