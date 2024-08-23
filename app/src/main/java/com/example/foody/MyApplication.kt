package com.example.foody

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // Components regarding to hilt will be created here and the annotation is important
class MyApplication: Application() {

}