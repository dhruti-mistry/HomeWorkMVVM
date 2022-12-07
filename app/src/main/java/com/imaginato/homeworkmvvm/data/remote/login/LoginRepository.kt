package com.imaginato.homeworkmvvm.data.remote.login

import android.util.Log
import kotlinx.coroutines.*
import retrofit2.Response

abstract class LoginRepository {

    private val job = Job()
    private val mainScope = CoroutineScope(Dispatchers.Main + job)
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    fun <T> IOExecutor(
        onBackground: suspend () -> Response<T?>,
        onLoading: (Boolean) -> Unit,
        onSuccess: (T?) -> Unit,
        onError: (Exception) -> Unit,
        isUnauthorized: (Boolean) -> Unit,
    ) {
        try {
            onLoading(true)
            GlobalScope.launch(Dispatchers.IO) {
                mainScope.launch {
                    try {
                        val response = onBackground()
                        onSuccess(response.body())
                    } catch (e: Exception) {
                        Log.e("!_@_Error : ", e.toString())
                        try {
                            if (e is IllegalArgumentException) {
                                onError(e)
                            } else if ((e is LoginHttpException) && (e.isUnauthorizedException()))
                                isUnauthorized(true)
                            else
                                onError(e)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    onLoading(false)
                }
            }
        } catch (e: Exception) {
            onLoading(false)
            if ((e as LoginHttpException).isUnauthorizedException())
                isUnauthorized(true)
            else
                onError(e)
        }
    }

    fun onClear() {
        job.cancelChildren()
    }
}