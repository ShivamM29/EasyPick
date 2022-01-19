# EasyPick
Image Picker

> Step 1. Add the JitPack repository to your build file

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  > Step 2. Add the dependency
    
    dependencies {
            implementation 'com.github.ShivamM29:EasyPick:1.0.0'
    }
    
  > Step 3. Initialize EasyPick object (in Kotlin)
  
     val easyPick = EasyPick(context,
       noOfImages, 
       activityResultLauncher!!)
       
  > Step 4. Launch the EasyPick object
  
    easyPick.launch()
    
  > Step 5. Get the images from ActivityResultLauncher
  
    activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                // Here you got the images list
                val imageList: ArrayList<String>? = it.data?.getStringArrayListExtra("imageList")
            }
        }
  

[![](https://jitpack.io/v/ShivamM29/EasyPick.svg)](https://jitpack.io/#ShivamM29/EasyPick)
