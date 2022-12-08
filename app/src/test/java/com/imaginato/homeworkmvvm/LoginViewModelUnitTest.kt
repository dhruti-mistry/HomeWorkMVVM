package com.imaginato.homeworkmvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.imaginato.homeworkmvvm.data.local.login.UserDao
import com.imaginato.homeworkmvvm.data.remote.login.LoginApi
import com.imaginato.homeworkmvvm.data.remote.login.LoginDataRepository
import com.imaginato.homeworkmvvm.data.remote.login.response.Data
import com.imaginato.homeworkmvvm.data.remote.login.response.LoginResponse
import com.imaginato.homeworkmvvm.ui.login.LoginViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class LoginViewModelUnitTest {

    private var loginApi: LoginApi = mock {}
    private var userDao: UserDao = mock {}
    private lateinit var repository: LoginDataRepository
    private lateinit var viewModel: LoginViewModel
    private var loginResponse: retrofit2.Call<LoginResponse?> = mock {}

    @Before
    fun setUp() {
        repository = LoginDataRepository(loginApi)
        viewModel = LoginViewModel(repository, userDao)
    }

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `login with valid username should set loginResult as success`() {
        Mockito.doAnswer {
            val callback : Callback<LoginResponse?> = it?.getArgument(
                0
            )!!
            callback.onResponse(loginResponse, Response.success(LoginResponse(data = Data())))

        }.`when`(loginResponse).enqueue(any())

        whenever(loginApi.doLogin(any())).doReturn(loginResponse)
        viewModel.doLogin("", "")

        assert(viewModel.resultLiveData.getOrAwaitValue() != null)
    }

    @Test
    fun `doLogin viewModel method test with error`() {
        Mockito.doAnswer {
            val callback : Callback<LoginResponse?> = it?.getArgument(
                0
            )!!
            callback.onFailure(loginResponse, Exception("Error"))

        }.`when`(loginResponse).enqueue(any())

        whenever(loginApi.doLogin(any())).doReturn(loginResponse)
        viewModel.doLogin("", "")

        assert(viewModel.mError.getOrAwaitValue() == "Error")
    }

    @Test
    fun `username is empty`() {
        viewModel.isDataValid("","")
        assert(viewModel.uiMessage.getOrAwaitValue() == R.string.add_username)
    }

    @Test
    fun `username is not empty`() {
        viewModel.isDataValid("123","")
        assert(viewModel.uiMessage.getOrAwaitValue() != R.string.add_username)
    }

    @Test
    fun `password is empty`() {
        viewModel.isDataValid("1234","")
        assert(viewModel.uiMessage.getOrAwaitValue() == R.string.add_password)
    }

    @Test
    fun `password is not empty`() {
        viewModel.isDataValid("1234","123")
        assert(viewModel.uiMessage.getOrAwaitValue() != R.string.add_password)
    }

    @Test
    fun `password length must be less then 5`() {
        viewModel.isDataValid("1234","123")
        assert(viewModel.uiMessage.getOrAwaitValue() == R.string.invalid_password)
    }

    @Test
    fun `password length must be grater then 5`() {
        viewModel.isDataValid("1234","123574")
        assert(viewModel.uiMessage.getOrAwaitValue() != R.string.invalid_password)
    }
}


fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}