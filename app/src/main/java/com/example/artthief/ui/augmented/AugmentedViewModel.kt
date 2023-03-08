package com.example.artthief.ui.augmented

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AugmentedViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is augmented reality Fragment"
    }
    val text: LiveData<String> = _text
}
