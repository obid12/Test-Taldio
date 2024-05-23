package com.obidia.todolistapp.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obidia.todolistapp.domain.model.NoteModel
import com.obidia.todolistapp.domain.usecase.NoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val useCase: NoteUseCase
): ViewModel() {

    suspend fun addNote(data: NoteModel): Long {
        return useCase.addNote(data)
    }

    fun updateNote(data: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.updateNote(data)
        }
    }

    fun deleteNote(data: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.deleteNote(data)
        }
    }
}