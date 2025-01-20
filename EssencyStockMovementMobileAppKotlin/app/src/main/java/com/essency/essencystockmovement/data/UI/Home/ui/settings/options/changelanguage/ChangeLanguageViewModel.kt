package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changelanguage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChangeLanguageViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is ChangeLanguage Fragment"
    }
    val text: LiveData<String> = _text
}