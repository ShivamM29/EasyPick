package com.easypick.utilities

import androidx.recyclerview.widget.DiffUtil
import com.easypick.models.ImageData

class ImagesDiffUtils(private val oldList:ArrayList<ImageData>, private val newList:ArrayList<ImageData>): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].imageUri == newList[newItemPosition].imageUri
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            oldList[oldItemPosition].folderName != newList[newItemPosition].folderName -> false

            oldList[oldItemPosition].imageUri != newList[newItemPosition].imageUri -> false

//            oldList[oldItemPosition].isSelected != newList[newItemPosition].isSelected -> false

            else -> true
        }
    }
}