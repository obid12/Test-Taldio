package com.obidia.todolistapp.presentation.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.obidia.todolistapp.databinding.ItemNoteBinding
import com.obidia.todolistapp.domain.model.NoteModel
import com.obidia.todolistapp.presentation.list.viewholder.NoteListViewHolder

class NoteListAdapter : ListAdapter<NoteModel, NoteListViewHolder>(DiffCallBack) {

    private var onClickItem: ((data: NoteModel, binding: ItemNoteBinding) -> Unit)? = null

    private var onCheckBoxListener: ((item: NoteModel?, position: Int) -> Unit)? = null

    private var onLongClickItem: ((data: NoteModel, isChecked: Boolean) -> Unit)? = null

    fun setOnClickItem(listener: ((data: NoteModel, binding: ItemNoteBinding) -> Unit)?) {
        onClickItem = listener
    }

    fun setOnCheckBoxListener(listener: ((item: NoteModel?, position: Int) -> Unit)?) {
        onCheckBoxListener = listener
    }

    fun setOnLongClickIte(listener: ((data: NoteModel, isChecked: Boolean) -> Unit)?) {
        onLongClickItem = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListViewHolder {
        return NoteListViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteListViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data, onClickItem, onCheckBoxListener, onLongClickItem)
    }

    object DiffCallBack : DiffUtil.ItemCallback<NoteModel>() {
        override fun areItemsTheSame(
            oldItem: NoteModel,
            newItem: NoteModel
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: NoteModel,
            newItem: NoteModel
        ): Boolean = oldItem.hashCode() == newItem.hashCode()
    }
}