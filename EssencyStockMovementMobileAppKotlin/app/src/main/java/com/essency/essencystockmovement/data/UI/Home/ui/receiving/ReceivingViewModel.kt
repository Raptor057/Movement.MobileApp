package com.essency.essencystockmovement.data.UI.Home.ui.receiving

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReceivingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Receiving Fragment"
    }
    val text: LiveData<String> = _text
}