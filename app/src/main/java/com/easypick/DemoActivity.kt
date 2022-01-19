package com.easypick

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.easypick.databinding.ActivityDemoBinding
import com.easypick.models.EasyPick

class DemoActivity : AppCompatActivity() {
    private var binding: ActivityDemoBinding? = null
    private var imageList: ArrayList<String>? = null
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                imageList = it.data?.getStringArrayListExtra("imageList")
                Log.i("Main", "Images List $imageList")
            }
        }

        binding?.button?.setOnClickListener {
            val easyPick = EasyPick(this, 6, activityResultLauncher!!)
            easyPick.launch()
        }
    }
}