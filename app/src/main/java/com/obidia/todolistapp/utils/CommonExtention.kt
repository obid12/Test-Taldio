package com.obidia.todolistapp.utils

import android.view.View

fun Boolean?.replaceIfNull(replace: Boolean = false): Boolean {
    return this ?: replace
}

fun String?.replaceIfNull(replace: String = ""): String {
    return this ?: replace
}

fun Int?.replaceIfNull(replace: Int = 0): Int {
    return this ?: replace
}

fun Long?.replaceIfNull(replace: Long = 0): Long {
    return this ?: replace
}

fun View.visible(isVisible: Boolean, isInVisible: Boolean = false) {
    if (isVisible) {
        this.visibility = View.VISIBLE
        return
    }

    if (isInVisible) {
        this.visibility = View.INVISIBLE
        return
    }

    this.visibility = View.GONE
}