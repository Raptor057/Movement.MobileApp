package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UsersViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Users Settings Fragment"
    }
    val text: LiveData<String> = _text
}