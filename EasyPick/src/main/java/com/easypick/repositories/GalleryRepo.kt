package com.easypick.repositories

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.easypick.models.ImageData

class GalleryRepo {
    private var mutableImagesData: MutableLiveData<HashMap<String, ArrayList<ImageData>>> = MutableLiveData()

    suspend fun getImagesData(context: Context): LiveData<HashMap<String, ArrayList<ImageData>>>{
        loadData(context)
        return mutableImagesData
    }

    private suspend fun loadData(context: Context){
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media._ID)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            val folderIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val folderList = ArrayList<String>()
            val imageDataList = ArrayList<ImageData>()

            while (it.moveToNext()){
                val imageData = ImageData()
                val id = it.getLong(idIndex)
                imageData.imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageData.folderName = it.getString(folderIndex)

                imageDataList.add(imageData)
                if (!folderList.contains(imageData.folderName)){
                    folderList.add(imageData.folderName)
                }
            }

            val mapData = HashMap<String, ArrayList<ImageData>>()
            mapData["All"] = imageDataList

            for (folderName in folderList){
                val tempList = ArrayList<ImageData>()
                for (data in imageDataList){
                    if (data.folderName == folderName){
                        tempList.add(data)
                    }
                }
                mapData[folderName] = tempList
            }

            mutableImagesData.value = mapData

        }
        cursor?.close()
    }

}