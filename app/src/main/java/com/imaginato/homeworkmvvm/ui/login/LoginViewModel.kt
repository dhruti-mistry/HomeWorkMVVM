package com.imaginato.homeworkmvvm.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.imaginato.homeworkmvvm.data.remote.login.LoginDataRepository
import com.imaginato.homeworkmvvm.data.remote.login.LoginHttpException
import com.imaginato.homeworkmvvm.data.remote.login.request.LoginRequest
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse
import com.imaginato.homeworkmvvm.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginDataRepository) : BaseViewModel()  {
    var resultLiveData: MutableLiveData<LoginResponse> = MutableLiveData()
    private var _progress: MutableLiveData<Boolean> = MutableLiveData()
    private var loginRequest = LoginRequest()


    val progress: LiveData<Boolean>
        get() { return _progress }





    fun doLogin(username: String,password: String) {
        loginRequest.userName = username
        loginRequest.password = password
        repository.IOExecutor({
            repository.doLogin(loginRequest)
        }, {
            _progress.value = true
        }, {
            it?.let { loginResponse ->
                _progress.value = false
                resultLiveData.value = loginResponse
            }
        }, {
            _progress.value = false
            mError.value = (it as? LoginHttpException)?.getErrorMessage().toString()
        }, {
            _progress.value = false
            isUnauthorized.value = it
        })
    }


    fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    fun isPasswordValid(password: String): Boolean {
        if (password.length < 5) return false

        return true
    }
}