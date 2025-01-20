package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changesendingemail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChangeSendingEmailViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Change Email Fragment"
    }
    val text: LiveData<String> = _text
}