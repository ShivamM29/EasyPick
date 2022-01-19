package com.easypick.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.easypick.R
import com.easypick.databinding.ImageLayoutBinding
import com.easypick.models.ImageData
import com.easypick.utilities.ImagesDiffUtils

class ImageRecyclerView(private val context:Context, private val isMultiple: LiveData<Boolean>, val onClickEvent: ClickEvent): RecyclerView.Adapter<ImageRecyclerView.MyViewHolder>(){
    var oldList = ArrayList<ImageData>()
    val selectedImageList = ArrayList<ImageData>()
    var singleImageSelected: ImageData? = null
    var lastImageSelectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = ImageLayoutBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        isMultiple.observe((context as LifecycleOwner), {
            if(it){
                holder.binding.multipleSelectorCard.visibility = View.VISIBLE
            }else{
                holder.binding.multipleSelectorCard.visibility = View.GONE
            }
        })

        if (!(context as Activity).isFinishing){
            Glide.with(context)
                .load(oldList[position].imageUri)
                .into(holder.binding.imageView)
        }

        if (selectedImageList.contains(oldList[position])){
            holder.textView.visibility = View.VISIBLE
            holder.textView.text = (selectedImageList.indexOf(oldList[position]) + 1).toString()
        }else{
            holder.textView.text = ""
            holder.textView.visibility = View.GONE
        }

        if (oldList[position] == singleImageSelected){
            holder.binding.bluredView.visibility = View.VISIBLE
        }else{
            holder.binding.bluredView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return oldList.size
    }

    inner class MyViewHolder(val binding: ImageLayoutBinding): RecyclerView.ViewHolder(binding.root){
        val textView = binding.root.findViewById<TextView>(R.id.selectedNumberTextView)!!

        init {
            binding.root.setOnClickListener {
                singleImageSelected = oldList[adapterPosition]
                onClickEvent.onImageClicked(oldList[adapterPosition], adapterPosition)
            }
        }
    }

    fun submitData(newList: ArrayList<ImageData>){
        val diffUtil = ImagesDiffUtils(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    interface ClickEvent{
        fun onImageClicked(imageData: ImageData, position: Int)
    }
}