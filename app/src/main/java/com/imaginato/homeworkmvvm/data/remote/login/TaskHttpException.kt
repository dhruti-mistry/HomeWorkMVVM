package com.imaginato.homeworkmvvm.data.remote.login

import java.io.IOException


open class TaskHttpException(private val response: ErrorResponse) : IOException() {

    companion object {
        const val CODE_BAD_REQUEST = 400
        const val CODE_UNAUTHORIZED = 401
        const val CODE_AUTHORIZED_TIMEOUT = 419
        const val CODE_FORBIDDEN = 403
        const val CODE_NOT_FOUND = 404
        const val CODE_UNPROCESS_ABLE = 422
        const val CODE_SERVER_ERROR = 500
    }

    class BadRequestException(response: ErrorResponse) : TaskHttpException(response)
    class NotFoundException(response: ErrorResponse) : TaskHttpException(response)
    class ForbiddenException(response: ErrorResponse) : TaskHttpException(response)
    class NetWorkException(response: ErrorResponse) : TaskHttpException(response)
    class ServerException(response: ErrorResponse) : TaskHttpException(response)
    class UnauthorizedException(response: ErrorResponse) : TaskHttpException(response)
    class UnprocessedAbleException(response: ErrorResponse) : TaskHttpException(response)

    fun getErrorMessage(): String {
        return if (response.code > 0) {
            response.message?: NetworkErrorInterceptor.UNKNOWN_ERROR
        } else {
            NetworkErrorInterceptor.UNKNOWN_ERROR
        }
    }

    fun isUnauthorizedException(): Boolean {
        return this is UnauthorizedException
    }
}