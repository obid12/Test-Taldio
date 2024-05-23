package com.obidia.todolistapp.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.obidia.todolistapp.domain.model.NoteModel
import com.obidia.todolistapp.utils.replaceIfNull
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "note_table")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String?,
    val date: String?,
    val time: String?,
    val isFinish: Boolean?
) : Parcelable {

    companion object {
        fun transform(list: List<NoteEntity>): ArrayList<NoteModel> {
            return ArrayList(list.map {
                NoteModel(
                    it.id.replaceIfNull(),
                    it.title.replaceIfNull(),
                    it.date.replaceIfNull(),
                    it.time.replaceIfNull(),
                    it.isFinish.replaceIfNull()
                )
            })
        }

        fun transformList(list: List<NoteModel>): ArrayList<NoteEntity> {
            return ArrayList(list.map {
                NoteEntity(
                    it.id.replaceIfNull(),
                    it.title.replaceIfNull(),
                    it.date.replaceIfNull(),
                    it.time.replaceIfNull(),
                    it.isFinish.replaceIfNull()
                )
            })
        }

        fun transform(model: NoteModel): NoteEntity =
            NoteEntity(
                model.id,
                model.title,
                model.date,
                model.time.replaceIfNull(),
                model.isFinish
            )
    }
}
