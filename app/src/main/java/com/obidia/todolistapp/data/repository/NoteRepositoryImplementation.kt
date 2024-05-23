package com.obidia.todolistapp.data.repository

import com.obidia.todolistapp.domain.model.NoteModel
import com.obidia.todolistapp.domain.repository.NoteRepository
import com.obidia.todolistapp.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class NoteRepositoryImplementation @Inject constructor(
    private val localDataSource: NoteLocalDataSource
) : NoteRepository {

    override suspend fun addNote(data: NoteModel): Long {
        return localDataSource.addNote(data)
    }

    override fun updateNote(data: NoteModel) {
        localDataSource.updateNote(data)
    }

    override fun deleteNote(data: NoteModel) {
        localDataSource.deleteNote(data)
    }

    override fun getAllNotes(isFinish: Boolean): Flow<Resource<ArrayList<NoteModel>>> {
        return flow {
            val data = localDataSource.getAllNotes(isFinish)
            delay(500)
            data.onStart {
                emit(Resource.Loading)
            }.catch {
                emit(Resource.Error(it))
            }.collect {
                emit(Resource.Success(it))
            }
        }
    }

    override fun getSearchNotes(char: String): Flow<Resource<ArrayList<NoteModel>>> {
        return flow {
            delay(500)
            val data = localDataSource.getSearchNotes(char)
            data.onStart {
                emit(Resource.Loading)
            }.catch {
                emit(Resource.Error(it))
            }.collect {
                emit(Resource.Success(it))
            }
        }
    }

    override fun deleteListNote(list: ArrayList<NoteModel>) {
        localDataSource.deleteListNote(list)
    }


}