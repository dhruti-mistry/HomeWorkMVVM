package com.imaginato.homeworkmvvm.data.remote.login

import com.imaginato.homeworkmvvm.data.remote.login.request.LoginRequest
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class LoginDataRepository @Inject constructor(
    private var api: LoginApi
)  : LoginRepository() {
    suspend fun doLogin(loginRequest: LoginRequest) : Response<LoginResponse?> =
        api.doLogin(loginRequest)



}