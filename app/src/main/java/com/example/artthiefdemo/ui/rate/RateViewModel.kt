package com.example.artthiefdemo.ui.rate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RateViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is rate art Fragment"
    }
    val text: LiveData<String> = _text
}