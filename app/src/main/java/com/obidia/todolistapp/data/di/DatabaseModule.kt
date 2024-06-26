package com.obidia.todolistapp.data.di

import android.content.Context
import androidx.room.Room
import com.obidia.todolistapp.data.local.room.NoteDao
import com.obidia.todolistapp.data.local.room.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(
        @ApplicationContext context: Context
    ): NoteDatabase = Room.databaseBuilder(
        context,
        NoteDatabase::class.java, "note_database"
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideNoteDao(database: NoteDatabase): NoteDao = database.noteDao()
}