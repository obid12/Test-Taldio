package com.obidia.todolistapp.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obidia.todolistapp.domain.model.NoteModel
import com.obidia.todolistapp.domain.usecase.NoteUseCase
import com.obidia.todolistapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val useCase: NoteUseCase
) : ViewModel() {
    fun getAllNote(isFinish: Boolean): Flow<Resource<ArrayList<NoteModel>>> =
        useCase.getAllNotes(isFinish)

    fun deleteNote(data: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.deleteNote(data)
        }
    }

    fun addNote(data: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.addNote(data)
        }
    }

    fun updateNote(data: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.updateNote(data)
        }
    }

    fun deleteListNote(list: ArrayList<NoteModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.deleteListNote(list)
        }
    }
}