package com.imaginato.homeworkmvvm.data.remote.login

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.imaginato.homeworkmvvm.BuildConfig
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
            throw TaskHttpException.NetWorkException(errorBody)
        }
        val request =
            chain.request().newBuilder()
                .header("IMSI", BuildConfig.IMSI)
                .header("IMEI", BuildConfig.IMEI)
                .build()


        val response = request.let { chain.proceed(it) }

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
                    (response.code == TaskHttpException.CODE_UNAUTHORIZED) ||
                            (response.code == TaskHttpException.CODE_AUTHORIZED_TIMEOUT) -> {
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
                    (response.code == TaskHttpException.CODE_UNAUTHORIZED) ||
                            (response.code == TaskHttpException.CODE_AUTHORIZED_TIMEOUT) -> {
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
            throw createException(response.code, errorBody)
        }
    }

    private fun createException(code: Int, errorBody: ErrorResponse): TaskHttpException {
        return when (code) {
            TaskHttpException.CODE_BAD_REQUEST -> TaskHttpException.BadRequestException(
                errorBody
            )
            TaskHttpException.CODE_NOT_FOUND -> TaskHttpException.NotFoundException(
                errorBody
            )
            TaskHttpException.CODE_UNAUTHORIZED -> TaskHttpException.UnauthorizedException(
                errorBody
            )
            TaskHttpException.CODE_AUTHORIZED_TIMEOUT -> TaskHttpException.UnauthorizedException(
                errorBody
            )
            TaskHttpException.CODE_FORBIDDEN -> TaskHttpException.ForbiddenException(
                errorBody
            )
            TaskHttpException.CODE_UNPROCESS_ABLE -> TaskHttpException.UnprocessedAbleException(
                errorBody
            )
            TaskHttpException.CODE_SERVER_ERROR -> TaskHttpException.ServerException(
                errorBody
            )
            else -> TaskHttpException(errorBody)
        }
    }

    companion object {
        const val UNKNOWN_ERROR = "Something went wrong"
        const val UNAUTHORIZED = "Unauthorized"
    }
}