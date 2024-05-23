package com.obidia.todolistapp.presentation.list.viewholder

import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.obidia.todolistapp.databinding.ItemNoteBinding
import com.obidia.todolistapp.domain.model.NoteModel
import com.obidia.todolistapp.utils.replaceIfNull
import com.obidia.todolistapp.utils.visible

class NoteListViewHolder(
    private val binding: ItemNoteBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        data: NoteModel,
        onClickItem: ((data: NoteModel, binding: ItemNoteBinding) -> Unit)?,
        onCheckBoxListener: ((item: NoteModel?, position: Int) -> Unit)?,
        onLongClickItem: ((data: NoteModel, isChecked: Boolean) -> Unit)?
    ) {
        binding.run {
            root.let { card ->
                card.isChecked = false
                card.setOnLongClickListener {
                    card.isChecked = !card.isChecked
                    onLongClickItem?.invoke(data, card.isChecked)
                    true
                }
                card.setOnClickListener {
                    if (card.isChecked) {
                        card.isChecked = !card.isChecked
                        onLongClickItem?.invoke(data, card.isChecked)
                        return@setOnClickListener
                    }
                    onClickItem?.invoke(data, binding)
                }
            }

            btnCheckbox.let { check ->
                check.setOnClickListener {
                    data.isFinish = !data.isFinish.replaceIfNull()
                    onCheckBoxListener?.invoke(data, adapterPosition)
                }
                check.isChecked = data.isFinish.replaceIfNull()
            }

            tvTitle.let {
                it.text = data.title
                it.paintFlags =
                    if (data.isFinish.replaceIfNull())
                        Paint.STRIKE_THRU_TEXT_FLAG
                    else Paint.ANTI_ALIAS_FLAG
            }

            tvDate.let {
                it.text = data.date
                it.visible(!data.isFinish.replaceIfNull())
            }

            tvTime.let {
                it.text = if (data.isFinish.replaceIfNull()) "Complete" else data.time
            }
        }
    }
}