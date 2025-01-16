package com.essency.essencystockmovement.data.UI.Home.ui.others

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InfoViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Info Fragment"
    }
    val text: LiveData<String> = _text
}