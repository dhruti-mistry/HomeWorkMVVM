package com.imaginato.homeworkmvvm.data.remote.login

import com.imaginato.homeworkmvvm.data.remote.login.request.LoginRequest
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse
import retrofit2.Call
import javax.inject.Inject

class LoginDataRepository @Inject constructor(
    private var api: LoginApi
) {
    fun doLogin(loginRequest: LoginRequest) : Call<LoginResponse?> {
        return api.doLogin(loginRequest)
    }
}