package com.obidia.todolistapp.presentation.search

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
class SearchNoteViewModel @Inject constructor(
    private val useCase: NoteUseCase
) : ViewModel() {
    fun getSearchNote(char: String): Flow<Resource<ArrayList<NoteModel>>> =
        useCase.getSearchNotes(char)

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