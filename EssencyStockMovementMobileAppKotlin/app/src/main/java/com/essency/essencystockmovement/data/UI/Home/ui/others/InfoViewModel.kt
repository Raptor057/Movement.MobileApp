package com.essency.essencystockmovement.data.UI.Home.ui.others

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class InfoViewModel(app: Application) : AndroidViewModel(app) {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    init {
        val pm = app.packageManager
        val pInfo = pm.getPackageInfo(app.packageName, 0)
        val versionName = pInfo.versionName ?: "?"
        val versionCode = if (Build.VERSION.SDK_INT >= 28) {
            pInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION") pInfo.versionCode.toLong()
        }
        _text.value = "Versión: $versionName ($versionCode)"
    }
}
