package com.obidia.todolistapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteModel(
    val id: Int,
    var title: String?,
    var date: String?,
    var time: String?,
    var isFinish: Boolean?
) : Parcelable
