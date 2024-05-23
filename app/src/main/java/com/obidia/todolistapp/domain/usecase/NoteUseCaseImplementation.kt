package com.obidia.todolistapp.domain.usecase

import com.obidia.todolistapp.domain.model.NoteModel
import com.obidia.todolistapp.domain.repository.NoteRepository
import com.obidia.todolistapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteUseCaseImplementation @Inject constructor(
    private val repository: NoteRepository
) : NoteUseCase {

    override suspend fun addNote(data: NoteModel): Long {
        return repository.addNote(data)
    }

    override fun updateNote(data: NoteModel) {
        repository.updateNote(data)
    }

    override fun deleteNote(data: NoteModel) {
        repository.deleteNote(data)
    }

    override fun getAllNotes(isFinish: Boolean): Flow<Resource<ArrayList<NoteModel>>> {
        return repository.getAllNotes(isFinish)
    }

    override fun getSearchNotes(char: String): Flow<Resource<ArrayList<NoteModel>>> {
        return repository.getSearchNotes(char)
    }

    override fun deleteListNote(list: ArrayList<NoteModel>) {
        repository.deleteListNote(list)
    }


}