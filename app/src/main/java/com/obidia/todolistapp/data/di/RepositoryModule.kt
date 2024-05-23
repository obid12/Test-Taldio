package com.obidia.todolistapp.data.di

import com.obidia.todolistapp.data.repository.NoteRepositoryImplementation
import com.obidia.todolistapp.domain.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [DatabaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideRepository(
        repositoryImplementation: NoteRepositoryImplementation
    ): NoteRepository
}