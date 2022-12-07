package com.imaginato.homeworkmvvm.data.remote.login

import android.accounts.NetworkErrorException
import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.imaginato.homeworkmvvm.ui.base.IApp
import com.imaginato.homeworkmvvm.ui.util.isNetworkAvailable
import okhttp3.Interceptor
import okhttp3.Response


class NetworkErrorInterceptor constructor(
    private val noNetworkMessage: String,
    private val gson: Gson,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkAvailable()) {
            val errorBody = ErrorResponse()
            errorBody.code = 300
            errorBody.message = noNetworkMessage
            throw LoginHttpException.NetWorkExceptionMaximon(errorBody)
        }
        val request =
            chain.request().newBuilder()
                .header("IMSI", "357175048449937")
                .header("IMEI", "510110406068589")
                .build()


        val response = request.let { chain.proceed(it) }
            ?: throw NetworkErrorException("Null response")

        return if (response.isSuccessful) {
            response
        } else {
            //Handle error structure for http request
            var errorBody: ErrorResponse
            try {
                errorBody = gson.fromJson(response.body?.string(), ErrorResponse::class.java)
                errorBody.code = response.code
            } catch (exception: JsonSyntaxException) {
                errorBody = ErrorResponse()
                errorBody.code = response.code
                when {
                    (response.code == LoginHttpException.CODE_UNAUTHORIZED) ||
                            (response.code == LoginHttpException.CODE_AUTHORIZED_TIMEOUT) -> {
                        errorBody.statusType = UNAUTHORIZED
                    }
                    TextUtils.isEmpty(response.message) -> {
                        errorBody.statusType = UNKNOWN_ERROR
                    }
                    else -> {
                        errorBody.statusType = response.message
                    }
                }
            } catch (exception: Exception) {
                errorBody = ErrorResponse()
                errorBody.code = response.code
                when {
                    (response.code == LoginHttpException.CODE_UNAUTHORIZED) ||
                            (response.code == LoginHttpException.CODE_AUTHORIZED_TIMEOUT) -> {
                        errorBody.message = UNAUTHORIZED
                    }
                    TextUtils.isEmpty(response.message) -> {
                        errorBody.message = UNKNOWN_ERROR
                    }
                    else -> {
                        errorBody.message = response.message
                    }
                }
            }
            throw createMaximonException(response.code, errorBody)
        }
    }

    private fun createMaximonException(code: Int, errorBody: ErrorResponse): LoginHttpException {
        return when (code) {
            LoginHttpException.CODE_BAD_REQUEST -> LoginHttpException.BadRequestExceptionMaximon(
                errorBody
            )
            LoginHttpException.CODE_NOT_FOUND -> LoginHttpException.NotFoundExceptionMaximon(
                errorBody
            )
            LoginHttpException.CODE_UNAUTHORIZED -> LoginHttpException.UnauthorizedExceptionMaximon(
                errorBody
            )
            LoginHttpException.CODE_AUTHORIZED_TIMEOUT -> LoginHttpException.UnauthorizedExceptionMaximon(
                errorBody
            )
            LoginHttpException.CODE_FORBIDDEN -> LoginHttpException.ForbiddenExceptionMaximon(
                errorBody
            )
            LoginHttpException.CODE_UNPROCESS_ABLE -> LoginHttpException.UnprocessedAbleExceptionMaximon(
                errorBody
            )
            LoginHttpException.CODE_SERVER_ERROR -> LoginHttpException.ServerExceptionMaximon(
                errorBody
            )
            else -> LoginHttpException(errorBody)
        }
    }

    companion object {
        const val UNKNOWN_ERROR = "Something went wrong"
        const val UNAUTHORIZED = "Unauthorized"
    }
}