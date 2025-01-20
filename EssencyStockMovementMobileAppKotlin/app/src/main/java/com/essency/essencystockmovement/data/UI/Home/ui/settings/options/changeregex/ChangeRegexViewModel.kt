package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changeregex

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChangeRegexViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Change Regex Fragment"
    }
    val text: LiveData<String> = _text
}