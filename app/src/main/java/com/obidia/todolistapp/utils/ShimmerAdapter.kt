package com.obidia.todolistapp.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

class ShimmerAdapter(
    @LayoutRes val layout: Int,
    private val shownItem: Int
) : RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShimmerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ShimmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShimmerViewHolder, position: Int) {
        // no implementation
    }

    override fun getItemCount(): Int = shownItem

    inner class ShimmerViewHolder(view: View) : RecyclerView.ViewHolder(view)
}