package com.easypick.models

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.easypick.views.MainActivity

class EasyPick(private val context: Context, private val limit: Int, private val activityResultLauncher: ActivityResultLauncher<Intent>) {
    fun launch(){
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("limit", limit)

        activityResultLauncher.launch(intent)
    }
}
