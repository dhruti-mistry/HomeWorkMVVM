package com.imaginato.homeworkmvvm.ui.login

import androidx.lifecycle.MutableLiveData
import com.imaginato.homeworkmvvm.R
import com.imaginato.homeworkmvvm.data.local.login.User
import com.imaginato.homeworkmvvm.data.local.login.UserDao
import com.imaginato.homeworkmvvm.data.remote.login.LoginDataRepository
import com.imaginato.homeworkmvvm.data.remote.login.request.LoginRequest
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse
import com.imaginato.homeworkmvvm.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginDataRepository, private val userDao: UserDao) : BaseViewModel()  {
    var resultLiveData: MutableLiveData<LoginResponse> = MutableLiveData()
    var uiMessage: MutableLiveData<Int> = MutableLiveData(-1)

    private var loginRequest = LoginRequest()

    fun isDataValid(username: String, password: String):Boolean {
        if (username.isEmpty()) {
            uiMessage.postValue(R.string.add_username)
            return false
        }
        if (password.isEmpty()) {
            uiMessage.postValue(R.string.add_password)
            return false
        }

        if (password.length<5){
            uiMessage.postValue(R.string.invalid_password)
            return false
        }
        return true
    }

    fun doLogin(username: String,password: String) {
        mProgress.postValue("Loading")
        loginRequest.userName = username
        loginRequest.password = password

        repository.doLogin(loginRequest).enqueue(object : Callback<LoginResponse?> {

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                mProgress.postValue("")

                mError.postValue(t.localizedMessage?.toString())
            }

            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                resultLiveData.value = response.body()
                val user = response.body()?.data?.isDeleted?.let { isDeleted->
                    User(response.body()?.data?.userId.toString(),
                        response.headers()["x-acc"] ?: "",
                        response.body()?.data?.userName.toString(),
                        isDeleted
                    )
                }

                user?.let { it1 -> userDao.insertUser(it1) }

                mProgress.postValue("")
            }
        })
    }


}
