package com.essency.essencystockmovement.data.UI.Home.ui.reporting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReportingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Section Historique"
    }
    val text: LiveData<String> = _text
}