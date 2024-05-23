package com.obidia.todolistapp.domain.repository

import com.obidia.todolistapp.domain.model.NoteModel
import com.obidia.todolistapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    suspend fun addNote(data: NoteModel): Long

    fun updateNote(data: NoteModel)

    fun deleteNote(data: NoteModel)

    fun getAllNotes(isFinish: Boolean): Flow<Resource<ArrayList<NoteModel>>>

    fun getSearchNotes(char: String): Flow<Resource<ArrayList<NoteModel>>>

    fun deleteListNote(list: ArrayList<NoteModel>)
}