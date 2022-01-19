package com.easypick.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.easypick.R
import com.easypick.adapters.ImageRecyclerView
import com.easypick.databinding.ActivityMainBinding
import com.easypick.models.ImageData
import com.easypick.viewmodels.GalleryViewModel

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, ImageRecyclerView.ClickEvent{
    private var permissionResult: ActivityResultLauncher<String>? = null
    private var galleryViewModel: GalleryViewModel? = null
    private var binding: ActivityMainBinding? = null
    private var imageAdapter: ImageRecyclerView? = null
    private var imagesMap = HashMap<String, ArrayList<ImageData>>()
    private var folderList = ArrayList<String>()
    private var folderImages = ArrayList<ImageData>()
    private var isMultiple = MutableLiveData(false)
    private var positionList = ArrayList<Int>()
    private var limitedImageNumber = 5
    private var folderWisePositionMap = HashMap<String, ArrayList<Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val bundle = Intent().extras
        bundle?.getInt("limit")?.let {
            limitedImageNumber = it
        }

        galleryViewModel = ViewModelProvider(this)[GalleryViewModel::class.java]
        permissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                openGallery()
            }else{
                finish()
            }
        }

        binding?.folderSelector?.onItemSelectedListener = this

        binding?.multiSelectBtn?.setOnClickListener {
            isMultiple.value = if (isMultiple.value!!){
                imageAdapter?.selectedImageList?.clear()
                for (pos in positionList){
                    imageAdapter?.notifyItemChanged(pos)
                }

                folderWisePositionMap.clear()
                binding?.fabDoneButton!!.setImageResource(R.drawable.done_icon)
                binding?.multiSelectBtn!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.select_deactive_btn_background))

                false
            }else{
                binding?.multiSelectBtn!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.select_active_btn_background))
                imageAdapter?.lastImageSelectedPosition = 0
                imageAdapter?.selectedImageList?.add(imageAdapter?.oldList!![imageAdapter?.lastImageSelectedPosition!!])
                positionList.add(imageAdapter?.lastImageSelectedPosition!!)
                imageAdapter?.singleImageSelected = imageAdapter?.oldList!![0]
                imageAdapter?.notifyItemChanged(imageAdapter?.lastImageSelectedPosition!!)  // this will update the last blurred image
                imageAdapter?.notifyItemChanged(positionList[0])
                true
            }
        }

        binding?.fabDoneButton!!.setOnClickListener{
            val listOfStringUri = ArrayList<String>()
            if (isMultiple.value!!){
                for (imageData in imageAdapter?.selectedImageList!!){
                    listOfStringUri.add(imageData.imageUri!!.toString())
                }
            }else{
                listOfStringUri.add(imageAdapter?.singleImageSelected?.imageUri!!.toString())
            }

            val intent = Intent()
            intent.putStringArrayListExtra("imageList", listOfStringUri)
            setResult(RESULT_OK, intent)
            finish()
        }

        checkPermissionAndProceed()
    }

    private fun checkPermissionAndProceed(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionResult?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }else{
            openGallery()
        }
    }

    private fun openGallery() {
        galleryViewModel?.getImageData(this)
        galleryViewModel?.mutableImagesData?.observe(this, {
            it?.let {
                imagesMap = it

                for (data in it){
                    folderList.add(data.key)
                }
                folderList.sort()

                val arrayAdapter = ArrayAdapter(this, R.layout.spinner_text, folderList)
                arrayAdapter.setDropDownViewResource(R.layout.dropdown_layout)
                binding?.folderSelector?.adapter = arrayAdapter
                folderWisePositionMap[folderList[0]] = ArrayList()
                positionList = folderWisePositionMap[folderList[0]]!!

                imageAdapter = ImageRecyclerView(this, isMultiple, this)
                binding?.recyclerView!!.adapter = imageAdapter

                folderImages = it["All"]!!
                imageAdapter?.singleImageSelected = folderImages[0]
                imageAdapter?.submitData(folderImages)

                if(folderImages.size >= 1) {
                    loadImage(folderImages[0].imageUri!!)
                }

            }
        })
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val folderName = p0?.getItemAtPosition(p2).toString()
        folderImages = imagesMap[folderName]!!

        if (!folderWisePositionMap.contains(folderName)){
            folderWisePositionMap[folderName] = ArrayList()
        }
        positionList = folderWisePositionMap[folderName]!!

        imageAdapter?.submitData(folderImages)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onImageClicked(imageData: ImageData, position:Int) {
        imageAdapter?.notifyItemChanged(imageAdapter?.lastImageSelectedPosition!!)
        imageAdapter?.lastImageSelectedPosition = position

        loadImage(imageData.imageUri!!)

        if (isMultiple.value!!) {
            var isEqual = false
            for(item in imageAdapter?.selectedImageList!!){
                if (imageData.imageUri == item.imageUri){
                    isEqual = true
                    break
                }else{
                    isEqual = false
                }
            }

            if (isEqual){
                imageAdapter?.selectedImageList!!.remove(imageData)

                for (pos in positionList){
                    imageAdapter?.notifyItemChanged(pos)
                }
                positionList.remove(position)

            }else{
                if (imageAdapter?.selectedImageList!!.size < limitedImageNumber){
                    imageAdapter?.selectedImageList!!.add(imageData)
                    positionList.add(position)
                }else{
                    val toast = Toast.makeText(this, "Max limit is $limitedImageNumber", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
                imageAdapter?.notifyItemChanged(position)
            }

            if (imageAdapter?.selectedImageList!!.size>1){
                binding?.fabDoneButton!!.setImageResource(R.drawable.done_all_icon)
            }else{
                binding?.fabDoneButton!!.setImageResource(R.drawable.done_icon)
            }

        }else{
            imageAdapter?.notifyItemChanged(position)
        }
    }

    private fun loadImage(imageUri: Uri){
        Glide.with(this)
            .load(imageUri)
            .into(binding?.selectedImageView!!)
    }
}