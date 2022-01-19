package com.easypick.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypick.models.ImageData
import com.easypick.repositories.GalleryRepo
import kotlinx.coroutines.launch

class GalleryViewModel: ViewModel() {
    var mutableImagesData: MutableLiveData<HashMap<String, ArrayList<ImageData>>> = MutableLiveData()
    private var galleryRepo = GalleryRepo()

    fun getImageData(context: Context){
        viewModelScope.launch {
            mutableImagesData = galleryRepo.getImagesData(context) as MutableLiveData
        }
    }


}