package com.essency.essencystockmovement.data.UI.Home.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Section Paramètres"
    }
    val text: LiveData<String> = _text
}