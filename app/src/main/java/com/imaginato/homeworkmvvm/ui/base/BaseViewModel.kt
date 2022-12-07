package com.imaginato.homeworkmvvm.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imaginato.homeworkmvvm.exts.LOG_TYPE_INFO
import com.imaginato.homeworkmvvm.exts.printLog


abstract class BaseViewModel : ViewModel() {
    val mProgress : MutableLiveData<String?> = MutableLiveData()
    val mError : MutableLiveData<String?> = MutableLiveData()
    val isUnauthorized : MutableLiveData<Boolean> = MutableLiveData()
    init {
        javaClass.simpleName.printLog(LOG_TYPE_INFO, "created")
    }

    override fun onCleared() {
        super.onCleared()
        javaClass.simpleName.printLog(LOG_TYPE_INFO, "destroyed")
    }
}