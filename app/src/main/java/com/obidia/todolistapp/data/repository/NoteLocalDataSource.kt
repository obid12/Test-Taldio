package com.obidia.todolistapp.data.repository

import com.obidia.todolistapp.data.local.entity.NoteEntity
import com.obidia.todolistapp.data.local.room.NoteDao
import com.obidia.todolistapp.domain.model.NoteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteLocalDataSource @Inject constructor(
    private val noteDao: NoteDao
) {
    suspend fun addNote(data: NoteModel): Long {
        return noteDao.addNote(NoteEntity.transform(data))
    }

    fun updateNote(data: NoteModel) {
        noteDao.updateNote(NoteEntity.transform(data))
    }

    fun deleteNote(data: NoteModel) {
        noteDao.deleteNote(NoteEntity.transform(data))
    }

    fun deleteListNote(list: ArrayList<NoteModel>) {
        noteDao.deleteListNote(NoteEntity.transformList(list))
    }

    fun getAllNotes(isFinish: Boolean): Flow<ArrayList<NoteModel>> = noteDao.getAllNotes(isFinish)
        .map {
            NoteEntity.transform(it)
        }.flowOn(Dispatchers.IO)

    fun getSearchNotes(char: String): Flow<ArrayList<NoteModel>> = noteDao.getSearchNotes(char)
        .map {
            NoteEntity.transform(it)
        }.flowOn(Dispatchers.IO)
}