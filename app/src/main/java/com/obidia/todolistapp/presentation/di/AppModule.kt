package com.obidia.todolistapp.presentation.di

import com.obidia.todolistapp.domain.usecase.NoteUseCase
import com.obidia.todolistapp.domain.usecase.NoteUseCaseImplementation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AppModule {
  @Binds
  @ViewModelScoped
  abstract fun provideUseCase(useCaseImplementation: NoteUseCaseImplementation): NoteUseCase
}